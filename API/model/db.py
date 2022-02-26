class Db:

    def __init__(self):
        self.host = ""
        self.database = ""
        self.user = "postgres"
        self.password = ""
        self.conn = None
        self.cursor = None