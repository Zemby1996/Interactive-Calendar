# --- ETAP 1: Budowanie Aplikacji ---
# Używamy oficjalnego obrazu Mavena z Javą 17 do zbudowania naszego projektu
FROM maven:3.8.5-openjdk-17 AS build

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
# Używamy lekkiego obrazu zawierającego tylko środowisko uruchomieniowe Javy (JRE)
FROM eclipse-temurin:17-jre-jammy

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy skompilowany plik .jar z etapu budowania
COPY --from=build /app/target/interactive-calendar-backend-0.0.1-SNAPSHOT.jar app.jar

# Ustawiamy port, na którym aplikacja będzie nasłuchiwać
EXPOSE 10000

# Komenda uruchamiająca aplikację po starcie kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]# --- ETAP 1: Budowanie Aplikacji ---
# Używamy oficjalnego obrazu Mavena z Javą 17 do zbudowania naszego projektu
FROM maven:3.8.5-openjdk-17 AS build

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
# Używamy lekkiego obrazu zawierającego tylko środowisko uruchomieniowe Javy (JRE)
FROM eclipse-temurin:17-jre-jammy

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy skompilowany plik .jar z etapu budowania
COPY --from=build /app/target/interactive-calendar-backend-0.0.1-SNAPSHOT.jar app.jar

# Ustawiamy port, na którym aplikacja będzie nasłuchiwać
EXPOSE 10000

# Komenda uruchamiająca aplikację po starcie kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]