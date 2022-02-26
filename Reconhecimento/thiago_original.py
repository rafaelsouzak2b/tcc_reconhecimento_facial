import numpy as np
import cv2 as cv
import requests
from multiprocessing.context import Process
import json


def gravarRosto():
    name = input("Digite o nome:")

    file = open("base.txt", "a")

    print("Olhe para a camera e pressione 'q' para sair")

    openCamera()

    jsonResponse = enviarFoto()

    file.write("%s<!!>%s\n" % (name, jsonResponse[0]["faceId"]))

    print("Rosto Salvo com Sucesso")

    file.close()


def enviarFoto():
    url = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_04&returnRecognitionModel=false&detectionModel=detection_03&faceIdTimeToLive=86400"

    img = open("frame.jpg", "r")

    payload = img.buffer
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
        cv.imshow('frame', gray)

        cv.imwrite("frame.jpg", frame)

        if cv.waitKey(1) == ord('q'):
            break

        if cv.CAP_PROP_FPS % 5 == 0 and reconhecer == True:
            time += 1

        if time % 200 == 0 and reconhecer == True:
            jsonResponse = enviarFoto()
            # jsonResponse = [{'faceId': 'eb6cc668-6267-4bfa-99c3-e38bfc4442bc', 'faceRectangle': {'top': 71, 'left': 295, 'width': 178, 'height': 248}}]

            findSimilar(jsonResponse[0]["faceId"])

    cap.release()
    cv.destroyAllWindows()


def findSimilar(faceId):
    file = open("base.txt", "r")

    faceIds = []
    names = []

    lines = file.readlines()

    for line in lines:
        # print(line)

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

    # print('RESPOSTA ', jsonResponse)

    if len(jsonResponse) > 0:

        if jsonResponse[0]["confidence"] > 0.7:

            # print("AQUII -", faceIds)

            index = faceIds.index(jsonResponse[0]["faceId"])

            name = names[index]

            print(name)

        else:
            print("Nenhuma correspondência")
    else:
        print("Nenhuma correspondência")


if __name__ == "__main__":
    while True:

        op = int(
            input("------------------\nMenu:\n1 - Gravar Rosto\n2 - Reconhecer\n3- Sair\nDigite a opcao desejada: "))

        if op == 1:
            gravarRosto()
        elif op == 2:
            openCamera(True)
        elif op == 3:
            break
        else:
            print("Opção Inválida")

    exit()
