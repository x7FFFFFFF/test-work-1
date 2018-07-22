## Test work

#### Dependencies
netty, h2, aalto-xml

#### Build
mvn clean package

#### Run
cd main/target
java -jar TestWebService.jar -isDbServer -port 8888    // run db server on port 8888
java -jar TestWebService.jar  -isHttpServer -port 8081 -dbServer localhost:8888 //run http server on port 8081, use db server on port 8888 (mast be already started)
java -jar TestWebService.jar  -isHttpServer -port 8082 -dbServer localhost:8888 //run http server on port 8082, use db server on port 8888 (mast be already started)
etc.

