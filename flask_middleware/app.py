from flask import Flask, send_from_directory
import os
from dotenv import load_dotenv

load_dotenv(r"C:\Users\Radwan\Desktop\cv_reader\middleware\email_reader\.env")
app = Flask(__name__)


@app.route('/<name>')
def get_file(name):
    # todo change path
    return send_from_directory(os.getenv("PDF_PATH"), f"{name}.pdf")


if __name__ == '__main__':
    app.run(host="0.0.0.0")
