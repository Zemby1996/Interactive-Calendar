# Dynamic Calendar App

A web application that allows users to view available dates and book appointments or events.

## 🚀 Features

* **Interactive Calendar**: Browse available dates and book events seamlessly.
* **Authentication**: Secure login and registration using JWT.
* **Database Management**: All data is stored securely in PostgreSQL.

## 🛠️ Tech Stack

### Backend

* **Java 21** & **Spring Boot 3**
* **Spring Security** with JWT Authentication
* **Hibernate** & **Spring Data JPA**
* **PostgreSQL** (Hosted on [Neon.tech](https://neon.tech))

### Frontend

* **React** with JavaScript (ES6+)
* **CSS3** for styling
* **Libraries**:

  * `axios` for API communication
  * `react-toastify` for notifications
  * `react-router-dom` for navigation
  * `FullCalendar` for the interactive calendar component

## ⚙️ Installation

### Backend

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/your-repo.git
   cd backend
   ```
2. Configure the database in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://<your-neon-tech-db-url>
   spring.datasource.username=<your-db-username>
   spring.datasource.password=<your-db-password>
   ```
3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend

1. Navigate to the frontend directory:

   ```bash
   cd frontend
   ```
2. Install dependencies:

   ```bash
   npm install
   ```
3. Start the development server:

   ```bash
   npm start
   ```

## 📸 Screenshots


![image](https://github.com/user-attachments/assets/086f3582-5970-435a-bcc8-c1e441e7d83c)


## 🚀 Deployment

* **Backend**: Hosted on Render
* **Frontend**: Hosted on Netlify


## 📄 License

This project is licensed under the MIT License.
