package backend.webappchatbot.service;

import backend.webappchatbot.model.Student;
import backend.webappchatbot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            Student existingStudent = student.get();
            if (studentDetails.getFullName() != null) {
                existingStudent.setFullName(studentDetails.getFullName());
            }
            if (studentDetails.getEmail() != null) {
                existingStudent.setEmail(studentDetails.getEmail());
            }
            if (studentDetails.getBio() != null) {
                existingStudent.setBio(studentDetails.getBio());
            }
            return studentRepository.save(existingStudent);
        }
        return null;
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

