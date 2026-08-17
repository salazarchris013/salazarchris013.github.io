# salazarchris013.github.io

# Christopher Salazar ePortfolio

# Self-Assessment
I've enjoyed my years learning to code and exploring many different areas of computer science at SNHU. The CS 499 project and milestones gave me the opportunity to bring some of my skills in software engineering, databases, and algorithms together by reviewing and enhancing an Android inventory management app I created in a previous CS 360 course. The development of my ePortfolio also provided me the opportunity to revisit that work, include feedback and make further improvements, instead of treating it as finished work. This process taught me a lot about how to maintain code, how important it is to have reliable data, security and clear communication.

My coursework has prepared me well to work alongside other developers. I completed the capstone on my own, but I treated my code as though someone else would eventually need to maintain it, building a code review presentation, building on instructor feedback, and commenting on my classes and methods to keep them easy to follow. As part of that process, I recorded a code review, walking through the code files and what I planned to enhance and why, along with written explanations to make it understandable to someone not familiar with the project. My full stack development CS 465 course reinforced this. Building the Travlr Getaways application with Angular, Node.js, Express, MongoDB, and REST APIs taught me how the different components of a larger program work together. Designing it around business and user requirements and then converting those requirements into technical decisions other developers could follow gave me an early introduction to balancing the needs of a business against the needs of its users. 

I've applied data structures and algorithms across more than one area of computer science. In CS 370, I built a Treasure Hunt Game using deep Q learning in a maze, which taught me how algorithms can learn from experience to make decisions. In this project, I used an in memory list to make searching and sorting inventory faster. Together, these experiences showed me how much an application's behavior and performance depend on structural and algorithmic choices.

My software engineering courses taught me how to organize software and manage data efficiently, from building and connecting the front end and back end of the full stack project, to separating database operations from input validation in the capstone so each could be reused independently. My database background includes relational design, normalization, foreign keys, SQL queries, and data migration, all with an emphasis on building software that can be maintained and extended over time.

Building secure software is a huge part of my software development process. As part of my security coursework in C++, I explored vulnerabilities in a variety of existing programs, such as hardcoded credentials, and how to secure them using secure coding practices. I applied those same principles to the capstone with input validation, parameterized SQL queries, safe handling of passwords, and safe database upgrades. That experience changed my mind about security, it was not just a nice to have, but something I thought about throughout the development process and not just at the end.

My ePortfolio brings these skills together through three enhancements to the same inventory application. The Software Design and Engineering artifact separates responsibilities into reusable components. The Algorithms and Data Structures artifact improves how the application searches and sorts data. The Databases artifact restructures the data model around related tables and JOIN queries. This shows how one application can be strengthened through better design, performance, and data management. While these artifacts represent the focus of my ePortfolio and project, it’s only a part of what I learned throughout the Computer Science program. Through the Travlr Getaways application, CS 370 Treasure Hunt Game, C++ security coursework and this final project, I have demonstrated my ability to apply these skills to solve problems, improve existing software, and translate requirements into technical solutions.

# Code Review
https://youtu.be/QhCGARdW1TY

# Algorithms and Data Structures
Inventory Management App

[View original code](https://github.com/salazarchris013/salazarchris013.github.io/tree/main/Algorithms%20and%20Data%20Structures%20-%20Inventory%20Tracker/app-original/src)

[View source code](Algorithms%20and%20Data%20Structures%20-%20Inventory%20Tracker/app-updated/src)

This enhancement involved rethinking the way that the app works with its inventory data. Rather than accessing the database each time the screen was changed, I created a class for each inventory item and stored these in a list during the app's life cycle making it possible to filter inventory logs without repeated database accesses. This met the course outcomes by selecting an appropriate data organization and keeping data retrieval separate from the way the information is displayed on the screen. Being able to filter items, along with instant search, required very little additional work because organizing the inventory this way made those features much easier to implement. One challenge was that every edit had to update both the list and the database, or the app could display information that did not match what was actually saved. Finding and fixing this issue taught me the importance of keeping data the same when working through the project.

# Databases
Inventory Management App

[View original code](https://github.com/salazarchris013/salazarchris013.github.io/tree/main/Databases%20-%20Inventory%20Tracker/app-original/src)

[View source code](Databases%20-%20Inventory%20Tracker/app-updated/src)

This artifact is an inventory management app that I originally created in CS360 that I chose for Databases category. I chose this artifact because it shows my ability to redesign a database, enhance data organization, and expand an existing application without altering the primary purpose of the application. The main changes are in DatabaseHelper.java, where the database is redesigned with the separation of the inventory data into categories, suppliers, inventory items and transaction tables, including JOIN queries to get related data, and in InventoryImporter.java adding a CSV import feature with sample_inventory.csv to populate the database for testing. I also made some changes in InventoryRepository.java, InventoryItem.java, and database.java to allow the app to add and view the new category and supplier data. These improvements show that I created a general understanding of relational database design, database normalization, foreign keys, and data retrieval.

In Module One, I determined that the following course outcomes were met by this artifact. I was able to design and implement a database solution that enhances an existing software application. The changes made improved functionality, without removing existing features, or reorganizing the data. In this project, I got to know how to change the structure of a database without losing any information, how to make relationships between tables, and how to use JOIN queries to get data in a related and efficient way. A major difficulty was that the larger the inventory, the slower the application, so changes had to be made to the user interface and more testing needed to make the application more responsive. Solving these problems gave me more insight into database design, data integrity and the need to test database changes to ensure that the application would be reliable and would fulfill the project requirements.

# Software Design and Engineering
Inventory Management App

[View original code](Software%20Design%20and%20Engineering%20-%20Inventory%20Tracker/app-original/src)

[View updated code](Software%20Design%20and%20Engineering%20-%20Inventory%20Tracker/app-updated/src)

This is an upgraded version of the Inventory Management application that I developed as part of my CS360 course. I added it to my ePortfolio as an example of how my coding techniques can be improved to enhance code organization and maintainability. I separated the responsibility of accessing the database into an InventoryRepository class and input validation into an InputValidator class. This enhancement achieved my intended course outcomes and demonstrated software engineering principles such as separation of responsibilities and code reusability. It was an important learning experience for me because I learned that good software is not just about making an application function, it is also about making it easier to maintain and update. The biggest challenge I faced was ensuring that the new classes interacted properly with the existing inventory features, so I had to thoroughly test the application.
