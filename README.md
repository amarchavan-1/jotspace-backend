<div align="center">
  
# 📝 JotSpace API Backend

**A Secure, Production-Ready Spring Boot API for the JotSpace Ecosystem**

[![Render Deployment](https://img.shields.io/badge/Render-Live-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://jotspace-backend.onrender.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](#)
[![Java 17](https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=java&logoColor=white)](#)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](#)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](#)

</div>

---

## 📖 Overview

This repository hosts the **Core Backend API** for JotSpace, built with Java 17, Spring Boot 3, and MongoDB. It acts as the backbone of the application, managing secure user authentication, complex data persistence, relational entity connections, and rapid API delivery.

Designed for robust production deployment on **Render**, this RESTful API is stateless, utilizing modern Spring Security implementations and highly specific CORS configurations to enable seamless interaction with our external React frontend.

---

## ✨ Core Features & Capabilities

- **🔐 Stateless JWT Security:** Implements JWT payload generation natively. Tokens are injected directly into cross-site HTTP-only cookies (`SameSite=None`, `Secure=true`) to completely prevent XSS extraction attacks.
- **🛡️ Global Exception Handling:** A customized ControllerAdvice framework translates internal exceptions (like `BadCredentialsException` or constraint violations) into standardized, predictable JSON error contracts.
- **📄 Extensible Document Store:** Employs `spring-boot-starter-data-mongodb` for highly flexible JSON-like NoSQL data structures, ideal for dynamic note contents and customizable user schemas.
- **📦 Relational Emulation:** Handles strict user-ownership validation. Users can exclusively access, mutate, or soft-delete notes actively attached to their authentication principal.
- **📚 Automated API Documentation:** Integrated with `springdoc-openapi` to automatically generate interactive Swagger UI endpoints natively reflecting the API’s state.

---

## 🛠️ Technology Stack (Backend)

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Framework** | **Spring Boot 3.2.x** | Enterprise foundation. |
| **Language** | **Java 17** | Modern syntax and robust compile-time safety. |
| **Database** | **MongoDB Atlas** | Managed cloud NoSQL storage. |
| **Security** | **Spring Security 6** | Request interception, filters, and authentication managers. |
| **Tokens** | **jjwt-api** | Cryptographic generation of authentication signatures. |
| **Docs** | **Swagger UI / OpenAPI** | Real-time endpoint contracts. |

---

## 🏗️ Architecture Design

The backend enforces a strict layered architectural pattern, ensuring loose coupling and high testability:

1. **Controller Layer (`/controller`)**: Maps HTTP requests to service delegates and formats outgoing ResponseEntity payloads.
2. **Service Layer (`/service`)**: Encapsulates 100% of the business logic, JWT issuance validation, and database orchestration.
3. **Repository Layer (`/repository`)**: Extends MongoDB interfaces for abstracted, direct database interactions.
4. **Security Filters (`/security`)**: Stateless `OncePerRequestFilter` checks each incoming request for valid `jwt` cookies, parses claims, and constructs the Spring `SecurityContext`.

---

## 💻 Local Setup & Installation

Follow these steps to run the backend application locally:

### 1. Pre-requisites
- **Java 17 JDK** (Oracle, OpenJDK, or Amazon Corretto)
- **Maven** (A Maven wrapper `mvnw` is included in the project)
- A **MongoDB Atlas** cluster URI (or local MongoDB running on port 27017).

### 2. Clone the Repository
```bash
git clone https://github.com/amarchavan-1/Personal-Notes-Application-Backend.git
cd Personal-Notes-Application-Backend
```

### 3. Configure Environment Properties
Create or modify the `src/main/resources/application.properties` file with your credentials:
```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.mongodb.net/jotspace
jwt.secret=YourSuperSecretKeyWithAtLeast256BitsOfCryptographicStrength==
jwt.expiration=86400000
```

### 4. Build and Run
```bash
./mvnw clean compile
./mvnw spring-boot:run
```
The server will start locally at `http://localhost:8080`.

---

## 📚 API References

Once the application is running, the interactive API documentation can be accessed securely via your browser:
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` (or `<render_url>/swagger-ui/index.html`)
- **OpenAPI JSON Spec:** `http://localhost:8080/v3/api-docs`

---

## 🧪 Testing

The application implements rigorous unit testing using **JUnit 5**, **Mockito**, and **Spring MockMvc** to guarantee the integrity of API endpoints without hitting the active database.

- **MockMvc Slicing:** Controller tests (like `AuthControllerTest`) are cleanly isolated using `@WebMvcTest`.
- **Security Bypasses:** Security filters are strategically bypassed during controller-only slice tests (`@AutoConfigureMockMvc(addFilters = false)`) to enable hyper-focused unit validation of request and response structures.
- **Service Mocking:** Business logic and database operations are mocked out using `@MockBean` to ensure rapid, deterministic testing cycles.

Run the test suite locally with:
```bash
./mvnw test
```

---

## 🚀 Deployment

The backend application is orchestrated for deployment on **Render.com** (Web Service).
- **Environment Details:** Utilizing Docker/Java native builds.
- **CORS Strategy:** Specifically customized `CorsConfigurationSource` allows origins (`*`) while ensuring `allowCredentials(true)` is strictly enforced for cross-origin local interaction.
