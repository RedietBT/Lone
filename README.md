# Loan Management System

A robust and secure web application for managing loan applications, approvals, and repayment schedules, built with Spring Boot and Thymeleaf. It provides distinct interfaces for customers to apply for and view their loans, and for administrators to review and manage all loan applications.

## Table of Contents

1.  [Features](#features)
2.  [Technologies Used](#technologies-used)
3.  [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Cloning the Repository](#cloning-the-repository)
    * [Database Setup](#database-setup)
    * [Configuration](#configuration)
    * [Running the Application](#running-the-application)
4.  [Usage](#usage)
    * [Web UI (Thymeleaf)](#web-ui-thymeleaf)
    * [REST API Endpoints](#rest-api-endpoints)
5.  [Security](#security)
6.  [Project Structure Highlights](#project-structure-highlights)
7.  [Future Enhancements](#future-enhancements)
8.  [License](#license)

## Features

* **User Authentication & Authorization:**
    * Secure registration and login for customers and administrators.
    * Role-based access control (RBAC) using Spring Security.
    * JWT-based authentication for REST API endpoints.
    * Session-based authentication for Thymeleaf web UI.
* **Customer Functionality:**
    * Apply for new loans with various details (amount, type, duration, purpose, income).
    * View a personalized dashboard of all submitted loans.
    * Track loan status (PENDING, APPROVED, REJECTED).
    * View detailed repayment schedules for approved loans.
* **Administrator Functionality:**
    * Access an admin dashboard to oversee all loan applications.
    * Filter and view pending loan applications.
    * Review detailed information for individual loan applications.
    * Approve or Reject loan applications with optional remarks.
    * Automatic EMI and repayment schedule calculation upon approval.
* **Loan Management:**
    * Storage and retrieval of loan application details.
    * Dynamic generation and storage of repayment schedules.
    * Tracking of outstanding balances and remaining payments.

## Technologies Used

* **Backend:**
    * **Spring Boot 3.5.0:** Rapid application development framework.
    * **Spring Data JPA:** For database interaction and ORM.
    * **Hibernate:** JPA implementation.
    * **Spring Security:** For authentication and authorization (JWT & Session-based).
    * **Lombok:** Reduces boilerplate code.
    * **Jackson (included with Spring Boot):** For JSON serialization/deserialization.
    * **H2 Database (default):** In-memory database for quick setup and development. Can be easily swapped for PostgreSQL, MySQL, etc.
* **Frontend:**
    * **Thymeleaf:** Server-side templating engine for dynamic HTML views.
    * **HTML5, CSS3, JavaScript:** Standard web technologies.
* **Build Tool:**
    * **Maven**

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* **Java 24 (or newer):** Ensure Java Development Kit (JDK) 17 or a compatible version is installed.
* **Maven** Ensure Maven is installed and configured in your system's PATH.
* **IDE:** An IDE like IntelliJ IDEA, Eclipse, or VS Code with Spring Boot support.
* **(Optional) Postman or cURL:** For testing REST API endpoints.

### Cloning the Repository

```bash
git clone https://github.com/RedietBT/Lone.git
cd loan
