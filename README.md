# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[![Sequence Diagram]]
This one I created
[https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uGi0azF5vDgM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVK0jr0iWWrWs062+2OgNBl1g9W5U6Ai5lCJQpFQSKqJVYNPAmWFI6XGDXUNisYwcqrJ7AQOpeoQADW6Emtf2rsoxfgyHM5QATE4nN0K0Mqx2po8pg2g8222gp6sDugOKYvL5-AFoOxyTAADIQaJJAJpDJZft5EvFMvVOpNVoGdQJNBjo0oE36V5LA6lkGFIWZbjrck6fE8ppLLsfQAucRaymqKDlAgx48rCR4nqi6KxNiSaGB6EZemSFIGiGE6jBaRKRjaHLcryBqCsKMAfuGVFer2eHlAx2iuu6BKESyPq6pk8bBh+lFMux0YcrGMCiYmcr4amcFlhhPK5vmmBASCCElFcAyVqMy4zn0c5Nq27Y1tOMHdjphTZAOMDDqOvQGeR1adl8ZkLpZnmrpwG7eH4gReCg6CHsevjMGe6SZJgDnXkU1B3tIACiB6pfUqXNC0z6qK+3TeRZaC2Wy2n6UVi7GTZ2lspxMAofYUXoZFgZYRiuGKRqAmkkYKDcCJjawpV6ASVaUaFLa0j9RShiidASAAF7xIkvHJspQKqa1YAaQgBYqVAKb5P+Vx-redl9jkYBDiOY7+eunhBdukKOge0IwAA4lWrIxRe8VXswul3h9mU5fYVaFY2Pklf+ZUHRVUPFdVsGbQBSVuvKDXQl9oyqOh2Pfe1OFrfh-FsYJMDkmAonDYji5jdRBS2vAqQoCALZyY2C3LQQq2etKR3o+CnMJjxeHdeTvXILEONqGRoEUaxkkskzHKZhYqA0DAwDKjAQaGODuMwAAZtAeus8xHRK+NR31W9sQwBAxuU99JOC+Vh4E7ju37ajgsneWUyG2o4yVP0wcAJLSKHACMg4AMwACxPOemQGncKxfDoCCgC26dgX0XzBwAclWEwwTAjRnclF0JddTm3b0QffaHFTh1WUexwnydTKn+qGfchdPNnuf50Z5dPCXZeF3slf3YFW6BNgPhQNg3DwMJhiyyksWXlddXneU94NGDEPBHT6BjlPozVyUBQe-0I1LuBzejKX48rlpB26cLvp6rLsI4Cb1lkTLEbt8j816lTGmT8GZSXyMzOArN2Yi1SNzFaJVIFRh-pjeSYtFIFCweUP+mQAHiWtpGVW5QgF+kyDANAKBkgu3FFgjiikyQd2kG7e+8MN60JQCAtQmltK20PoHPokdo6eRgLfSgR0643Rcu3UYncrKfzXAvYKARLD9RQkwgAUhAHkn0qyBBHuzf6+8gYZmqJSR8LRg6Q3nMVMca9gA6KgHACAKEoCzEkbIw6+QH6mQvs-Oscwc4eK8T46Ck8O4fE-iIm8iFygACsjFoAAYYnkgi0QdXAUQymFIYGhLgQLBBaskFsw5vNKAS0MEUKkjgpCqCFKIUIT1b0RSwAAMkWU9QVD1Z1JQFrYOMBkgZFSMwlAUdGkqwKPVCokjZh9zHp+IUOd2ZrL8VWd+KBGgFM6eUAwPSqxVEiZQWEyzLbuRNBczx3joD9NUIMyk2AtB0LGRMoMMA3EeO4UE3hND-5Vh9l-P2CyxE9ACfIgGii7pmACo9ReAQvDuL7H6WAwBsBr0IBgnef064HxrkfNKGUso5WMLZHhqNTrgvTM04h3A8Cwk6u0iBRyYAgGZXCZ5gzITDAgFrHWCAblQFUDs3Gswn4aDwkdD2iBMVgpEZCkl5YYUFAUQ3JR88gA)

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
