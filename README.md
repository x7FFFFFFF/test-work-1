## Test work

#### Dependencies
netty, h2, aalto-xml

#### Build
mvn clean package

#### Run
###### cd main/target<br/>
###### java -DisDbServer -Dport=8888 -jar TestWebService.jar
// run db server on port 8888 <br/>
###### java -DisHttpServer -Dport=8081 -DdbServer=localhost:8888 -jar TestWebService.jar
//run http server on port 8081, use db server on port 8888 (mast be already started)<br/>
###### java   -DisHttpServer -Dport=8082 -DdbServer=localhost:8888 -jar TestWebService.jar
//run http server on port 8082, use db server on port 8888 (mast be already started)<br/>
etc.<br/>

#### Test
Default url is "/test".  It can be changed in settings.properties file. <br/>
###### localhost:8081/test 
###### localhost:8082/test 




