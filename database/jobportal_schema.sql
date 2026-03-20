CREATE DATABASE IF NOT EXISTS jobportal;
USE jobportal;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    bio VARCHAR(1000),
    skills VARCHAR(1000),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    company VARCHAR(150) NOT NULL,
    location VARCHAR(150) NOT NULL,
    recruiter_id BIGINT NOT NULL,
    CONSTRAINT fk_jobs_recruiter
        FOREIGN KEY (recruiter_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    applied_date DATE NOT NULL,
    CONSTRAINT fk_applications_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_applications_job
        FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT uk_user_job UNIQUE (user_id, job_id)
);
