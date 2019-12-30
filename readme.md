# For New Comers
If you are new to Keikai, we recommend you to read [Tutorial](https://doc.keikai.io/tutorial) first to know some basic ideas.

# How to Run This Project
Clone the project and launch your command line interface in the keikai-tutorial folder. Execute the following commands based on your environment.

## Maven installed
`mvn jetty:run`

## No Maven installed yet
Run the Maven wrapper below which will download everything needed for you during starting up: 
* Linux / Mac

`./mvnw jetty:run`

* Windows

`mvnw.cmd jetty:run`


When you see the following messages:
```
...
[INFO] Started Jetty Server
[INFO] Starting scanner at interval of 5 seconds.

```

Then visit http://localhost:8080/ with your browser. You can press `Ctrl+c` to stop the server.


# Welcome to our sites:
## [Website](https://keikai.io)  
## [Demo](https://keikai.io/demo)
## [Document](https://doc.keikai.io)
## [Blog](https://keikai.io/blog)


# Try Freshly Release
Freshly release contains the latest features and bug fixes that are under development. It's built for testing and evaluation. Welcome to try it and [give us feedback](https://keikai.io/contact).

The steps are:
1. check the latest freshly version at [evaluation repo](https://mavensync.zkoss.org/eval/io/keikai/keikai-ex/)
2. change the keikai version in `pom.xml`
3. run the project according to [How to Run This Project](#How-to-Run-This-Project)