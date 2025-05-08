# Email Signup Service

A production-ready email signup service built with Spring Boot 3, PostgreSQL, Docker, and AWS SES.

## Features
- Email signup and confirmation flow
- Secure secrets via environment variables
- Database migrations with Flyway
- Dockerized for local development
- AWS SES integration
- Rate limiting and input validation

## Prerequisites
- Java 21
- Docker & Docker Compose
- AWS SES account with a verified sender email

## Getting Started

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

### 3. Application Configuration
- Edit `src/main/resources/application.yml` if needed (AWS region, sender email, etc.)
- Database and other settings are preconfigured for Docker Compose

### 4. Start the Application
```bash
docker compose up --build
```
- App: [http://localhost:8080](http://localhost:8080)
- PostgreSQL: `localhost:5432`

### 5. Database Migrations
- Flyway runs migrations automatically from `src/main/resources/db/migration/`

## API Usage

**Signup:**
```bash
curl -X POST http://localhost:8080/api/signup \
  -H "Content-Type: application/json" \
  -d '{"email": "your-verified-email@example.com", "name": "Test User"}'
```
- You will receive a confirmation email (sender and recipient must be verified in SES sandbox mode).

**Confirm:**
Click the link in your email, or:
```bash
curl "http://localhost:8080/api/confirm?token=YOUR_TOKEN_HERE"
```

## Troubleshooting
- **Database errors:** Ensure Docker Compose is running and the app uses `db` as the database host.
- **SES errors:** Both sender and recipient emails must be verified in AWS SES (sandbox mode).
- **Confirmation link 404:** The link must use `/api/confirm?token=...`.
- **Rate limiting:** 5 requests per minute per IP.

## Security
- Secrets are stored in `.env` (gitignored)
- Never hardcode secrets in code or config files

## Project Structure
```
email-signup-springboot/
├── .env
├── .gitignore
├── build.gradle
├── docker-compose.yml
├── Dockerfile
├── src/
│   └── main/
│       ├── java/com/email/signup/...
│       └── resources/
│           ├── application.yml
│           └── db/migration/
└── ...
```

## License
MIT (or your preferred license)
