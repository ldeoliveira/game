# Game
Simple board game

# Building

Clone this repo, cd into the project's directory and type the following command:

`/gradlew build && docker-compose build && docker-compose up`

Disclaimer: the integration tests use an embedded version of MongoDB that takes some time to setup the first time you execute them. 


# Playing

After the application is up, the game should be available at `http://localhost:9090/game-ui`. To simulate 2 players against each other, simply open the same URL in another tab.

Players are assigned a board on a first-come first-serve basis. Therefore, it is possible to have more than one game happening at any given moment.

# Technical details

### Stack: 
Java 1.8, Spring 5 (Spring Data, Spring Websocket...), MongoDB, Docker, Gradle

### Testing
* Unit tests: they were written using the Gherkin syntax and are responsible mainly for testing the game logic. Coverage is 94% of lines.
* Integration tests: they are mainly responsible for testing HTTP related communication, like request payload validation, correct status, and response serialization.
* UI tests: done manually.



