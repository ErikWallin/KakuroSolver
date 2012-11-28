# KakuroSolver
KakuroSolver is a project done for the Typesafe Developer Contest to show some strengths of Typesafe Stack in a simple and concise manner. The resulting Kakuro solver program is entirely written in scala and solves the Kakuro with the Akka and Play2-mini, which are both part of the Typesafe Stack. The project should be perfect for scala developers with beginner/intermediate level.

## Kakuro domain
Kakuro is a logic puzzle with numbers. It's similar to a regular crossword, but with numbers instead of letters. The word descriptions are the sum of the numbers in the word. The numbers in each word should be unique. More information about Kakuro could be found at http://en.wikipedia.org/wiki/Kakuro

### Solution
The goal of this project is to solve the Kakuro. There exists no easy mathematical solution to Kakuro, since the problem is NP-complete. A program which use an elimination process is very suitable to solve the Kakuro, but also other similar problems.

### Example
If a word has the the sum 8 and consists of 3 numbers, one can easily figure out that the numbers 6-9 can't be in that word. Other words that contain the same squares will then get information and could remove other numbers until the Kakuro is finally solved.

<pre>
|  X  |17\  | 3\  | 
|  \12|     |     |
|  \8 |     |  X  |
</pre>

The Kakuro above is 3x3 and has four words, two vertical and two horizontal. Try for yourself to solve the puzzle by filling in the three empty squares.

## Architecture
KakuroSolver consists of three sub projects to separate domain logic, parallelism and web from each other.

### Domain
There are 3 main domain specific value objects in KakuroSolver, all immutable. They are all case classes with a few methods for the elimination process algorithm.

* White - A white is a square where a number should be filled in when solved.
* Entry - Consists of whites and a sum. This is like a word in a crossword.
* Puzzle - Consists of entries and whites.

* Square - Square is a class that represents different fields that each square in a kakuro puzzle can hold. It is used when parsing between a Puzzle and json/csv formats.

### Concurrency - Akka Actors
There exist many calculations that could be made parallel. Each word could for example be calculated in parallel at once. This leads to a setup with three types of Akka actors that wraps the three core domain classed - Puzzle, Word, Square. A main program or a HTTP request starts the PuzzleActor, which creates and starts the WordActors and SquareActors. When everything is setup, the following flow is what happens.

0. PuzzleActor tells the WordActors to start working.
1. If WordActor finds numbers that can't be part of the solution, it updates the state (it's word) and tells the corresponding SquareActors.
2. SquareActor updates the state (it's square) and tells the eventual other WordActor it's connected to.
3. WordActor updates the state. Goto 1

If squares or words are completed, they tell PuzzleActor, which updates the state (it's puzzle). When the Kakuro is solved, the main program or HTTP request presents the result and is thus finished.  

### REST - Play2-mini
The purpose of a REST api with json is often to separate frontend from backend. There exist many REST opportunities, but KakuroSolver uses play2-mini, which is build on top of Typesafe's Play! Framework. This implies unfortunately a lot of dependencies. The KakuroSolver's REST api contains only one method, which is a GET request towards /kakuro. The body should be a json representation of the puzzle. Frontend developers and mobile app developers should easily be able to use the api for building nice GUIs.

### Build system - sbt
Although KakuroSolver consists of three sub projects, the sbt configuration is extremely simple and small. As much as possible of the settings has ben put in build.sbt instead of project/Build.scala.

Version 0.12.1 was used.

### Test - specs2
Specs2 is used for unit tests of domain classes.
Scalatest with akka testkit is used for testing the actors classes.

### Dependencies
Production dependencies
<pre>
scala          2.9.2
akka-actor     2.0.4
play-mini      2.0.3 (including a lot of other dependencies!)
</pre>

Test dependencies
<pre>
specs2        1.12.2
junit            4.8
scalatest        1.8
akka-testkit   2.0.4
</pre>

## Build and run
These instructions are for MacOS, but should work on any Linux OS as well.

### Build and test
<pre>
$ git clone git@github.com:ErikWallin/KakuroSolver.git
$ sbt
> compile
> test
</pre>
Eclipse project files could be generated
<pre>
> eclipse
</pre>

### Run the Akka main program
<pre>
> project kakurosolver-akka
> run
</pre>
To run an own puzzle
<pre>
> run /path/to/KakuroSolver/scripts/body.json
</pre>

### Run the REST api
<pre>
> project kakurosolver-rest
> run
</pre>
You can now use the REST api with for example curl. Open another terminal and test the api.
<pre>
$ /path/to/KakuroSolver/scripts/curl_test.sh
</pre>
You might need to chmod it first
<pre>
$ chmod +x /path/to/KakuroSolver/scripts/curl_test.sh 
</pre>

## Future improvements
* The json handling seems to be much better in the future release of Play! Framework, but is currently not available.
* Optimized Mailbox for WordActors. SquareUpdate messages should be prioritized before Reduce messages to avoid unneeded calculations and thus improve performance.

