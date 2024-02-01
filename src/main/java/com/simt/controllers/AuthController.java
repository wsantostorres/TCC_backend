package com.simt.controllers;

import com.simt.dtos.UserGetDataDto;
import com.simt.dtos.UserRegisterDto;
import com.simt.models.CourseModel;
import com.simt.models.EmployeeModel;
import com.simt.models.StudentModel;
import com.simt.models.VacancyModel;
import com.simt.repositories.CourseRepository;
import com.simt.repositories.EmployeeRepository;
import com.simt.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CourseRepository courseRepository;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRegisterDto userRegisterDto){
        try{
            if(userRegisterDto.bondType().equals("Servidor")){
                EmployeeModel newUser = new EmployeeModel();
                newUser.setId(userRegisterDto.id());
                newUser.setRegistration(userRegisterDto.registration());
                newUser.setFullName(userRegisterDto.fullName());
                newUser.setBondType(userRegisterDto.bondType());

                EmployeeModel employeeCreated = employeeRepository.save(newUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(employeeCreated);
            }else if(userRegisterDto.bondType().equals("Aluno")){
                StudentModel newUser = new StudentModel();
                newUser.setId(userRegisterDto.id());
                newUser.setRegistration(userRegisterDto.registration());
                newUser.setFullName(userRegisterDto.fullName());
                newUser.setBondType(userRegisterDto.bondType());

                CourseModel course = courseRepository.findByName(userRegisterDto.course());
                newUser.setCourse(course);

                StudentModel studentCreated = studentRepository.save(newUser);

                Map<String, Object> dataStudent = new HashMap<>();
                dataStudent.put("id", studentCreated.getId());
                dataStudent.put("fullName", studentCreated.getFullName());
                dataStudent.put("bondType", studentCreated.getBondType());
                dataStudent.put("courseId", studentCreated.getCourse().getId());
                dataStudent.put("courseName", studentCreated.getCourse().getName());

                return ResponseEntity.status(HttpStatus.CREATED).body(dataStudent);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vínculo não permitido.");
            }
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocorreu um erro ao registrar usuário.");
        }
    }

    @PostMapping("/get-data")
    public ResponseEntity<Object> getDataByToken(@RequestBody UserGetDataDto userGetDataDto) {
        try{
            Long id = extractIdFromToken(userGetDataDto.token());

            Map<String, Object> UserId = new HashMap<>();
            UserId.put("id", id);
            UserId.put("created", false);

            if(userGetDataDto.bondType().equals("Aluno")){

                Optional<StudentModel> studentOptional = studentRepository.findById(id);

                if (studentOptional.isPresent()) {

                    StudentModel student = studentOptional.get();

                    Map<String, Object> dataStudent = new HashMap<>();
                    dataStudent.put("id", student.getId());
                    dataStudent.put("fullName", student.getFullName());
                    dataStudent.put("bondType", student.getBondType());
                    dataStudent.put("courseId", student.getCourse().getId());
                    dataStudent.put("courseName", student.getCourse().getName());
                    dataStudent.put("created", true);

                    List<Long> vacanciesIds = student.getVacancies()
                            .stream()
                            .map(VacancyModel::getId)
                            .toList();
                    dataStudent.put("vacanciesIds", vacanciesIds);

                    if (student.getResume() != null) {
                        long resumeId = student.getResume().getId();
                        dataStudent.put("resumeId", resumeId);
                    } else {
                        dataStudent.put("resumeId", null);
                    }

                    return ResponseEntity.status(HttpStatus.OK).body(dataStudent);
                }

                return ResponseEntity.status(HttpStatus.OK).body(UserId);

            }else if(userGetDataDto.bondType().equals("Servidor")){

                Optional<EmployeeModel> employeeOptional = employeeRepository.findById(id);

                if (employeeOptional.isPresent()){
                    EmployeeModel employee = employeeOptional.get();

                    Map<String, Object> dataEmployee = new HashMap<>();
                    dataEmployee.put("id", employee.getId());
                    dataEmployee.put("fullName", employee.getFullName());
                    dataEmployee.put("bondType", employee.getBondType());
                    dataEmployee.put("created", true);

                    return ResponseEntity.status(HttpStatus.OK).body(dataEmployee);
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserId);

            }else{
                return ResponseEntity.status(HttpStatus.OK).body(UserId);
            }
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
        }

    }

    private Long extractIdFromToken(String token) {
        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String tokenPayload = new String(decoder.decode(chunks[1]));

        Matcher matcher = Pattern.compile("\"user_id\":\\s*(\\d+)").matcher(tokenPayload);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Token inválido");
        }
    }
}
