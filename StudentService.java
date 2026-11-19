
package com.example.studentmanagement.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // Validate student details
    private void validate(Student s) {

        if (s.getStudentId() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid student ID"
            );
        }

        if (s.getName() == null || s.getName().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Name is required"
            );
        }

        if (s.getEmail() == null || !s.getEmail().contains("@")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid email"
            );
        }

        if (s.getCourse() == null || s.getCourse().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Course is required"
            );
        }

        if (s.getProgrammingScore() < 0 ||
            s.getProgrammingScore() > 100 ||
            s.getMathematicsScore() < 0 ||
            s.getMathematicsScore() > 100) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Scores must be between 0 and 100"
            );
        }
    }

    // Add student
    public int addStudent(Student s) {
        validate(s);

        if (repository.getById(s.getStudentId()) != null) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Student ID already exists"
            );
        }

        return repository.addStudent(s);
    }

    // Get all
    public List<Student> getAll() {
        return repository.getAllStudents();
    }

    // Get by ID
    public Student getById(int id) {

        Student s = repository.getById(id);

        if (s == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Student not found"
            );
        }

        return s;
    }

    // Update
    public int update(int id, Student s) {

        if (repository.getById(id) == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Student not found"
            );
        }

        if (s.getProgrammingScore() < 0 ||
            s.getProgrammingScore() > 100 ||
            s.getMathematicsScore() < 0 ||
            s.getMathematicsScore() > 100) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Scores must be between 0 and 100"
            );
        }

        return repository.updateStudent(id, s);
    }

    // Delete
    public int delete(int id) {

        if (repository.getById(id) == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Student not found"
            );
        }

        return repository.deleteStudent(id);
    }

    // Merit list
    public List<Student> meritList() {
        return repository.getMeritList();
    }

    // Search by email
    public List<Student> searchByEmail(String email) {
        return repository.getByEmail(email);
    }

    // Search by course
    public List<Student> searchByCourse(String course) {
        return repository.getByCourse(course);
    }

    // Improvement candidates
    public List<Student> improvementStudents() {
        return repository.getImprovementStudents();
    }

    // Same skills
    public List<Student> sameSkills() {
        return repository.getSameSkills();
    }
}
