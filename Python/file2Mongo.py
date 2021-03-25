import os
import tarfile
import mongodb
from pymongo import MongoClient
"""
{
"user":"email or username",
"passwords":["list","of","passwords"]
}

Things to be done:
Check documents for name, if not found create and add password.
If found append passsword to passwords list
"""
client = MongoClient('localhost', 27017)
db = client.['password-db']
collection = db.['password-col']
PATH = "/media/user/DATA/FrostWire/Torrent Data/Collection 1/Collection  #1_BTC combos.tar.gz"


def main():
    tar = tarfile.open(PATH,'r')
    for file in tar.getmembers():
        if file.size > 0:
            buffer = []
            nl = 0
            cur = ""
            extracted = tar.extractfile(file)

            cur = extracted.read(file.size).decode()

            cur = cur.split('\r\n')
            for line in cur:
                if ":" in line:
                    line =line.split(":")
                else:
                    line =line.split(";")
                print(line)


            #print(extracted.read(16))
        print(file,file.size)

main()
