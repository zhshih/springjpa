# Simple Web Application with Spring Boot 3

A simple web application built using **Spring Boot 3**, **Spring Data JPA**, with a focus on **resilience** and **observability**.  
This application allows you to perform CRUD operations on a collection of books.

## Features
- **Spring Boot 3** for rapid, efficient web service development
- **Spring Data JPA** for seamless database persistence
- **WebSocket support** to receive live ICU signal streams
- **RESTful APIs** to query ICU signal data
- **Resilience & Observability** integrated via logging, metrics, and tracing
- Built with **clean architecture**: DTOs, mappers, service layer, repository layer

## System Overview

This application acts as an **ICU data receiver**.  
It listens for ICU signal data sent by the **ICUSimulator** over WebSocket and stores them into a relational database.  
It also provides REST APIs to:
- Upload signals manually
- Retrieve signal data in a time range
- Fetch the latest ICU readings for a given patient

## Architecture
```plaintext
    ICUSimulator ---> WebSocket (ICUSignalWebSocketHandler)
    ↓
    ICUController (REST APIs)
    ↓
    ICUService
    ↓
    ICURepository (JPA)
    ↓
    Database (H2/MySQL/etc.)
 ```

## REST API Endpoints

| Method | Endpoint | Description | Request Params / Body | Example |
|--------|-----------|--------------|------------------------|----------|
| **POST** | `/api/v1/icu/upload` | Upload or receive a new ICU signal record. | JSON body with patient data. | ```json { "nationalId": 123456, "timestamp": "2025-11-09T14:30:00", "heartbeat": 85, "pulse": 97 } ``` |
| **GET** | `/api/v1/icu/range/{nationalId}` | Get all ICU signals for a patient within a given time range. | `start`, `end`, `page`, `size` | `/api/v1/icu/range/123456?start=2025-11-09T00:00:00&end=2025-11-09T23:59:59&page=0&size=10` |
| **GET** | `/api/v1/icu/latest/{nationalId}` | Get the latest ICU signals for a specific patient. | `page`, `size` | `/api/v1/icu/latest/123456?page=0&size=20` |

**Response format (common across APIs):**
```json
{
  "message": "Fetched latest ICU signals",
  "data": {
    "content": [
      { "timestamp": "2025-11-09T14:45:00", "heartbeat": 87, "pulse": 96 }
    ]
  }
}
```

## Technology Stack
* **Spring Boot 3**
* **Spring Data JPA** for database access
* **Resilience** (via Resilience4j or similar)
* **Observability** (metrics, logging, tracing)
* H2/MySQL/PostgreSQL (choose your database)

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd icu-receiver
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```
   
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

    The app will start on http://localhost:8080.

4. (Optional) Run with ICUSimulator:
    
    To test the WebSocket streaming, run the ICUSimulator application in parallel.
    It will connect to this service and stream simulated ICU data in real-time.

## Observability & Resilience
* Logs: Enabled via SLF4J + Logback (/logs or console output)
* Metrics & Health Checks: Available under /actuator
* Tracing: Can be integrated with Zipkin or OpenTelemetry
* Resilience: Configurable retry, circuit breaker, and rate limiter mechanisms for external calls