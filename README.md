**File Storage REST service**    

***Task description***     

 Let's imagine we are developing an application that allows us to store files in the cloud, categorize them with tags 
 and search through them.
 In this test assignment let's implement the smaller subset of the described functionality. We won't store the actual 
 file content, only their name and size at the moment.
 Design a web-service (REST) with an interface described below.  
 
 ***How to run application***
1. You need to have installed on your PC [docker](https://docs.docker.com/engine/install/)
If you already have had it, run next command:

```shell script
docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.9.3
```
Instead, this step, you can install 
[elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html) 
and run it on ***localhost***, port ***9200***. Check [link](http://localhost:9200/), that everything works correctly.

2. Then from root folder of the project run command:
```shell script
mvn clean package
```
3. After run next command  
```shell script
mvn spring-boot:run
```