import email.message
from collections import namedtuple
from imaplib import IMAP4_SSL
import re
from typing import Optional, List


class EmailClient:
    Header = namedtuple("Header", ["From", "To", "Subject", "Date"])
    Body = namedtuple("Body", ["Text", "Pdf"])
    Pdf = namedtuple("Pdf", ["FileName", "Data"])
    text = None
    pdf_file = None

    def __init__(self):
        self.gmail = IMAP4_SSL("imap.gmail.com", 993)

    def auth(self, user: str, pwd: str) -> None:
        self.gmail.login(user, pwd)

    def get_unseen_emails(self, from_sender=None) -> Optional[List]:
        self.gmail.select("INBOX")
        if from_sender is not None:
            status, response = self.gmail.search(None, f'(FROM "{from_sender}")', '(UNSEEN)')
        else:
            status, response = self.gmail.search(None, '(UNSEEN)')

        if status == "OK":
            ls = response[0].split()
            data = []
            for email_id in ls:
                status, msg_ = self.gmail.fetch(email_id, '(RFC822)')
                body = msg_[0][-1].decode("utf-8")
                data.append(email.message_from_string(body))
            return data
        return None

    @staticmethod
    def get_header(msg_: email.message.Message) -> Header:
        head = EmailClient.Header(*[msg_.get(i, '') for i in EmailClient.Header._fields])
        return head

    def parse_body(self, msg_: email.message.Message) -> None:
        if msg_ is None:
            return None
        if msg_.is_multipart():
            parts = msg_.get_payload()
            for part in parts:
                if part.is_multipart():
                    self.parse_body(part)
                else:
                    self.parse_content(part)
        else:
            self.parse_content(msg_)

    def parse_content(self, msg_: email.message.Message) -> None:
        content_type = msg_.get_content_type()
        if content_type == "text/plain":
            content = msg_.get_payload(decode=True)
            charset = msg_.get_charset()
            if charset is None:
                content_type = msg_.get('Content-Type', '')
                result = re.search(r'charset="([A-Z0-9-]*?)"', content_type)
                if result:
                    charset = result.group(1)
            content = content.decode(charset)
            self.text = content

        elif content_type == "application/pdf":
            file_info = msg_.get("Content-Disposition")
            filename_result = re.search(r'filename="(.*?)"', file_info)
            filename = filename_result.group(1)
            data = msg_.get_payload(decode=True)
            pdf_file = EmailClient.Pdf(FileName=filename, Data=data)
            self.pdf_file = pdf_file

    def get_data(self, msg_) -> Body:
        self.parse_body(msg_)
        return EmailClient.Body(Text=self.text, Pdf=self.pdf_file)

    def close(self):
        self.gmail.close()
