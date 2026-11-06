FROM maven:3.10-amazoncorretto-25-alpine

WORKDIR /backend

COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests

COPY src ./src
RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/MyMedsBackend-0.0.1-SNAPSHOT.jar"]