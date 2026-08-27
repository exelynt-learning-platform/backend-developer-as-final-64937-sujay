# Resource Booking System

A secure RESTful Resource Booking System built using Spring Boot, Spring
Security, JWT Authentication, JPA/Hibernate, and MySQL.

The application allows users to view available resources and
create/manage their own reservations, while administrators have full
access to manage resources and reservations.

------------------------------------------------------------------------

## Assignment

This project was developed as part of the **EXELYNT Backend Developer
Assignment**.

### Objective

Build a secure RESTful Resource Booking System with:

-   JWT Authentication
-   Role-Based Access Control
-   Resource management
-   Reservation management
-   Reservation ownership
-   Filtering
-   Pagination
-   Sorting
-   Validation
-   MySQL database integration
-   Swagger/OpenAPI documentation
-   Automated testing

------------------------------------------------------------------------

## Features

-   JWT-based authentication
-   BCrypt password hashing
-   ADMIN and USER roles
-   Role-Based Access Control (RBAC)
-   Stateless JWT authentication
-   Secure REST APIs
-   Resource CRUD operations
-   Reservation CRUD operations
-   Reservation ownership protection
-   Reservation statuses:
    -   PENDING
    -   CONFIRMED
    -   CANCELLED
-   Reservation filtering:
    -   Status
    -   Minimum price
    -   Maximum price
-   Pagination using `page` and `size`
-   Optional sorting
-   Request validation
-   Centralized exception handling
-   MySQL database integration
-   JPA/Hibernate
-   Swagger/OpenAPI documentation
-   Seed users for testing
-   Unit tests for authentication, authorization, ownership, and
    validation

------------------------------------------------------------------------

## Technology Stack

-   Java 21
-   Spring Boot 4.1.1
-   Spring Security
-   JWT (JJWT 0.13.0)
-   Spring Data JPA
-   Hibernate
-   MySQL
-   Maven
-   Lombok
-   Springdoc OpenAPI / Swagger
-   JUnit 5
-   Mockito

------------------------------------------------------------------------

## Prerequisites

Make sure the following are installed:

-   Java 17 or higher
-   Maven
-   MySQL 8+
-   Git

This project uses **Java 21**.

------------------------------------------------------------------------

## Project Structure

``` text
src
├── main
│   └── java
│       └── com.exelynt.booking
│           ├── auth
│           ├── config
│           ├── exception
│           ├── reservation
│           ├── resource
│           ├── security
│           └── user
│
└── test
    └── java
        └── com.exelynt.booking
            ├── ResourceBookingSystemApplicationTests.java
            │
            ├── auth
            │   └── service
            │       └── AuthenticationServiceTest.java
            │
            └── reservation
                └── service
                    ├── ReservationServiceOwnershipTest.java
                    └── ReservationValidationTest.java
```

------------------------------------------------------------------------

## Database Setup

The application uses **MySQL** with Spring Data JPA and Hibernate.

Create the database:

``` sql
CREATE DATABASE resource_booking_system;
```

The application automatically creates and updates the required tables
using Hibernate:

``` properties
spring.jpa.hibernate.ddl-auto=update
```

------------------------------------------------------------------------

## Environment Variables

The application supports environment variables for database and JWT
configuration.

### Variables

``` text
DB_URL=jdbc:mysql://localhost:3306/resource_booking_system
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_long_random_secret_key
JWT_EXPIRATION=86400000
```

The application properties use environment variables with local
development defaults:

``` properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/resource_booking_system}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:your_mysql_password}

jwt.secret=${JWT_SECRET:change-this-to-a-long-random-secret-key-at-least-32-characters}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

### Security Note

Do not commit real database passwords or production JWT secrets to
GitHub.

------------------------------------------------------------------------

## Running the Application

### 1. Clone the repository

``` bash
git clone <your-github-repository-url>
```

### 2. Navigate to the project

``` bash
cd resource-booking-system
```

### 3. Create the MySQL database

``` sql
CREATE DATABASE resource_booking_system;
```

### 4. Configure environment variables

Set the following variables:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
```

### 5. Build the project

``` bash
mvn clean install
```

### 6. Run the application

``` bash
mvn spring-boot:run
```

The application runs on:

``` text
http://localhost:8080
```

------------------------------------------------------------------------

# Authentication

## Login

``` http
POST /auth/login
```

### Request

``` json
{
    "username": "user",
    "password": "User@123"
}
```

### Response

``` json
{
    "token": "<JWT_TOKEN>",
    "username": "user",
    "role": "USER"
}
```

Use the returned JWT token for protected endpoints:

``` http
Authorization: Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# Seed Users

The application provides seed users for testing.

## ADMIN

``` text
Username: admin
Password: Admin@123
Role: ADMIN
```

## USER

``` text
Username: user
Password: User@123
Role: USER
```

Passwords are stored using BCrypt hashing.

------------------------------------------------------------------------

# Authorization & RBAC

## ADMIN

ADMIN users have full access to resources and reservations.

ADMIN can:

-   Create resources
-   View resources
-   Update resources
-   Delete resources
-   Create reservations
-   View all reservations
-   Update reservations
-   Delete reservations
-   Update reservation status

## USER

USER users can:

-   View resources
-   Create reservations
-   View their own reservations
-   Update their own reservations
-   Delete their own reservations

USER users cannot:

-   Create resources
-   Update resources
-   Delete resources
-   Access another user's reservation
-   Perform ADMIN-only operations

The authenticated user's identity is obtained from the JWT.

A `userId` is not accepted from the reservation request to determine
ownership.

------------------------------------------------------------------------

# Resource APIs

## Create Resource

``` http
POST /resources
```

**ADMIN only**

Example request:

``` json
{
    "name": "Meeting Room A",
    "description": "Large meeting room",
    "type": "ROOM",
    "price": 1500.00,
    "available": true
}
```

## Get All Resources

``` http
GET /resources
```

Accessible by:

-   ADMIN
-   USER

## Get Resource by ID

``` http
GET /resources/{id}
```

Accessible by:

-   ADMIN
-   USER

## Update Resource

``` http
PUT /resources/{id}
```

**ADMIN only**

## Delete Resource

``` http
DELETE /resources/{id}
```

**ADMIN only**

------------------------------------------------------------------------

# Reservation APIs

## Create Reservation

``` http
POST /reservations
```

The authenticated user's identity is taken from the JWT.

Example:

``` json
{
    "resourceId": 1,
    "startTime": "2026-09-01T10:00:00",
    "endTime": "2026-09-01T12:00:00",
    "price": 1500.00
}
```

New reservations are created with:

``` text
PENDING
```

## Get Reservations

``` http
GET /reservations
```

Supported query parameters:

``` text
status
minPrice
maxPrice
page
size
sortBy
sortDirection
```

Example:

``` http
GET /reservations?status=CONFIRMED&minPrice=1000&maxPrice=5000&page=0&size=10&sortBy=createdAt&sortDirection=desc
```

### ADMIN

ADMIN can view all reservations matching the filters.

### USER

USER can view only their own reservations.

## Get Reservation by ID

``` http
GET /reservations/{id}
```

ADMIN can access any reservation.

USER can access only their own reservation.

## Update Reservation

``` http
PUT /reservations/{id}
```

Users can update their own reservations.

ADMIN can update reservations.

## Delete Reservation

``` http
DELETE /reservations/{id}
```

Users can delete their own reservations.

ADMIN can delete reservations.

## Update Reservation Status

``` http
PATCH /reservations/{id}/status
```

Supported statuses:

``` text
PENDING
CONFIRMED
CANCELLED
```

------------------------------------------------------------------------

# Filtering

Reservations can be filtered using:

### Status

``` http
GET /reservations?status=CONFIRMED
```

### Minimum Price

``` http
GET /reservations?minPrice=1000
```

### Maximum Price

``` http
GET /reservations?maxPrice=5000
```

### Combined Filters

``` http
GET /reservations?status=PENDING&minPrice=1000&maxPrice=5000
```

------------------------------------------------------------------------

# Pagination

Pagination is supported using:

``` text
page
size
```

Example:

``` http
GET /reservations?page=0&size=10
```

Example for the second page:

``` http
GET /reservations?page=1&size=10
```

Pages are zero-based.

------------------------------------------------------------------------

# Sorting

Reservation results support optional sorting.

Parameters:

``` text
sortBy
sortDirection
```

Example:

``` http
GET /reservations?sortBy=price&sortDirection=asc
```

Example:

``` http
GET /reservations?sortBy=createdAt&sortDirection=desc
```

------------------------------------------------------------------------

# Validation

The application validates:

-   Required resource ID
-   Required start time
-   Required end time
-   Required price
-   Price must be greater than zero
-   Start time must be in the future
-   End time must be in the future
-   Start time must be before end time
-   Reservation status must be valid

Invalid requests return:

``` text
400 Bad Request
```

------------------------------------------------------------------------

# Resource Availability

Reservations cannot be created for resources marked as unavailable.

Example:

``` text
Resource available = false
        ↓
Reservation request
        ↓
400 Bad Request
```

Response message:

``` text
Resource is not available
```

------------------------------------------------------------------------

# Error Handling

The application uses centralized exception handling.

Supported HTTP status codes include:

``` text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

### 400 Bad Request

Used for invalid request data, validation failures, invalid status
values, and business-rule violations.

### 401 Unauthorized

Returned when authentication is required but a valid JWT is not
provided.

### 403 Forbidden

Returned when an authenticated user does not have permission to access a
resource.

### 404 Not Found

Returned when the requested resource, reservation, or user does not
exist.

### 500 Internal Server Error

Reserved for unexpected server-side errors.

------------------------------------------------------------------------

# Swagger / OpenAPI

Swagger UI:

``` text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

``` text
http://localhost:8080/v3/api-docs
```

Swagger can be used to explore and test the available REST APIs.

------------------------------------------------------------------------

# Testing

The project includes automated tests using JUnit 5 and Mockito.

## Authentication Tests

Tests include:

-   Valid login generates a JWT response
-   Invalid credentials are rejected

## Reservation Ownership Tests

Tests include:

-   USER can access their own reservation
-   USER cannot access another user's reservation
-   ADMIN can access another user's reservation
-   Missing reservation throws the appropriate exception

## Reservation Validation Tests

Tests include:

-   Unavailable resource is rejected
-   Invalid reservation time range is rejected

## Application Context Test

The project also includes a Spring Boot application context test.

## Run Tests

Run all tests using:

``` bash
mvn test
```

For a clean test run:

``` bash
mvn clean test
```

------------------------------------------------------------------------

# Security

The application uses:

-   Spring Security
-   JWT-based authentication
-   Stateless authentication
-   BCrypt password hashing
-   Role-Based Access Control
-   Protected REST endpoints
-   Reservation ownership validation
-   Centralized security exception handling

JWT tokens contain the authenticated username and are validated before
protected endpoints are accessed.

### Security Notes

The application uses JWT Bearer token authentication with stateless sessions.

CSRF protection is disabled because the API does not use server-side browser sessions or cookies for authentication. Clients authenticate requests by sending the JWT in the `Authorization: Bearer <token>` header.

Spring Security is configured with `SessionCreationPolicy.STATELESS`, so the server does not maintain authentication sessions.

State-changing endpoints require a valid JWT and appropriate role-based authorization.

------------------------------------------------------------------------

# Database Model

The application contains the following main entities:

``` text
User
Resource
Reservation
```

A reservation belongs to:

``` text
User
Resource
```

Relationship:

``` text
User
  │
  │ 1
  │
  │ *
Reservation
  │
  │ *
  │
  │ 1
Resource
```

Reservation price is stored as a decimal value using:

``` text
BigDecimal
```

------------------------------------------------------------------------

# API Status Codes

Operation                 Status
  ------------------------- ---------------------------
Successful GET            200 OK
Successful POST           201 Created
Successful PUT/PATCH      200 OK
Successful DELETE         204 No Content
Invalid request           400 Bad Request
Missing authentication    401 Unauthorized
Insufficient permission   403 Forbidden
Resource not found        404 Not Found
Unexpected error          500 Internal Server Error

------------------------------------------------------------------------

# Assignment Evaluation Coverage

The implementation covers the major EXELYNT evaluation areas:

-   **Authentication** --- JWT login and BCrypt password handling
-   **Authorization & RBAC** --- ADMIN and USER permissions
-   **Security** --- protected endpoints and ownership protection
-   **CRUD Operations** --- resources and reservations
-   **Reservation Ownership** --- USER own reservations only
-   **Validation** --- request, price, status, and time validation
-   **Filtering** --- status, minimum price, maximum price
-   **Pagination & Sorting** --- page, size, and sorting parameters
-   **Database** --- MySQL, JPA/Hibernate, and entity relationships
-   **API Design** --- REST endpoints and HTTP status codes
-   **Error Handling** --- centralized exception handling
-   **Code Quality** --- layered architecture
-   **Testing** --- authentication, authorization, ownership,
    validation, and application context

------------------------------------------------------------------------

# Project Information

**Project:** Resource Booking System

**Assignment:** EXELYNT Backend Developer Assignment

**Backend:** Spring Boot

**Database:** MySQL

**Authentication:** JWT

**Authorization:** Spring Security + RBAC

**Documentation:** Swagger/OpenAPI

**Testing:** JUnit 5 + Mockito
