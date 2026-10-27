CREATE DATABASE IF NOT EXISTS quiz_management;
USE quiz_management;

CREATE TABLE IF NOT EXISTS quiz_results (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    quiz_name VARCHAR(100) NOT NULL,
    total_questions INT NOT NULL,
    score INT NOT NULL,
    attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (total_questions > 0),
    CHECK (score >= 0 AND score <= total_questions)
);
