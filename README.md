# Student Quiz Management System

A Java console application using JDBC and MySQL to record quiz attempts and report scores.

## Features
- Insert a quiz attempt for a student.
- Display all quiz attempts and percentages.
- Display the highest-scoring attempt by percentage.
- List attempts scoring strictly above 80%.

## Requirements
- JDK 17 or later
- MySQL Server
- IntelliJ IDEA
- Maven (IntelliJ can import the included `pom.xml`)

## Database setup
1. Open MySQL Workbench or the MySQL command line.
2. Run the SQL statements in `database/schema.sql`.

## Configure database credentials
Set these environment variables before running the application:
- `DB_PASSWORD` (required): your MySQL password
- `DB_USER` (optional; defaults to `root`)
- `DB_URL` (optional; defaults to `jdbc:mysql://localhost:3306/quiz_management`)

Do not put your database password in source code or commit it to GitHub.

### IntelliJ on Windows
Open **Run → Edit Configurations...**, select the `QuizApp` run configuration, and add environment variables under **Environment variables**, for example:

`DB_PASSWORD=your_actual_password;DB_USER=root`

Use your own password locally. Do not share screenshots or commit files containing it.

## Run in IntelliJ
1. Open this folder in IntelliJ IDEA.
2. Trust/open the project and allow Maven to load dependencies.
3. Make sure MySQL Server is running.
4. Set `DB_PASSWORD` in the run configuration.
5. Run `QuizApp.main()`.

## Scoring
`score` is the number of correct answers. The percentage is calculated as `score / total_questions * 100`. The above-80% query excludes scores of exactly 80%.
