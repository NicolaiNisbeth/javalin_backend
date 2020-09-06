
# Playgrounds of Copenhagen
Course project in Backend and distributed system built on (client-server architecture).

Quick overview of the project consiting of three servers and three user interfaces. The Amazon server, AWS EC2, is responsible for hosting RESTful API, communicating with database and handling user logins via login-service. 
The Amazon server is monitored by the Google cloud server, utilizing the framework Prometheus.
![Image of system](https://github.com/NicolaiNisbeth/javalin_backend/blob/master/src/main/resources/images/deployment_diagram.png?raw=true)
The website can be found on: *https://github.com/SersanAslan/KoebenhavnsLegepladser*

The android app can be found on: *https://github.com/NicolaiNisbeth/KobenhavnApp*

## Usage
The project is no longer in service but can be experimented with in localhost by starting the javalin server in javalin_backend/src/main/java/Main.java
