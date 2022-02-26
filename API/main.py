from flask import Flask, request
##from flask_restx import Api, Resource
import json
from control.faceControl import FaceControl

app = Flask(__name__)

##api = Api(app, version='1.0', title='Sample Book API', description='A simple book API')

@app.route("/", methods=["GET"])
##class Defaut(Resource):
def main():
    ## retun json.dumps({"OK": True}), 200
    return json.dumps({"Result": "Hello World"}), 200
    ##return "Hello World", 200

@app.route("/file", methods=["POST"])
##class Defaut(Resource):
def file():
    photo = request.files["photo"]
    photo.save("foto/" + photo.filename)
    return json.dumps({"Result": photo.filename}), 200


@app.route("/photo/<name>", methods=["POST"])
##class EnviarFoto(Resource):
def send_photo(name):

    try:
        photo = request.files["photo"]

        image = photo.stream.read()

        photo.close()

        faceControl = FaceControl()

        id = faceControl.send_face(image)

        faceControl.save_face(id, name)
            
        return json.dumps({"Result": "Foto salva com sucesso"}), 201
        
    except:

        return json.dumps({"Result": "Erro ao enviar foto"}), 400


@app.route("/photo", methods=["POST"])
##class BuscaSimilar(Resource):
def find_similar_photo():

    try:

        photo = request.files["photo"]

        image = photo.stream.read()

        photo.close()

        face_control = FaceControl()

        name = face_control.find_similar(image=image)

        if name is not None:
            return json.dumps({"Result": name}), 200
        else:
            return json.dumps({"Result": "Rosto não identificado"}), 404

    except:

        return json.dumps({"Result": "Erro ao procurar rosto similar"}), 400


app.run("192.168.68.112")
##app.run("192.168.200.106")
##app.run("localhost")
##app.run()