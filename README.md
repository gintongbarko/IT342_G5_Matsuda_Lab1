# ⏳ Employee Timesheet Tracker (Unofficial name)

A web-based application designed to manage employee time tracking and attendance records.

🔗 **Live Deployment:**
👉 Coming soon

------------------------------------------------------------------------

## 📌 Overview

The Employee Timesheet Tracker is a full-stack web and mobile application designed to streamline employee time management and attendance tracking across multiple platforms.

------------------------------------------------------------------------

## 🛠️ Tech Stack

### **Frontend:**

  **React** - UI library for building interactive user interfaces
                                   
  -----------------------------------------------------------------------

### **Backend:**

  **Spring Boot (API Layer)** - Java-based framework for building REST APIs

  -----------------------------------------------------------------------

### **Database:**

  **MySQL** - Relational database for data persistence

  -----------------------------------------------------------------------

### **Authentication:**

  **TBD** - TBD

  -----------------------------------------------------------------------

### **Mobile:**

  **Kotlin** - Programming language responsible for building mobile apps

  -----------------------------------------------------------------------

  ### **Password Encryption:**

  **BCrypt** - Adaptive password hashing function designed to secure passwords by transforming it into unreadable hashes

  -----------------------------------------------------------------------

### **Deployment:**

  **TBD** - TBD

------------------------------------------------------------------------

## 📂 Project Structure

(Current as of latest commit)

    IT342_G5_Matsuda_Lab1/
    ├── backend/
    ├── docs/
    ├── mobile/
    ├── web/
    ├── README.md           #This file
    └── TASK_CHECKLIST.md

------------------------------------------------------------------------

## ⚙️ Installation & Setup

### Backend (Spring Boot)

1. Create MySQL database (optional - app creates it if configured):
   ```sql
   CREATE USER 'timesheets'@'localhost' IDENTIFIED BY 'your-password-here';
   GRANT ALL PRIVILEGES ON timesheets_db.* TO 'timesheets'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. Configure database in `backend/src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/timesheets_db?...
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Run backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Backend runs at http://localhost:8080

### Frontend (React)

1. Copy env example and configure API URL (optional, defaults to http://localhost:8080):
   ```bash
   cd web
   cp .env.example .env
   # Edit .env: REACT_APP_API_URL=http://localhost:8080
   ```

2. Run frontend:
   ```bash
   npm install
   npm start
   ```
   Frontend runs at http://localhost:3000

------------------------------------------------------------------------

## 📡 API Endpoints

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login (returns JWT)
- `POST /api/auth/logout` - Logout (invalidates session)
- `GET /api/user/me` - Get current user (protected, requires Bearer token)

------------------------------------------------------------------------

## 🤝 How to Contribute

### **1. Clone this repository**

``` bash
git clone https://github.com/sanggreterra/IT342_G5_Matsuda_Lab1.git
```

### **2. Create a Branch**

``` bash
git checkout -b feature/my-feature
```

### **3. Commit Changes**

### **4. Push & Submit PR**

Note: Passwords are hashed with BCrypt. JWT tokens are stored in localStorage.