FROM maven:3.10-amazoncorretto-25-alpine

WORKDIR /backend
COPY . .
RUN mvn clean package -DskipTests

CMD ["mvn", "spring-boot:run"]
