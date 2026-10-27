import java.sql.*;
import java.util.Scanner;

public class QuizApp {
    private static final String URL =
            System.getenv().getOrDefault(
                    "DB_URL",
                    "jdbc:mysql://localhost:3306/quiz_management"
            );
    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");
    // Set DB_PASSWORD in your environment; do not commit database passwords.
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static final Scanner sc = new Scanner(System.in);

    public static Connection connectDB() throws SQLException {
        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "DB_PASSWORD environment variable is not set."
            );
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertResult(Connection con) throws SQLException {
        System.out.print("Enter Student ID: ");
        String studentId = sc.nextLine().trim();

        System.out.print("Enter Quiz Name: ");
        String quizName = sc.nextLine().trim();

        if (studentId.isEmpty() || quizName.isEmpty()) {
            System.out.println("Student ID and quiz name cannot be empty.");
            return;
        }

        int totalQuestions;
        int score;
        try {
            System.out.print("Enter Total Questions: ");
            totalQuestions = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Correct Answers: ");
            score = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Enter valid integer values.");
            return;
        }

        if (totalQuestions <= 0 || score < 0 || score > totalQuestions) {
            System.out.println("Invalid total questions or score.");
            return;
        }

        String sql = """
                INSERT INTO quiz_results
                (student_id, quiz_name, total_questions, score)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, quizName);
            ps.setInt(3, totalQuestions);
            ps.setInt(4, score);
            ps.executeUpdate();
            System.out.println("Quiz result inserted successfully!");
        }
    }

    public static void displayResults(Connection con) throws SQLException {
        String sql = """
                SELECT student_id, quiz_name, total_questions, score,
                       ROUND(score * 100.0 / total_questions, 2) AS percentage
                FROM quiz_results
                ORDER BY attempt_id
                """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\\n--- All Quiz Results ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf(
                        "Student ID: %s | Quiz: %s | Total Questions: %d "
                                + "| Score: %d | Percentage: %.2f%%%n",
                        rs.getString("student_id"),
                        rs.getString("quiz_name"),
                        rs.getInt("total_questions"),
                        rs.getInt("score"),
                        rs.getDouble("percentage")
                );
            }

            if (!found) {
                System.out.println("No quiz results found.");
            }
        }
    }

    public static void displayHighestScore(Connection con) throws SQLException {
        String sql = """
                SELECT student_id, quiz_name, total_questions, score,
                       ROUND(score * 100.0 / total_questions, 2) AS percentage
                FROM quiz_results
                ORDER BY score * 1.0 / total_questions DESC, score DESC
                LIMIT 1
                """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("\\n--- Highest Scoring Attempt ---");
                System.out.println("Student ID: " + rs.getString("student_id"));
                System.out.println("Quiz Name: " + rs.getString("quiz_name"));
                System.out.println("Score: " + rs.getInt("score")
                        + "/" + rs.getInt("total_questions"));
                System.out.println("Percentage: "
                        + rs.getDouble("percentage") + "%");
            } else {
                System.out.println("No quiz results available.");
            }
        }
    }

    public static void displayAbove80(Connection con) throws SQLException {
        String sql = """
                SELECT student_id, quiz_name, total_questions, score,
                       ROUND(score * 100.0 / total_questions, 2) AS percentage
                FROM quiz_results
                WHERE score * 100.0 / total_questions > 80
                ORDER BY score * 1.0 / total_questions DESC
                """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\\n--- Students Scoring Above 80% ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf(
                        "Student ID: %s | Quiz: %s | Score: %d/%d "
                                + "| Percentage: %.2f%%%n",
                        rs.getString("student_id"),
                        rs.getString("quiz_name"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getDouble("percentage")
                );
            }

            if (!found) {
                System.out.println("No students scored above 80%.");
            }
        }
    }

    public static void main(String[] args) {
        try (Connection con = connectDB()) {
            System.out.println("Connected to MySQL successfully!");

            while (true) {
                System.out.println("\\n===== Student Quiz Management System =====");
                System.out.println("1. Insert Quiz Result");
                System.out.println("2. Display All Results");
                System.out.println("3. Display Highest Score");
                System.out.println("4. List Students Above 80%");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                String choice = sc.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> insertResult(con);
                        case "2" -> displayResults(con);
                        case "3" -> displayHighestScore(con);
                        case "4" -> displayAbove80(con);
                        case "5" -> {
                            System.out.println("Exiting application.");
                            return;
                        }
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database operation failed: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            System.out.println("Check MySQL service, database, and DB_* environment variables.");
        }
    }
}
