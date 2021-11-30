from email.parser import Parser
import email.message
import poplib
from collections import namedtuple
import re


class EmailHandler:
    Header = namedtuple("Header", ["From", "To", "Subject"])
    Body = namedtuple("Body", ["Text", "Pdf"])
    Pdf = namedtuple("Pdf", ["FileName", "Data"])
    text = None
    pdf_file = None

    def __init__(self):
        self.gmail = poplib.POP3_SSL('pop.gmail.com', 995)

    def auth(self, email_: str, pwd: str) -> None:
        self.gmail.user(email_)
        self.gmail.pass_(pwd)

    def get_last_email(self) -> email.message.Message:
        ls = self.gmail.list()
        msg = self.gmail.retr(len(ls[1]))
        lines = msg[1]
        msg_content = b'\r\n'.join(lines).decode('utf-8')
        msg_ = Parser().parsestr(msg_content)
        return msg_

    @staticmethod
    def get_header(msg: email.message.Message) -> Header:
        head = EmailHandler.Header(*[msg.get(i, '') for i in EmailHandler.Header._fields])
        return head

    def parse_body(self, msg: email.message.Message) -> None:
        if msg.is_multipart():
            parts = msg.get_payload()
            for part in parts:
                if part.is_multipart():
                    self.parse_body(part)
                else:
                    self.parse_content(part)
        else:
            self.parse_content(msg)

    def parse_content(self, msg: email.message.Message) -> None:
        content_type = msg.get_content_type()
        if content_type == "text/plain":
            content = msg.get_payload(decode=True)
            charset = msg.get_charset()
            if charset is None:
                content_type = msg.get('Content-Type', '')
                result = re.search(r'charset="([A-Z0-9-]*?)"', content_type)
                if result:
                    charset = result.group(1)
            content = content.decode(charset)
            self.text = content

        elif content_type == "application/pdf":
            file_info = msg.get("Content-Disposition")
            filename_result = re.search(r'filename="(.*?)"', file_info)
            filename = filename_result.group(1)
            data = msg.get_payload(decode=True)
            pdf_file = EmailHandler.Pdf(FileName=filename, Data=data)
            self.pdf_file = pdf_file

    def get_data(self, msg) -> Body:
        self.parse_body(msg)
        return EmailHandler.Body(Text=self.text, Pdf=self.pdf_file)
