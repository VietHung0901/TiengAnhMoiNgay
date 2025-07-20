# TiengAnhMoiNgay (Daily English Learning)

A comprehensive web application for learning English through reading, listening, and writing exercises. Built with Spring Boot and designed to help users improve their English language skills through daily practice.

## Features

- **User Authentication**: Secure login/registration system with email verification
- **Multiple Learning Modes**:
  - **Reading Lessons**: Text-based lessons with comprehension questions
  - **Listening Lessons**: Audio-based lessons with subtitles support
  - **Writing Lessons**: Writing exercises with feedback
- **Dictionary Integration**: Look up words and phrases
- **Progress Tracking**: Monitor learning progress over time
- **Multi-level Support**: Content for different proficiency levels
- **Translation Services**: Powered by AWS Translate
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

- **Backend**: Java 22, Spring Boot 3.4.1
- **Security**: Spring Security, JWT Authentication
- **Database**: MySQL
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **External Services**: AWS Translate
- **Build Tool**: Maven

## Project Structure

```
TiengAnhMoiNgay/
├── src/main/
│   ├── java/Project/TiengAnhMoiNgay/
│   │   ├── AWS/            - AWS service integrations
│   │   ├── config/         - Application configurations
│   │   ├── constant/       - Constants and enums
│   │   ├── controllers/    - API and view controllers
│   │   ├── entities/       - Database entities
│   │   ├── exception/      - Custom exceptions
│   │   ├── model/          - Data models
│   │   ├── repositories/   - Data access layer
│   │   ├── request/        - Request DTOs
│   │   ├── response/       - Response DTOs
│   │   └── services/       - Business logic
│   └── resources/
│       ├── static/         - CSS, JS, images
│       └── templates/      - Thymeleaf templates
├── readings/               - Reading lesson content
├── subtitles/              - Subtitle files for listening lessons
└── writings/               - Writing lesson content
```

## Getting Started

### Prerequisites

- Java 22 or higher
- MySQL 8.0 or higher
- Maven 3.8 or higher

### Installation

1. Clone the repository:
   ```
   https://github.com/VietHung0901/TiengAnhMoiNgay/tree/DEV
   ```

2. Configure the database in `application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/TiengAnhMoiNgay
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Configure AWS credentials (for translation features):
   ```
   aws.iam.access_key=your_access_key
   aws.iam.secret_key=your_secret_key
   ```

4. Build the project:
   ```
   mvn clean install
   ```

5. Run the application:
   ```
   mvn spring-boot:run
   ```

6. Access the application at `http://localhost:8080`

## User Roles

- **User**: Regular learners who can access lessons and track their progress
- **Employee**: Content creators and administrators who can manage lessons and user data

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- Spring Boot team for the excellent framework
- AWS for translation services
- All contributors to this project
