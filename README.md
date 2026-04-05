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
----------------

Claire - Strategy Pattern

I used the Strategy pattern in the DataRepository class of our project using the Comparator interface. The Comparator interface is essentially a strategy interface built into Java. I used lambda to pass through .sort(), which is a concrete strategy. I am swapping in a different algorithm without changing the sorting code at all, which is what the strategy pattern is. Examples of these methods in the class are in lines 132, 137, 142 among others. This demonstrates the Strategy Pattern because the sorting behavior is encapsulated and interchangeable at runtime.

Avery - Observer

The Observer pattern is implemented through the DataSubject and DataObserver classes in our project. DataSubject acts as the subject, maintaining a list of observers and notifying them when data changes. DataObserver is the observer interface, which concrete observers implement to receive updates. When data is modified in the repository, the subject calls notifyObservers(), which triggers the update() method on all registered observers. This demonstrates the Observer pattern because objects that depend on the data do not need to constantly poll for changes — they are automatically notified when something changes. This mirrors a real scenario where a dashboard or UI component needs to reflect live data updates without manually checking for changes on every interaction.

Marcela - Builder Pattern

The Builder pattern I selected is implemented in the Game.java class. There is a static Builder class that simplifies adding games by breaking the data into smaller steps. It keeps the data organized by having the gameId, teamA and teamH as final variables to avoid any changes, and the other variables are set into a default status. This means that the gameId and both teams are the minimum requirements to input a new game, as those are always needed to identify the games. Therefore, the variabales that differ in every game are the ones that only require changes. Each method returns "this" to keep the format, which avoids needing to call every element every time. Once all data is collected, build() passes the data into the game constructor to create a Game object. The overall purpose of the builder pattern is to clearly construct objects that have many fields, without requiring all of them.This mirrors a real scenario where a game is first scheduled with only the required info, and the remaining details like score and status are updated as the game is played.

Matt - Prototype Creational Pattern
The prototype creational pattern that I selected was implemented in 

UI Patterns
-------------

Claire - Search Filters

Search filters is one of the main functions of our UI interface, and it is used in many different areas of our software system. It is used when the users needs to conduct a search using contextual filters that narrow the search results. One of the main areas we used search filters is in our compare players interface, where users are able to choose two players from our olympic software system and view their stats alongside each other. There are so many players that participated in the Olympics, so our interface allows users to choose a country before finding a player if they are looking to narrow the search. All of this was done in the Compare.html file. 

Avery - Dashboard

The Dashboard pattern is implemented through the main landing page (index.html) of our project. The dashboard serves as a centralized view that aggregates and displays the most important information from across the application in one place. On loading, it fetches the full player dataset and computes key  statistics including total players, total goals, number of teams, and the points leader. It also renders a goals-by-country bar chart that visually ranks each country by their total goal output. Rather than requiring a user to navigate through multiple pages to build a picture of the tournament, the dashboard surfaces the most relevant information immediately. Quick navigation links to Compare Players, Leaderboard, Teams, and Results are also provided, making the dashboard the natural starting point for any user interaction with the application. This demonstrates the Dashboard pattern because it consolidates data from multiple sources into a single, scannable, high-level overview that gives the user immediate situational awareness of the entire tournament.

Marcela - Formatting Data

The UI pattern of formatting data is implemented across multiple pages in the system. In Leaderboard.html, a data grid displays player stats such as goals, assists, and points in sortable columns, allowing users to quickly rank and compare players. Dropdowns filter the table by country and position so users only see relevant data. In
Results.html, game result cards are grouped into lists by stage (Group A, Quarterfinal, etc.) and filtered by team and gender using dropdowns. In Teams.html, each team card expands into a roster table showing individual player stats. In Player.html, a 6-stat grid displays a single player's full statistics once selected. Together, these elements keep the display consistent across the platform and allow users to navigate large amounts of data without being overwhelmed.

Matt- Cards
The UI pattern of cards is implemented

_____________________________________________________________________________
|                 |                   |                 | Classes/Interfaces |
| Team- Member ID | Team- Member Name | Design Pattern  |  implementing the  |
|                 |                   | Implemented     |   Design Pattern   |
-----------------------------------------------------------------------------
|                 |                   |                 | Game.java          |
|                 | Marcela Ramirez   | Builder Pattern | Leaderboard.html   |
|      mfr5829    | Vadillo           | UI: Formatting  | Results.html       |
|                 |                   | Data            | Teams.html         |
|                 |                   |                 | Player.html        |
------------------------------------------------------------------------------
|                 |                   |                    | Game.java          |
|                 | Claire Keef       | Strategy Pattern   | Compare.html       |
|      cek5677    |                   | UI: Search Filters |                    |
|                 |                   |                    |                    |
|                 |                   |                    |                    |
------------------------------------------------------------------------------
|                 |                   |                    |                    |
|                 | Matthew Bolger    | Prototype Pattern  |                    |
|      mmb7354    |                   | UI: Cards          |                    |
|                 |                   |                    |                    |
|                 |                   |                    |                    |
------------------------------------------------------------------------------

