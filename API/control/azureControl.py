from requests import post
import json

class AzureControl:

    def post(self, azure):

        response = post(url=azure.url, headers=azure.headers, data=azure.data)

        return json.loads(response.text), response.status_code