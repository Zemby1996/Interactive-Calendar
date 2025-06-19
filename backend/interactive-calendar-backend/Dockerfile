# --- ETAP 1: Budowanie Aplikacji ---
# ZMIANA: Używamy nowszego obrazu z JDK 21, aby pasował do pom.xml
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Ustawiamy katalog roboczy wewnątrz kontenera
WORKDIR /app

# Kopiujemy plik pom.xml, aby pobrać zależności
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiujemy resztę kodu źródłowego
COPY src ./src

# Uruchamiamy komendę budowania Mavena
RUN mvn package -DskipTests


# --- ETAP 2: Tworzenie Finalnego, Lekkiego Obrazu ---
# ZMIANA: Używamy nowszego obrazu z JRE 21 dla spójności
FROM eclipse-temurin:21-jre-jammy

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy skompilowany plik .jar z etapu budowania
COPY --from=build /app/target/interactive-calendar-backend-0.0.1-SNAPSHOT.jar app.jar

# Ustawiamy port, na którym aplikacja będzie nasłuchiwać
EXPOSE 10000

# Komenda uruchamiająca aplikację po starcie kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]