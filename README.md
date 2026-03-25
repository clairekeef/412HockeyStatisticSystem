# 412HockeyStatisticSystem

How to Run without maven:
    Make sure you have Java installed 
    Open a terminal in the folder containing the .jar file
    Run the following command: java -jar HockeyDashboard-0.0.1-SNAPSHOT.jar
    Open your browser and go to: http://localhost:8080


How to run with maven: 
    To run on mac in terminal: mvn spring-boot:run
    To run on Windwos in terminal: ./mvnw.cmd spring-boot:run



    

Please include Readme file to specify the usage of particular input values that facilitate the demonstration of various functional-flows / scenarios of your software system.

Also include the effort contributions of each of the team-member in the readme file in following format:

Team Member ID + Name	Contribution in The Particular Assignment

cek5677 - Claire Keef
    Implemented Deshboard View (interacts with most model classes), Authentication Module, Testing for all classes implemented
    Adjusted UML diagram to match implementation
    Activity and Sequence Diagrams for Dashboard Overview use case

apd5982- Avery Dayal 
    Implemented: Statistics, Stats, PlayerStats, TeamStats, DataRepository, MetricCalculator, UserManagement, User, UserRole, Profile, Preferences; Created Tester tests for above classes; Added above classes/interfaces/enums to class UML Diagram; Activity and Sequence diagram for Compare Teams/Players Across Tournaments

mmb7354 - Matthew Bolger
    Implemented: PerformanceDataEvaluation and Team with testing for all classes implemented.
    I adjusted the UML diagram for each class that was updated throughout the coding process.
    I added all methods to both classes connecting them to the rest of the project.
    
mfr5829 - Marcela Ramirez Vadillo
    Implemented: Game and Game Summary classes. Tested these classes in the Tester and updated the UML diagram with all attributes and methods implemented. Also added activity and sequence diagram for View Game Information and Create Game Summary.
