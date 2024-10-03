# Hongyee Chronic Disease Management System

## Project Overview

The Hongyee Chronic Disease Management System is an enterprise-level backend management system designed to handle medical device data and user information. This system enables users to manage various entities such as medical devices, institutions, members, and resources. It provides multiple functionalities, including device management, user management, and statistical data queries, with users having roles such as institution users, partners, or administrators.

## Project Structure

The project consists of three main modules:

1. **chronicdisease-common**:
   - Contains shared dependencies and configuration files, including Maven's `pom.xml` for managing project dependencies.

2. **chronicdisease-data**:
   - Defines the data layer with persistent objects (PO), request objects (REQ), and response objects (RESP). These classes map to database tables and are used for transferring data between the front end and back end.
   - **PO** includes entities like `Member`, `Institution`, and `SysUser`.
   - **REQ/RESP** handle input and output for various business requests and responses.

3. **chronicdisease-management**:
   - Contains the core logic of the system, including the service layer, controller layer, caching, and configuration.
   - **Controller Layer** provides RESTful API endpoints for managing devices, users, institutions, and other resources.
   - **Service Layer** handles business logic, such as managing user data, devices, and statistics.
   - **Mapper Layer** interacts with the database using MyBatis to execute SQL queries.
   - **Cache Layer** uses Caffeine cache to optimize performance by reducing the frequency of database queries.
   - **Interceptor Layer** ensures security by handling user token authentication and authorization checks.

## Technologies Used

- **Spring Boot**: A Java framework for building enterprise applications, providing embedded servers, configuration management, and dependency injection.
- **MyBatis**: A persistence framework for mapping SQL queries to Java objects, facilitating database interaction.
- **Spring MVC**: Handles RESTful HTTP requests and maps them to Java methods.
- **Caffeine Cache**: A high-performance caching library to improve application performance by reducing database load.
- **JWT (JSON Web Token)**: Used for secure token-based authentication and authorization of users.
- **Aspect-Oriented Programming (AOP)**: Used to handle cross-cutting concerns like logging and security.

## Key Features

1. **User Management**: Allows managing users (institutions, partners, administrators) and their associated roles.
2. **Device Management**: Users can manage their medical devices and associated data, including the ability to add, edit, and delete devices.
3. **Security**: Implements token-based authentication using interceptors to ensure secure access to the system.
4. **Caching**: Utilizes Caffeine cache to improve system performance by storing frequently accessed data in memory.
5. **API Endpoints**: Provides a wide range of REST API endpoints to interact with users, devices, and resources.


## Configuration

- **application.yml**: The main configuration file located in `resources` defines environment-specific settings such as database connections and logging.
- **MyBatis Mappers**: Located in the `resources/mapper` directory, these XML files define SQL queries for interacting with the database.
