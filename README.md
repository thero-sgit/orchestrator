# Resonate Orchestrator

A Spring Boot-based orchestrator service for the Resonate audio fingerprinting system. This service coordinates audio file processing between storage, messaging, and fingerprinting components.

## Overview

The Resonate Orchestrator is the central coordination service in the Resonate ecosystem. It handles audio file uploads, manages storage and metadata persistence, and orchestrates the fingerprinting process through event-driven messaging.

## Architecture

The orchestrator integrates with several components:

- **PostgreSQL**: Metadata storage for audio files
- **Amazon S3**: Audio file storage
- **Apache Kafka**: Event messaging for processing coordination
- **Rust Fingerprinting Service ([Resonate][1])**: External service that performs audio fingerprinting

## Features

- Audio file upload and validation
- Secure storage in Amazon S3
- Metadata persistence in PostgreSQL
- Event-driven processing via Kafka
- Health monitoring endpoints
- Comprehensive integration testing with Testcontainers

## Prerequisites

- Java 21
- Maven 3.6+
- Docker (for testing with Testcontainers)
- PostgreSQL (production)
- Kafka (production)
- AWS S3 bucket (production)

## Configuration

The application uses environment variables for configuration:

### Server
- `port`: Server port (default: 8080)

### Database
- `DB_HOST`: PostgreSQL host
- `DB_PORT`: PostgreSQL port (default: 5432)
- `DB_NAME`: Database name
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

### Kafka
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
- `KAFKA_CONSUMER_GROUP_ID`: Kafka consumer group ID

### AWS S3
- `AWS_REGION`: AWS region
- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `S3_BUCKETS`: S3 bucket name

### Services
- `RUST_SERVICE_URL`: Base URL for the Rust fingerprinting service

## Building and Running

### Local Development

1. Clone the repository
2. Configure environment variables or create `application-local.yml`
3. Run with Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

### Production

Build the application:
```bash
./mvnw clean package
```

Run the JAR:
```bash
java -jar target/ochestrator-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Upload Audio File
- **POST** `/upload`
- **Content-Type**: `multipart/form-data`
- **Parameter**: `file` (audio file)
- **Supported formats**: MP3, WAV, FLAC, AAC

Example:
```bash
curl -X POST -F "file=@audio.mp3" http://localhost:8080/upload
```

### Health Check
- **GET** `/actuator/health`
- Returns health status of the service and its dependencies

## Testing

The project includes comprehensive integration tests using Testcontainers:

```bash
./mvnw test
```

Tests cover:
- Kafka integration
- PostgreSQL database operations
- S3 storage operations
- End-to-end upload processing
- Rust service communication

## Project Structure

```
src/
├── main/
│   ├── java/io/github/therosgit/resonate/ochestrator/
│   │   ├── ResonateOchestratorApplication.java
│   │   ├── components/          # Core business logic
│   │   ├── controller/          # REST API controllers
│   │   ├── domain/             # JPA entities
│   │   ├── repository/         # Data access layer
│   │   └── services/           # External service integrations
│   └── resources/
│       └── application.yml     # Configuration
└── test/
    ├── java/io/github/therosgit/resonate/ochestrator/
    │   ├── integration/        # Integration tests
    │   ├── kafka/             # Kafka-specific tests
    │   ├── postgres/          # Database tests
    │   ├── resonate/          # Rust service tests
    │   └── storage/           # S3 storage tests
    └── resources/
        └── application.yml     # Test configuration
```

## Dependencies

- Spring Boot 4.0.2
- Spring Data JPA
- Spring Kafka
- AWS SDK for S3
- Apache Tika (file type detection)
- Testcontainers (integration testing)
- PostgreSQL driver

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the terms specified in the pom.xml file.</content>
<parameter name="filePath">/home/wethinkcode_/Personal/orchestrator/README.md

[1]: https://github.com/thero-sgit/resonate