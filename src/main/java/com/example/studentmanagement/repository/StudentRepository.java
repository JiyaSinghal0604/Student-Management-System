
package com.example.studentmanagement.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.studentmanagement.model.Student;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbc;

    public StudentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Convert database row into Student object
    private RowMapper<Student> rowMapper = (rs, rowNum) -> {
        Student s = new Student();

        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setCourse(rs.getString("course"));
        s.setProgrammingScore(rs.getDouble("programming_score"));
        s.setMathematicsScore(rs.getDouble("mathematics_score"));

        s.setTechnicalSkills(getSkills(s.getStudentId()));

        return s;
    };

    // Get skills of a student
    private List<String> getSkills(int studentId) {

        return jdbc.query(
            "SELECT skill FROM student_skills WHERE student_id=?",
            (rs, rowNum) -> rs.getString("skill"),
            studentId
        );
    }

    // Add student
    public int addStudent(Student s) {

        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)";

        int result = jdbc.update(sql,
            s.getStudentId(),
            s.getName(),
            s.getEmail(),
            s.getCourse(),
            s.getProgrammingScore(),
            s.getMathematicsScore()
        );

        if (s.getTechnicalSkills() != null) {
            for (String skill : s.getTechnicalSkills()) {
                jdbc.update(
                    "INSERT INTO student_skills(student_id, skill) VALUES (?, ?)",
                    s.getStudentId(), skill
                );
            }
        }

        return result;
    }

    // Retrieve all students
    public List<Student> getAllStudents() {

        return jdbc.query(
            "SELECT * FROM students",
            rowMapper
        );
    }

    // Search by ID
    public Student getById(int id) {

        List<Student> list = jdbc.query(
            "SELECT * FROM students WHERE student_id=?",
            rowMapper,
            id
        );

        return list.isEmpty() ? null : list.get(0);
    }

    // Search by email
    public List<Student> getByEmail(String email) {

        return jdbc.query(
            "SELECT * FROM students WHERE email=?",
            rowMapper,
            email
        );
    }

    // Search by course
    public List<Student> getByCourse(String course) {

        return jdbc.query(
            "SELECT * FROM students WHERE course=?",
            rowMapper,
            course
        );
    }

    // Update scores and skills
    public int updateStudent(int id, Student s) {

        int result = jdbc.update(
            "UPDATE students SET programming_score=?, mathematics_score=? WHERE student_id=?",
            s.getProgrammingScore(),
            s.getMathematicsScore(),
            id
        );

        if (result > 0) {

            jdbc.update(
                "DELETE FROM student_skills WHERE student_id=?",
                id
            );

            if (s.getTechnicalSkills() != null) {
                for (String skill : s.getTechnicalSkills()) {
                    jdbc.update(
                        "INSERT INTO student_skills(student_id, skill) VALUES (?, ?)",
                        id, skill
                    );
                }
            }
        }

        return result;
    }

    // Delete student
    public int deleteStudent(int id) {

        return jdbc.update(
            "DELETE FROM students WHERE student_id=?",
            id
        );
    }

    // Merit list
    public List<Student> getMeritList() {

        return jdbc.query(
            "SELECT * FROM students ORDER BY (programming_score + mathematics_score)/2 DESC",
            rowMapper
        );
    }

    // Students needing programming improvement but mathematics >= 75
    public List<Student> getImprovementStudents() {

        return jdbc.query(
            "SELECT * FROM students WHERE programming_score < 50 AND mathematics_score >= 75",
            rowMapper
        );
    }

    // Students with identical technical skills
    public List<Student> getSameSkills() {

        return jdbc.query(
            "SELECT s.* FROM students s JOIN (" +
            "SELECT GROUP_CONCAT(skill ORDER BY skill SEPARATOR ',') AS skillset " +
            "FROM student_skills GROUP BY student_id" +
            ") x ON x.skillset = (" +
            "SELECT GROUP_CONCAT(ss.skill ORDER BY ss.skill SEPARATOR ',') " +
            "FROM student_skills ss WHERE ss.student_id=s.student_id" +
            ")",
            rowMapper
        );
    }
}
