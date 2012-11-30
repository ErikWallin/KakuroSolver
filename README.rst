************
KakuroSolver
************
KakuroSolver is a project done for the Typesafe Developer Contest to show some strengths of Typesafe Stack in a simple and concise manner. The resulting Kakuro solver program is entirely written in scala and solves the Kakuro with the Akka and Play2-mini, which are both part of the Typesafe Stack. The project should be perfect for scala developers with beginner/intermediate level.

Kakuro domain
#############
Kakuro is a logic puzzle with numbers. It's similar to a regular crossword, but with numbers instead of letters. The squares for numbers in Kakuro are called whites. The word descriptions are the sum of the numbers in the word. The words in Kakuro are called entries. The whites' numbers in each entry should be unique. More information about Kakuro could be found at http://en.wikipedia.org/wiki/Kakuro

Solution
********
The goal of this project is to solve the Kakuro. There exists no easy mathematical solution to Kakuro, since the problem is NP-complete. A program which use an elimination process is very suitable to solve the Kakuro, but also other similar problems.

Example
*******
If an entry has the the sum 8 and consists of 3 whites, one can easily figure out that the numbers 6-9 can't be in the whites. Other entries that contain the same whites will then be informed and could remove other numbers from the whites until the Kakuro is finally solved.::

    |  X  |17\  | 3\  | 
    |  \12|     |     |
    |  \8 |     |  X  |

The Kakuro above is 3x3 and has four entries, two vertical and two horizontal. Try for yourself to solve the puzzle by filling in the three whites.

Architecture
############
KakuroSolver consists of three sub projects to separate domain logic, parallelism and web from each other.

Domain
******
There are 3 main domain specific value objects in KakuroSolver, all immutable. They are all case classes with a few methods for the elimination process algorithm.

* White - A white is a square where a number should be filled in when solved.
* Entry - Consists of whites and a sum. This is like a word in a crossword.
* Puzzle - Consists of entries and whites.

* Square - Square is a class that represents different fields that each square in a kakuro puzzle can hold. It is used when parsing between a Puzzle and json/csv formats.

Concurrency - Akka Actors
*************************
There exist many calculations that could be made parallel. Each ``Entry`` could for example be calculated in parallel at once. This leads to a setup with three types of Akka actors that wraps the three domain classed - ``Puzzle``, ``Entry``, ``White``. A main program or a HTTP request starts the ``PuzzleActor``, which creates and starts the ``EntryActors`` and ``WhiteActors``. When everything is setup, the following flow is what happens.

0. ``PuzzleActor`` tells the ``EntryActors`` to start working.
1. If ``EntryActor`` finds numbers that can't be part of the solution, it updates the state (it's ``Entry``) and tells the corresponding ``WhiteActors``.
2. ``WhiteActor`` updates the state (it's ``White``) and tells the eventual other ``EntryActors`` it's connected to.
3. ``EntryActor`` updates the state. Goto 1

If whites or entries are completed, they tell ``PuzzleActor``, which updates the state (it's ``Puzzle``). When the Kakuro is solved, the main program or HTTP request presents the result and is thus finished.  

REST - Play2-mini
*****************
The purpose of a REST api with json is often to separate frontend from backend. There exist many REST opportunities, but KakuroSolver uses play2-mini, which is build on top of Typesafe's Play! Framework. This implies unfortunately a lot of dependencies. The KakuroSolver's REST api contains only one method, which is a GET request towards /kakuro. The body should be a json representation of the puzzle. Frontend developers and mobile app developers should easily be able to use the api for building nice GUIs.

Build system - sbt (Simple Build Tool)
**************************************
Although KakuroSolver consists of three sub projects, the sbt configuration is extremely simple and small. As much as possible of the settings has ben put in build.sbt instead of project/Build.scala.

Version 0.12.1 of sbt was used.

Test
****
Specs2 is used for unit tests of domain classes.
Scalatest with Akka testkit is used for testing the actors classes.

Dependencies
************
Production dependencies::

    scala          2.9.2
    akka-actor     2.0.4
    play-mini      2.0.3 (including a lot of other dependencies!)

Test dependencies::

    specs2        1.12.2
    junit            4.8
    scalatest        1.8
    akka-testkit   2.0.4

Build and run
#############
These instructions are for MacOS, but should work on any Linux OS as well.

Build and test
**************

    $ git clone git@github.com:ErikWallin/KakuroSolver.git
    $ sbt
    > compile
    > test

Eclipse project files could be generated.

    > eclipse


Run the Akka main program
*************************

    > project kakurosolver-akka
    > run

It is possible to specify your own puzzle.

    > run /path/to/KakuroSolver/scripts/body.json

Run the REST api
****************

    > project kakurosolver-rest
    > run

You can now use the REST api with for example curl. Open another terminal and test the api.

    $ /path/to/KakuroSolver/scripts/curl_test.sh

You might need to chmod it first.

    $ chmod +x /path/to/KakuroSolver/scripts/curl_test.sh 

Future improvements
###################
* The json handling seems to be much better in the future release of Play! Framework, but is currently not released.
* Optimized Mailbox for ``EntryActors``. ``WhiteUpdate`` messages should be prioritized before ``Reduce`` messages to avoid unneeded calculations and thus improve performance. Duplicates of ``Reduce`` messages should also be removed.
