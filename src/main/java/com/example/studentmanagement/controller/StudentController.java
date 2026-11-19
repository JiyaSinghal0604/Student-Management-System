
package com.example.studentmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // Add student
    @PostMapping
    public ResponseEntity<String> addStudent(@RequestBody Student s) {
        service.addStudent(s);
        return ResponseEntity.ok("Student added successfully");
    }

    // Get all students
    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public Student getById(@PathVariable("id") int id) {
        return service.getById(id);
    }

    // Update scores and skills
    @PutMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @RequestBody Student s) {
        service.update(id, s);
        return "Student updated successfully";
    }

    // Delete student
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        service.delete(id);
        return "Student deleted successfully";
    }

    // Merit list
    @GetMapping("/merit-list")
    public List<Student> meritList() {
        return service.meritList();
    }

    // Search by email
    @GetMapping("/search/email")
    public List<Student> searchByEmail(
            @RequestParam("email") String email) {
        return service.searchByEmail(email);
    }

    // Search by course
    @GetMapping("/search/course")
    public List<Student> searchByCourse(
            @RequestParam("course") String course) {
        return service.searchByCourse(course);
    }

    // Programming improvement candidates
    @GetMapping("/improvement")
    public List<Student> improvementStudents() {
        return service.improvementStudents();
    }

    // Students having same technical skills
    @GetMapping("/same-skills")
    public List<Student> sameSkills() {
        return service.sameSkills();
    }

    // Handle validation and other service errors
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleError(
            org.springframework.web.server.ResponseStatusException ex) {

        Map<String, Object> response = Map.of(
            "status", ex.getStatusCode().value(),
            "message", ex.getReason() == null
                    ? "Request failed" : ex.getReason()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
}
