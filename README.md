# [SpringBoot] SMSProxy
> Sample server-application that exposes a REST service able to send SMSs using [MessageBird's API](https://github.com/messagebird/java-rest-api).

Application starts a Tomcat server on localhost (port 8443) and a scheduled task that will execute every second and send to MessageBird's API the messages pending.\
* It exposes 2 services: "request-sms" (POST) to send a SMS; and "report" (GET) to check status of already sent SMSs.
* It uses a H2 database running on memory, to store the created (and pending) SMSs. Meaning, you will lose all data if you reboot the application.\
* Server only accepts SSL communications (use ssl-server.crt as valid certificate).


## Installation

### Requirements

* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/overview/index.html) 8+
* [Apache Maven](https://maven.apache.org/) 3.3+
* [MessageBird's API key](https://dashboard.messagebird.com/app/en/sign-up)

### Step by step

* Clone this repository.
```
git clone https://github.com/dpradom/smsproxy
```
* Edit `src/main/resources/application.properties` to set your MessageBird's API key on `sender.apiKey`.
* Build the project.
```
mvn clean install
```
* Start it.
```
java -jar smsproxy-0.0.1-SNAPSHOT.jar
```

## Usage example

Sample request to create a SMS using curl:
```
curl -k  --header "Content-Type: application/json" --request POST --data "{\"recipient\":1555432112,\"originator\":\"INBOX\",\"message\":\"This is a test message.\"}" https://localhost:8443/request-sms
```

## Release History

* 0.0.1
    * Work in progress

## Meta
Daniel Prado – dpradom@argallar.com

Distributed under the GPLv3 license. See ``LICENSE`` for more information.

