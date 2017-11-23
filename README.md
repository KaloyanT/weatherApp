Weather App
==

This is a simple Weather App for personal usage, which can display the current weather in predefined cities around the world and can also save the weather
data for each day for each city. The idea is to be able to display a weather history for the given city and to compare the weather in different cities. 
I have planned to use Spring Boot and MySQL for the back-end and the database and React for the front-end. Currently the front-end project is missing
since I am still learning React. 

Back end: 
1. JDK 1.8+
2. Maven for building the JAR files 

Technologies: 
Java, Spring Boot, MySQL, Hibernate

The back-end project will require a database connection in order to start, so you will need to specify one inside the application.properties file. 
This is, of course, if you attempt to run the back-end part of the project on it's own.

Front end: 
1. Node.js + NPM 
2. React

Technologies: 
HTML/CSS/JavaScript, React, JSX, Node.js, Bootstrap (3)

Deployment: 
1. Docker

## Start the project/system (Front-End Client + Back-End): 

Currently the project is configured to run on one machine with every component running inside its own Docker Container. 

1. Clone or download the repository with both frond-end and back-end projects
2. Navigate to the backend directory, open a terminal/cmd and write: "mvn clean package -DskipTests docker:build"
3. Navigate to the root directory of the project, open a terminal/cmd and write: "docker-compose up --build --force-recreate -d"