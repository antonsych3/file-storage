**File Storage REST service**    

***Task description***     

 Let's imagine we are developing an application that allows us to store files in the cloud, categorize them with tags 
 and search through them.
 In this test assignment let's implement the smaller subset of the described functionality. We won't store the actual 
 file content, only their name and size at the moment.
 Design a web-service (REST) with an interface described below.  
 
 ***How to run application***
1. You need to have installed on your PC [docker](https://docs.docker.com/engine/install/), 
[maven](https://maven.apache.org/download.cgi) 
and [jdk-11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
If you have, run next command:

```shell script
docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.9.3
```
Instead of using Elasticsearch from Docker, you can install 
[elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html) 
and run it on ***localhost***, port ***9200***. Check [link](http://localhost:9200/), that everything works correctly.

2. Then from root folder of the project run command:
```shell script
mvn clean package
```
3. Then run next command  
```shell script
mvn spring-boot:run
```