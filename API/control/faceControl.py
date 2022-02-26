from model.face import Face
from control.dbControl import DbControl
from model.azure import Azure
from control.azureControl import AzureControl
import json

class FaceControl:

    def send_face(self, image):

        url = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_04&returnRecognitionModel=false&detectionModel=detection_03&faceIdTimeToLive=86400"
        headers = {
        'Content-Type': 'application/octet-stream',
        'Ocp-Apim-Subscription-Key': ''
        }

        azure = Azure(url=url, headers=headers, data=image)

        response, status_code = AzureControl().post(azure)

        return response[0]["faceId"]

    
    def find_similar(self, image):

        url = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/findsimilars"

        id = self.send_face(image)

        db = DbControl()
        ids = db.selectAllFacesIds()

        data = json.dumps({
            "faceId": id,
            "faceIds": ids,
            "maxNumOfCandidatesReturned": 10,
            "mode": "matchPerson"
            })

        headers = {
            'Content-Type': 'application/json',
            'Ocp-Apim-Subscription-Key': ''
        }

        azure = Azure(url=url, headers=headers, data=data)

        response, status_code = AzureControl().post(azure)

        if status_code == 200 and len(response):
        
            if response[0]["confidence"] > 0.7:

                faceId = response[0]["faceId"]

                name = db.selectFace(faceId)

                db.closeConnection()

                return name
            
            else:

                db.closeConnection()
                return None
        else:
            db.closeConnection()
            return None


    def save_face(self, id, name):
        
        face = Face(id, name)

        try:
            db = DbControl()

            db.insertFace(face)

            db.closeConnection()

        except Exception as ex:
            print(ex)
            raise Exception("Erro ao salvar no banco")
            

    
        
        
