import os
from threading import Thread
import readEmail_v2
from kafka import KafkaProducer
import re
from uuid import uuid4
from dotenv import load_dotenv

load_dotenv()


def download_pdf(pdf_data: bytes, path_: str, file_name: str):
    if pdf_data:
        dist = os.path.join(path_, file_name)
        if not dist.endswith(".pdf"):
            dist = f"{dist}.pdf"
        with open(dist, "wb") as f:
            f.write(data.Pdf.Data)
            f.flush()


handler = readEmail_v2.EmailClient()
handler.auth(os.getenv("MAIL"), os.getenv("PASS"))
msgs = handler.get_unseen_emails()
for msg in msgs:
    header = handler.get_header(msg)
    print(header)
    data = handler.get_data(msg)
    print(data.Text)
    user = re.search("(.*?) <", header.From).group(1).title()
    email = re.search("<(.*?)>", header.From).group(1)
    print(user)
    print(email)
    producer = KafkaProducer(bootstrap_servers=['127.0.0.1:9092'])
    pdf_bytes = data.Pdf
    uuid = uuid4()
    path = r"C:\Users\Radwan\Desktop\dd"
    thread = Thread(target=download_pdf, args=(pdf_bytes, path, uuid))
    thread.start()

    # if data.Pdf:
    #     path = fr"C:\Users\Radwan\Desktop\dd\{uuid4()}.pdf"
    #     with open(path, "wb") as f:
    #         f.write(data.Pdf.Data)
    #         f.flush()
    #

    value = f"""
            <?xml version = "1.0"?>
            <data>
                <file name="{data.Pdf.FileName}" url="http://localhost/{uuid}" />
                <user name="{user}" email="{email}" />
            </data>
            """

    producer.send("delta", key="cv", value=value.encode())
    producer.flush()
    print(data.Pdf.FileName)
