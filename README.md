# Klondike Solitaire

Klondike is a well known solitaire game, mostly called simply Solitaire.
This game implements the hardest type of Klondike: it puts three cards at once to the waste and only the top card is movable (the right most one) in that triad.

![Klondike Solitaire](https://cloud.githubusercontent.com/assets/18663930/26455236/f4bbe558-4169-11e7-9b71-f40d7296ae51.png)

## Requirement
Requires Oracle JDK 8 and Maven 3.0 or above.

## Usage
To build and run project:
```
git clone https://github.com/kovsanyi/klondike-solitaire.git
cd klondike-solitaire/
mvn package
java -jar ./target/klondike-solitaire-1.0-jar-with-dependencies.jar
```

## Generating a site
To generate the project's site and reports, execute:
```
mvn site
```
