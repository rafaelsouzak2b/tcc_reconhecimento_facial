import psycopg2
from model.db import Db

class DbControl:

    def __init__(self):

        self.db = Db()
        self.db.conn = psycopg2.connect(host=self.db.host, database=self.db.database, user=self.db.user, password=self.db.password)
        self.db.cursor = self.db.conn.cursor()


    def insertFace(self, face):

        self.db.cursor.execute(f"INSERT INTO face VALUES('{face.id}', '{face.name.upper()}')")
        self.db.conn.commit()


    def selectFace(self, id):

        self.db.cursor.execute(f"SELECT name FROM face WHERE id = '{id}'")
        
        name = self.db.cursor.fetchone()

        if name is not None:
            return name[0]
        
        return None

    
    def selectAllFacesIds(self):

        self.db.cursor.execute("SELECT id FROM face")

        rows = self.db.cursor.fetchall()

        ids = []

        for row in rows:
            ids.append(row[0])

        return ids
    
    
    def closeConnection(self):

        self.db.conn.close()
