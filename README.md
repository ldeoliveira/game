# Game
Simple board game

# Building

Clone this repo, cd into the project's directory and type the following command:

`./gradlew build && docker-compose build && docker-compose up`

Disclaimer: the integration tests use an embedded version of MongoDB that takes some time to setup the first time you execute them. 

# Playing

After the application is up, the game should be available at `http://localhost:9090/game-ui`. To simulate 2 players against each other, simply open the same URL in another tab.

Players are assigned a board on a first-come first-serve basis. Therefore, it is possible to have more than one game happening at any given moment.

# Technical details

### Stack: 
Javascript, HTML, CSS, Java 1.8, Spring 5 (Spring Data, Spring Websocket...), MongoDB, Docker, Gradle

### Testing
* Unit tests: they were written using the Gherkin syntax and are responsible mainly for testing the game logic. Coverage is 94% of lines.
* Integration tests: they are mainly responsible for testing HTTP related communication, like request payload validation, correct status, and response serialization.
* UI tests: done manually.

# What was NOT done?

I would like to mention some features that should be included to reach a production level:

* Security: authentication, use HTTPS instead of HTTP, WSS instead of WS, and so on...
* Fault tolerance: handle edge cases like, for example, player abandons the game
* Documentation: Swagger and Javadocs
* Logging


# References

Last, but never least, some resources were fundamental for me completing this assignment. I think it's more than fair to mention them below:

* https://dev-pages.info/how-to-run-spring-boot-and-mongodb-in-docker-using-docker-compose/
* https://techlab.bol.com/in-search-for-perfect-coding-interview-assignment/
* https://www.petrikainulainen.net/programming/gradle/getting-started-with-gradle-integration-testing/
* https://spring.io/guides/gs/messaging-stomp-websocket/



