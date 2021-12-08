import os
import readEmail_v2
from kafka import KafkaProducer
import re
from uuid import uuid4
from dotenv import load_dotenv
from prefect import task, Flow
from prefect.executors import LocalDaskExecutor
from collections import namedtuple
from pymongo import MongoClient


load_dotenv()

DataRow = namedtuple("DataRow", ["username", "email", "receiving_date", "response_date", "uuid", "file_name"])


def download_pdf(pdf_data: bytes, path_: str, file_name: str):
    if pdf_data:
        dist = os.path.join(path_, file_name)
        if not dist.endswith(".pdf"):
            dist = f"{dist}.pdf"
        with open(dist, "wb") as f:
            f.write(pdf_data)
            f.flush()


@task()
def download(data):
    if data:
        path = os.getenv("PDF_PATH")
        file_name = data["row"].uuid
        print(file_name)
        pdf_data = data["pdf"]
        download_pdf(pdf_data, path, file_name)


@task()
def get_emails():
    handler = readEmail_v2.EmailClient()
    handler.auth(os.getenv("MAIL"), os.getenv("PASS"))
    emails_ = handler.get_unseen_emails()
    handler.close()
    return emails_


@task()
def get_emails_data(email):
    handler = readEmail_v2.EmailClient()
    data = handler.get_data(email)
    if data.Pdf:
        header = handler.get_header(email)
        user = re.search("(.*?) <", header.From).group(1).title()
        email = re.search("<(.*?)>", header.From).group(1)
        data_row = DataRow(user, email, header.Date, None, str(uuid4()), data.Pdf.FileName)
        return {
            "row": data_row,
            "pdf": data.Pdf.Data
        }
    return None


@task()
def to_kafka(data):
    if data:
        row = data["row"]
        uuid = row.location_url
        producer = KafkaProducer(bootstrap_servers=['127.0.0.1:9092'])
        value = f"""
                <?xml version = "1.0"?>
                <data>
                    <file name="{row.file_name}" uuid="{uuid}" />
                    <user name="{row.user}" email="{row.email}" />
                </data>
                """

        producer.send("delta", key="cv", value=value.encode())
        producer.flush()
        producer.close()


@task()
def to_database(data):
    rows = [d["row"]._asdict() for d in data if d is not None]
    host = os.getenv("DB_HOST", "")
    client = MongoClient(host)
    db_name = os.getenv("DB_NAME")
    if db_name is None:
        raise RuntimeError("DB_NAME environment variable is not set pls set it up")
    db = client[db_name]
    col = db["email_data"]
    col.insert_many(rows)


with Flow("ETL email pipeline") as flow:
    emails = get_emails()
    email_data = get_emails_data.map(emails)
    download.map(email_data)
    to_kafka.map(email_data)
    to_database(email_data)

flow.executor = LocalDaskExecutor()
flow.run()
# flow.visualize()
