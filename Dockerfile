#Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

#Set the working directory in the container
WORKDIR /app

#Copy the project's jar file into the container at /app
COPY target/be-project-0.0.1-SNAPSHOT.war /app/perwatt.war
#Make port 8080 available to the world outside this container

EXPOSE 8080

#Run the jar file
ENTRYPOINT ["java", "-jar", "/app/perwatt.war"]
