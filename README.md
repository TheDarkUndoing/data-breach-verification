# passwordDB MariaDB Implementation
Currently a database builder for the Collection password leak.
Eventually going to be a full web application.

[![version](https://img.shields.io/github/v/tag/TheDarkUndoing/passwordDB)](https://github.com/TheDarkUndoing/passwordDB/releases)





## Prerequisite
- [MariaDB 10.4+](https://downloads.mariadb.org/)

## Running a release version
Download passwordDB.jar and testData.tar.gz from releases tab.
Then place them in the same directory.
```
$ java -jar passwordDB.jar testData.tar.gz
```

## Build Instructions
Instructions for building and deployment as well as necessary requirements.

### Build Project
```
$ cd Java/passwordDB/
$ gradle build
```
### Deploying Project
Take jar from build/libs/ and place in same folder with testData.tar.gz
```
$ java -jar passwordDB.jar testData.tar.gz
```

If there are any features you would like or issues you have please open a [ticket](https://github.com/TheDarkUndoing/passwordDB/issues/).
