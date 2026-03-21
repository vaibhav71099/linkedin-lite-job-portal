# Deployment Guide

This project can be deployed in three common ways:

1. Local or VPS deployment with Docker Compose
2. Production-style deployment with Nginx reverse proxy
3. Split deployment with backend and frontend hosted separately

## 1. Environment Variables

Backend variables:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/jobportal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
DB_USERNAME=root
DB_PASSWORD=change-me
JWT_SECRET=replace-with-a-long-random-base64-secret
JWT_EXPIRATION=86400000
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SERVER_PORT=8081
```

If you run the backend on your host machine against the MySQL container from `docker compose`, use host port `3307` instead of `3306`:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/jobportal?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
```

Frontend variable:

```env
VITE_API_BASE_URL=https://your-backend-domain.com
```

Examples are available in:

- [.env.example](/Users/vaibhavsuryawanshi/Downloads/jobportal/.env.example)
- [frontend/.env.example](/Users/vaibhavsuryawanshi/Downloads/jobportal/frontend/.env.example)
- [.env.prod.example](/Users/vaibhavsuryawanshi/Downloads/jobportal/.env.prod.example)

## 2. Docker Compose Deployment

From the project root:

```bash
docker compose up --build -d
```

Services:

- MySQL: `localhost:3306`
- Spring Boot backend: `http://localhost:8081`
- React frontend: `http://localhost:5173`

Stop services:

```bash
docker compose down
```

Stop and remove DB volume:

```bash
docker compose down -v
```

## 3. Production Deployment With Reverse Proxy

Create a production env file from [.env.prod.example](/Users/vaibhavsuryawanshi/Downloads/jobportal/.env.prod.example) and set a real domain and JWT secret.

Start the production stack:

```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
```

Public traffic flow:

- `proxy` listens on port `80`
- frontend is served through Nginx
- backend APIs are proxied through the same domain
- MySQL is internal only and not exposed publicly

Key production files:

- [docker-compose.prod.yml](/Users/vaibhavsuryawanshi/Downloads/jobportal/docker-compose.prod.yml)
- [infra/nginx/default.conf](/Users/vaibhavsuryawanshi/Downloads/jobportal/infra/nginx/default.conf)

Recommended next production step:

- place this stack behind HTTPS using Cloudflare, Nginx SSL, or a load balancer

## 4. Backend-Only Deployment

Build the jar:

```bash
./mvnw clean package
```

Run with environment variables:

```bash
export DB_USERNAME=root
export DB_PASSWORD=change-me
export JWT_SECRET=replace-with-a-long-random-base64-secret
export APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
java -jar target/jobportal-0.0.1-SNAPSHOT.jar
```

## 5. Frontend-Only Deployment

Build the frontend with the backend URL:

```bash
cd frontend
export VITE_API_BASE_URL=https://your-backend-domain.com
npm install
npm run build
```

The production files will be created in `frontend/dist`.

## 6. Files Added For Deployment

- [Dockerfile](/Users/vaibhavsuryawanshi/Downloads/jobportal/Dockerfile)
- [frontend/Dockerfile](/Users/vaibhavsuryawanshi/Downloads/jobportal/frontend/Dockerfile)
- [frontend/nginx.conf](/Users/vaibhavsuryawanshi/Downloads/jobportal/frontend/nginx.conf)
- [docker-compose.yml](/Users/vaibhavsuryawanshi/Downloads/jobportal/docker-compose.yml)
- [docker-compose.prod.yml](/Users/vaibhavsuryawanshi/Downloads/jobportal/docker-compose.prod.yml)
- [docker-compose.prod.ssl.yml](/Users/vaibhavsuryawanshi/Downloads/jobportal/docker-compose.prod.ssl.yml)
- [infra/nginx/default.conf](/Users/vaibhavsuryawanshi/Downloads/jobportal/infra/nginx/default.conf)
- [infra/nginx/default-ssl.conf](/Users/vaibhavsuryawanshi/Downloads/jobportal/infra/nginx/default-ssl.conf)

## 7. Recommended Production Improvements

- move away from `spring.jpa.hibernate.ddl-auto=update` to Flyway or Liquibase
- remove default secret fallbacks from `application.properties`
- use HTTPS in front of frontend and backend
- store JWT secret in a real secret manager or deployment environment

## HTTPS Setup

If you already have SSL certificate files:

- `infra/certs/fullchain.pem`
- `infra/certs/privkey.pem`

then run:

```bash
docker compose -f docker-compose.prod.yml -f docker-compose.prod.ssl.yml --env-file .env.prod up --build -d
```

Expected `.env.prod` values:

```env
DOMAIN=your-domain.com
APP_CORS_ALLOWED_ORIGINS=https://your-domain.com
```

The HTTPS files added for this are:

- [docker-compose.prod.ssl.yml](/Users/vaibhavsuryawanshi/Downloads/jobportal/docker-compose.prod.ssl.yml)
- [infra/nginx/default-ssl.conf](/Users/vaibhavsuryawanshi/Downloads/jobportal/infra/nginx/default-ssl.conf)
- [infra/certs/.gitkeep](/Users/vaibhavsuryawanshi/Downloads/jobportal/infra/certs/.gitkeep)
