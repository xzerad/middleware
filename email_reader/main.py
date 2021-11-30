import readEmail
from kafka import KafkaProducer
import re

handler = readEmail.EmailHandler()
handler.auth("", "")
msg = handler.get_last_email()
header = handler.get_header(msg)
print(header)
data = handler.get_data(msg)
print(data.Text)
user = re.search("(.*?) <", header.From).group(1)
email = re.search("<(.*?)>", header.From).group(1)
print(user.title())
print(email)
producer = KafkaProducer(bootstrap_servers=['127.0.0.1:9092'])

# if " cv" in str(data.Text).lower():
# print("cv found")
if data.Pdf:
    producer.send("delta", key=bytes(f'{user.title()}|{email}', encoding='utf-8'), value=data.Pdf.Data)
    producer.flush()
    print(data.Pdf.FileName)
# else:
#     print("it's not a cv !!")
