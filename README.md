# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

URL for the Presentation:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5xDAaTgALdvYoALIoAIyY9lAQAK7YAMQALADMABwATG4gMHHI9r5gOgjRhgBKKPZIqhZySBBomIiopAC0AHzklDRQAFwwANoACgDyZAAqALowAPTRBlAAOmgA3gBEs5RowAC2KCvdKzArADSHuOoA7tAcewfHhyhbwEgIN4cAvpjCXTBtrOxclF6q3WUE2O1edxWZ1Ulyg132hxOKweTxeCJWHzYnG4sF+X1EvSg5UqYEoAAoyhUqpQygBHYpVACUn06oh+7Vk8iUKnUvUCYAAqnMySCwShmZzFMo1Kp2UYdN0AGJITgwIWUSUwHSWGCi7ZiHRE4AAazVcxg5yQYF8urmYpgwAQRo4OpQAA8SRpJdyZT88ayVL11VBJSyRCo-e0voCYAonShgC7Rib0ABRN0qbAEOph77NX6Ncy9BJOJKLVY7dTAQJ7Q6pqBRHq2jb6h3xxM6+TG9AYsycTDe6XqSMdcMoXpoaIIBC5tm-Qc81TdEBG0nBkVzSUS7Q+4e-YzdBQcDhmjXaWcR+c7odLlcJ0kKaLWsnAJ++LcD6+LuUHo8nx-WqG+KXr8WIAk2lIkjSahTlgYE4iO0ZNsCdr6gcvS3Eir7WqMEDdmg6GHO8uaUCOhYYL0aROE45ZrKh4IwBhiKHNhvi4fhhG3B86AcKYERRLEcTQAEhhxIqcCptIcAKDAAAyEAVPU5HMP61AxoMIwTNMBjqLUaDlnqOwnNCsIcB8SFyvBMYoS24LokiJlXBCmL-DibTAeOMAIApKpkvJil0gyYDMh5bQLjK3T8uuhnip+XI3vuCrKqqwaatqzagvqmCGgmpoAEKJjAgVqFg4XqPmUYBp5k7TjAF4oBVo5dL0BUcMVVTppm2b1EhjXKcW1G0ZWqjVrs+z1o2vR6CeRL0iVvY8dlRqmgAkmgVCOkgJ4xVMjlwnFUqLo1Hm9GtG3ICewb2tAMB7bxoWqc1aqbG+0BIAAXigHCdSgWZ6SRUB9cgRYwCWoRDTKo03BN0C9NEL3Wm9n3XNx-Y5SaMgoAAZq+CDMDDUAHbuqjHVVQZoMaaAQOcaAwD9f05g9lVqU2AoU1TNP091ANA00YC9AArINywrMNUPjQ2sMwGSHBqCuxDdTAEBYzAlCNoyC39mVJOPQSp4hueoUcl+EV3nIKAAb4L5vh+2uJYex6xjbhtVY1VlNv5KplKosGYO7pMs0CdG2WNmEsW+7E9ui5mdJQvMg1RNEizFEJYRHeFR1xfa8aYkQxPEkQoOg2S5PkhfF-5MRYMpcpIb0fTSKmsmpqMqaTFMOmqHpiysZHPWx4D7Tu70vcZ-U-vM1QevefYVd+QpVftcF9VXvFi6RSgYCW9bOFj9ua8yvbyX-s78hajqo-4UtuUwGdm0npf6BEwluuBjANUzkzTUxmzrFI19XN-q9QLMDCioMnDgxFmLGsEtJq6gRr4f+KNs7XwxtIbGuN8aS0JnbV+1UpwICVrAc4UQHC3X0DCK4K9Ho-3ZtTNAgDGYD3jmAoWScKyQxgSsAmvQZZyygArPSSsVZq2gBrVGvFtaITJk7QCLsxxym1t0WW3AHxvh3mxPez9vz2zKFsCANBZHvnkVPFQFVh5yQXk+b2vt-buQHkCGOLMKr9RgIncsEi+J50EkSE8cRsAqlNLJEkMAADi+oNDKXsYHfooSW7t3sPqHu6cr69VaBYx+49XJx1aCdLyJJwmVj8gUiJS8Qqu1aEo-k29Mn70OofVoB5j5GLShfFJT90arXWvfB07T6i4NyTIj+1DBkxN-q9ARyNGH92cW0VxYMIZVi4Tw+Bf9JlfU1rxTpmMcawTptg7Rh88ETgIUQi0pDHB3RGd-VmdDOYZl+tzYB7RXFsMWSNZZ2DeGy1UPLBmtNlaq2weIlBAy8mWyAhUpRyAqiFLUGSOpxM2gHlCZvNshC4WhjBTI4JVQAA8mLtCtBGRY3FYA4WqBsXjP22TAbRKeqsRJlY9h9EZfqFa0hayhDSEkBISJzi+CtCgK6aF9iQkKKAY0Iq7IrEhEylAAA5fUYq3jjCcV0FxoD+ZuOFmy5l9c9UoA5bWeVHL+WCrXPRUO4qECSuldapE8qlU7BVWqlBucBLxA4AAdjcE4FAThsipiSHACSAA2eAq5DBwrqjXel6lhhjASUkhBfdyxOv1OqnJGS+kwAANQwAzTsGl2Icl5LNqSOFZI4BRrhWUlelSTa8mqeo2phzyqNKSiqE+ciz7pUyag-KhUl7tp1pPPWa0QAQC2NgVRpQUBzSqNcuuMBWpL2mTzEBfMBrsNFpw0OKzpowFmkFTZg7b7dIur03eV9sVjhHrmkhdRHA3SpkIJA9g0BfRgCqEZK7xmI3Wd9B5-zN2tHmRA954tuFfNWRMj6GzPHbPQbsvG+zGyjoDqYzybNKb0PQ9AZdDjnp4fuV1IBzCt0gzeVA-d0NYN8N+QI-5wigXqzPXe7DvQIUmNEGFJtt5a36jJPK22AnkUKj-GE-UWLxP0r1qa6QJLaW9BrfeFAdaYLUrsaMhlKxFOHD5Oy6QWa6Xga1ZRXV+njOGcLcZzxHr85xEsCgac1MS4fvyC5tz5wYAACkIAqmkzsbIEqQDGgaFq2uxHBgCi0lMeVySb3oHLLO4ALmoBwAgN5KAJxFOmcsip69mj8L5pgAAK0C7TFUWMIAlvAtF+9FWqtVoCyqTTi7l4eUUQJjeW9W19MRTeH8XbVQ8b7W05L9Rtl3yvQOqRWG9bDO61h2hayEPAfI0wlmZELPgMgRwpZB7YPw3W8jDjAnpFNfG8AeqPWD7Ns3lWxTQ2dHtCad24LKBWl2Z2By89MaOAQDUO-CAzB3Qkkw-Jt+y3XbxqbGuhdQUN3PIg7u6Bx24FHpPfNBzC3jlfdDNsrLOW2zOk7CmGZ2GrtPTjOT5M+EUcD129u8BZZaNHfo3Ax05OYBdh7Eh5aOzMEEZwXJ8db9cMc1pgTIjYy7kMJA08yjLy9s0cOx8zHUtGN-MVoC0RUAQWLRW8bB7S5YhaErcJ0T2hXu+ntgKbAlvo0yd42YtoFi2toE0z7bTtKKorqWKZzVrP3HLAc-xJzkR0sebyGAaPOpEAJlgMAbAs7CA1DqLGqLNCmwNybi3Nu0xjAA0K6Wno9WEIE5ANwPACK7ur3qbyGvyf68LfewqdBc70X61k2bkb3Qu+b0MI6QhN3R0D6H6SHvhL5DnpQyL2XnG9ZS-w7LpmK7EedaZ7M8zrP1eHAx7WGDcCdfMb1yI4FBxPErdAkVpPeAqVwVpTT6yBWWHarD0HlBQA
