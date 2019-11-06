# Simple FD Generator

## Motivation

This is a very simple and probably not a very good piece of software ( this was done in a very short time ). I had an Assignment in Introduction To Databases where we should find FD's (functional dependencies) in a file with 44k lines of entities. It seemed like a trivial and time-consuming assignment and is, therefore, the reason for this tools existence.

## Note

1. The program only works for tables with entities in them, i is impossible for the prorgam to detect any FD's without entitie sin the table.
2. It **Should** work on any given DB.

## How to use

1. Enter the database information and user details in Main.java
2. Compile the two java file `javac *.java`
3. run the program with `java -cp .:./postgresql-42.2.8.jar Main` ( I'm not sure if this will work on windows, however it should work on any unix system. )
