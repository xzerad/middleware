from flask import Flask, send_from_directory

app = Flask(__name__)


@app.route('/<name>')
def get_file(name):
    # todo change path
    return send_from_directory(r"C:\Users\sonia bahri\Desktop\cv_dir", f"{name}.pdf")


if __name__ == '__main__':
    app.run(host="0.0.0.0")
