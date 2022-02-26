import numpy as np
import cv2 as cv
import requests
import json
import os

arqCasc = 'haarcascade_frontalface_default.xml'
faceCascade = cv.CascadeClassifier(arqCasc)

def gravarRosto():

    name = input("Digite o nome:")

    file = open("base.txt", "a")

    print("Olhe para a camera e pressione 'q' para sair")

    salva = openCamera()

    if salva:
        jsonResponse = enviarFoto()

        file.write("%s<!!>%s\n" %(name, jsonResponse[0]["faceId"]))

        print("Rosto Salvo com Sucesso")

    file.close()


def enviarFoto():
    url = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_04&returnRecognitionModel=false&detectionModel=detection_03&faceIdTimeToLive=86400"

    img = open("frame.jpg", "r")

    payload=img.buffer
    headers = {
        'Content-Type': 'application/octet-stream',
        'Ocp-Apim-Subscription-Key': ''
    }

    response = requests.request("POST", url, headers=headers, data=payload)
    jsonResponse = json.loads(response.text)

    img.close()

    return jsonResponse


def openCamera(reconhecer=None):
    cap = cv.VideoCapture(0)
    nome = ' '
    if not cap.isOpened():
        print("Cannot open camera")
        exit()

    time = 1

    while True:
        
        ret, frame = cap.read()
        
        if not ret:
            print("Can't receive frame (stream end?). Exiting ...")
            break
        
        gray = cv.cvtColor(frame, cv.COLOR_RGBA2RGB)

        imagem = cv.flip(gray, 180)

        faces = faceCascade.detectMultiScale(
            imagem,
            minNeighbors=5,
            minSize=(30, 30),
            maxSize=(200, 200)
        )

        for (x, y, w, h) in faces:
            cv.rectangle(imagem, (x, y), (x + w, y + h), (0, 255, 0), 2)


        if cv.waitKey(1) == ord('q'):
            if len(faces) > 0:
                retorno = True
            else:
                retorno = False
            break

        if cv.CAP_PROP_FPS % 5 == 0 and reconhecer==True:
            time += 1
        if len(faces) > 0:
            cv.imwrite("frame.jpg", imagem)
            if time % 500 == 0 and reconhecer==True:
                jsonResponse = enviarFoto()
                nome = findSimilar(jsonResponse[0]["faceId"])

            cv.putText(imagem, nome, (faces[0][0], faces[0][1] - 10), cv.FONT_HERSHEY_SIMPLEX, 0.9, (36, 255, 12), 2)
            if time % 400 == 0 and reconhecer==True:
                nome = ""
        cv.imshow('frame', imagem)
    cap.release()
    cv.destroyAllWindows()
    return retorno

def findSimilar(faceId):

    file = open("base.txt", "r")

    faceIds = []
    names = []

    lines = file.readlines()

    for line in lines:
        name, id = line.split('<!!>')

        faceIds.append(str(id).replace("\n", ""))
        names.append(name)

    url = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/findsimilars"

    payload = json.dumps({
    "faceId": faceId,
    "faceIds": faceIds,
    "maxNumOfCandidatesReturned": 10,
    "mode": "matchPerson"
    })
    headers = {
    'Content-Type': 'application/json',
    'Ocp-Apim-Subscription-Key': ''
    }

    response = requests.request("POST", url, headers=headers, data=payload)

    jsonResponse = json.loads(response.text)

    if response.status_code == 200 and len(jsonResponse) > 0:
        
        if jsonResponse[0]["confidence"] > 0.7:

            index = faceIds.index(jsonResponse[0]["faceId"])

            name = names[index]

            print(name)
            return name
        
        else:
            print("Nenhuma correspondência")
    else:    
        print("Nenhuma correspondência")



if __name__ == "__main__":
    while True:

        op = int(input("------------------\nMenu:\n1 - Gravar Rosto\n2 - Reconhecer\n3- Sair\nDigite a opcao desejada: "))

        if op == 1:
            gravarRosto()
        elif op == 2:
            openCamera(True)
        elif op == 3:
            if os.path.exists("frame.jpg"):
                os.remove("frame.jpg")
            break
        else:
            print("Opção Inválida")

    exit()







