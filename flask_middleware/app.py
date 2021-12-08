from flask import Flask, send_from_directory

app = Flask(__name__)


@app.route('/<name>')
def hello_world(name):
    # todo change path
    return send_from_directory(r"C:\Users\Radwan\Desktop", name)


if __name__ == '__main__':
    app.run(host="0.0.0.0")
