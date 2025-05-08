# Email Signup Service

A secure, production-ready email signup service built with Spring Boot 3, PostgreSQL, Docker, and AWS SES.

## Features
- Subscriber signup and email confirmation flow
- Secure storage of secrets using environment variables and .env files
- Database migrations with Flyway
- Dockerized for easy local development
- AWS SES integration for sending emails
- Rate limiting and input validation

## Prerequisites
- Java 21
- Docker & Docker Compose
- AWS account with SES enabled and a verified sender email

## Setup

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd email-signup-springboot
```

### 2. Configure AWS Credentials
Create a `.env` file in the project root:
```env
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_SES_FROM_EMAIL=your-verified-sender@domain.com
```
- Never commit `.env` to version control (it's gitignored by default).

### 3. Configure Application Properties
Edit `src/main/resources/application.yml` as needed:
- Set your AWS region and sender email under `aws:`
- Database and other settings are preconfigured for local Docker Compose

### 4. Start the Application (Recommended: Docker Compose)
```bash
docker compose up --build
```
- The app will be available at [http://localhost:8080](http://localhost:8080)
- PostgreSQL will be available at `localhost:5432`

### 5. Database Migrations
- Flyway will automatically run migration scripts from `src/main/resources/db/migration/`

## API Usage

### Signup Endpoint
```bash
curl -X POST http://localhost:8080/api/signup \
-H "Content-Type: application/json" \
-d '{
    "email": "your-verified-email@example.com",
    "name": "Test User"
}'
```
- You will receive a confirmation email (sender and recipient must be verified in SES sandbox mode).

### Confirmation Endpoint
- Click the link in your email, or:
```bash
curl "http://localhost:8080/api/confirm?token=YOUR_TOKEN_HERE"
```

## Troubleshooting
- **Database connection errors:** Ensure Docker Compose is running and the app uses `db` as the database host in Docker.
- **SES MessageRejectedException:** Make sure both sender and recipient emails are verified in AWS SES (sandbox mode).
- **Confirmation link 404:** The confirmation link must use `/api/confirm?token=...` (not `/confirm`).
- **Rate limiting:** The API is limited to 5 requests per minute per IP.

## Security Best Practices
- Secrets are stored in `.env` and injected as environment variables
- `.env` and other sensitive files are gitignored
- Never hardcode secrets in code or config files

## Project Structure
```
email-signup-springboot/
├── .env                # AWS secrets (not committed)
├── .gitignore          # Security best practices
├── build.gradle        # Gradle build file
├── docker-compose.yml  # Docker Compose setup
├── Dockerfile          # Docker build for app
├── src/
│   └── main/
│       ├── java/com/email/signup/...
│       └── resources/
│           ├── application.yml
│           └── db/migration/
└── ...
```

## Next Steps
- Implement REST endpoints for signup and confirmation
- Add integration tests
- Move SES out of sandbox mode for production

## License
MIT (or your preferred license)
