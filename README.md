
# Island Rescue Mission

- Authors:
  - [Jake, Finlay]
  - [Vikram, Chandar]
  - [Arjun, Sasidaran]

## Product Description

This product is an _exploration command center_ for the [Island](https://ace-design.github.io/island/) serious game. 

- The `Explorer` class implements the command center, used to compete with the others. 
- The `Runner` class allows one to run the command center on a specific map.

### Strategy description

The exploration strategy is for to do an interlaced scan once finding the island to reduce the number of turns necessary.

## How to compile, run and deploy

### Compiling the project:

```
mosser@azrael a2-template % mvn clean package
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.960 s
[INFO] Finished at: 2024-01-20T18:26:43-05:00
[INFO] ------------------------------------------------------------------------
chandar@Vikrams-MacBook-Air a2-template % 
```

This creates one jar file in the `target` directory, named after the team identifier.

As the project is intended to run in the competition arena, this jar is not executable. 

### Run the project

The project is not intended to be started by the user, but instead to be part of the competition arena. However, one might one to execute their command center on local files for testing purposes.

To do so, we ask maven to execute the `Runner` class, using a map provided as parameter:

```
chandar@Vikrams-MacBook-Air a2-template % mvn exec:java -q -Dexec.args="./maps/map03.json"
```

It creates three files in the `outputs` directory:

- `_pois.json`: the location of the points of interests
- `Explorer_Island.json`: a transcript of the dialogue between the player and the game engine
- `Explorer.svg`: the map explored by the player, with a fog of war for the tiles that were not visited.
