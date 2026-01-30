# Stage 1: Build (imagine cu Maven + JDK 17)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiază pom.xml și descarcă dependențele
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiază codul sursă și construiește
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime (doar JRE, imagine mică)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

#dependency:go-offline: descarcă dependențele înainte de COPY src, astfel cache-ul Docker se păstrează când schimbi doar codul
#-DskipTests: build-ul nu rulează teste (le poți rula separat sau într-un alt stage)