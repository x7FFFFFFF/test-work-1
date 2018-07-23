## Test work

#### Dependencies
netty, h2, aalto-xml

#### Build
mvn clean package

#### Run
###### cd main/target<br/>
###### java -jar TestWebService.jar -isDbServer -port 8888
// run db server on port 8888 <br/>
###### java -jar TestWebService.jar  -isHttpServer -port 8081 -dbServer localhost:8888
//run http server on port 8081, use db server on port 8888 (mast be already started)<br/>
###### java -jar TestWebService.jar  -isHttpServer -port 8082 -dbServer localhost:8888
//run http server on port 8082, use db server on port 8888 (mast be already started)<br/>
etc.<br/>

#### Test
Default url is "/test".  It can be changed in settings.properties file. <br/>
###### http://localhost:8081/test 
###### http://localhost:8082/test 




