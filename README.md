# 412HockeyStatisticSystem

How to Run without maven:
    Make sure you have Java installed 
    Open a terminal in the folder containing the .jar file
    Run the following command: java -jar HockeyDashboard-0.0.1-SNAPSHOT.jar
    Open your browser and go to: http://localhost:8080


How to run with maven: 
    To run on mac in terminal: mvn spring-boot:run
    To run on Windwos in terminal: ./mvnw.cmd spring-boot:run

M03-A04 - Implementing Design Patterns

GROUP DEADLINE DUE SUNDAY APRIL 5TH AT 5PM

- specifies each of the pattern implementations i.e. the class or classes where the patterns are implemented.

Design Patterns

Claire - Strategy Pattern
I used the Strategy pattern in the DataRepository class of our project using the Comparator interface. The Comparator interface is essentially a strategy interface built into Java. I used lambda to pass through .sort(), which is a concrete strategy. I am swapping in a different algorithm without changing the sorting code at all, which is what the strategy pattern is. Examples of these methods in the class are in lines 132, 137, 142 among others. This demonstrates the Strategy Pattern because the sorting behavior is encapsulated and interchangeable at runtime.

Avery - Observer

Marcela - Builder

Matt - Prototype Creational

UI Patterns

Claire - Search Filters
Search filters is one of the main functions of our UI interface, and it is used in many different areas of our software system. It is used when the users needs to conduct a search using contextual filters that narrow the search results. One of the main areas we used search filters is in our compare players interface, where users are able to choose two players from our olympic software system and view their stats alongside each other. There are so many players that participated in the Olympics, so our interface allows users to choose a country before finding a player if they are looking to narrow the search. All of this was done in the Compare.html file. 

Avery - Dashboard

Marcela - Formatting Data

Matt- Card


