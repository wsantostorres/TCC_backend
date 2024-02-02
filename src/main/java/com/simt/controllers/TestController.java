package com.simt.controllers;

import com.simt.repositories.CourseRepository;
import com.simt.repositories.ResumeRepository;
import com.simt.repositories.StudentRepository;
import com.simt.repositories.VacancyRepository;
import com.simt.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    VacancyRepository vacancyRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ResumeRepository resumeRepository;


    // Endpoint apenas para uso dos testes
    @GetMapping("/get-vacancies-test")
    public ResponseEntity<Object> allVacancies(){
        return ResponseEntity.status(HttpStatus.OK).body(vacancyRepository.findAll());
    }

    @PostMapping("/delete-vacancies")
    public ResponseEntity<Object> deleteAllVacancies(){
        try{

            List<VacancyModel> listVacancies = vacancyRepository.findAll();
            List<CourseModel> listCourses = courseRepository.findAll();

            // Desvinculando cursos e vagas
            for(VacancyModel vacancy : listVacancies){
                for(CourseModel course : listCourses){
                    if(vacancy.getCourses().contains(course)){
                        vacancy.getCourses().remove(course);
                        course.getVacancies().remove(vacancy);
                    }
                }
            }

            // Desvinculando alunos e vagas
            List<StudentModel> relatedStudents = null;

            for(VacancyModel vacancy : listVacancies){
                relatedStudents = vacancy.getStudents();
                for (StudentModel relatedStudent : relatedStudents){
                    relatedStudent.getVacancies().remove(vacancy);
                }
            }

            vacancyRepository.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).body("Todas as vagas excluidas!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    @PostMapping("/delete-resume/{studentId}")
    public ResponseEntity<Object> deleteStudentResume(@PathVariable Long studentId){
        try{

            Optional<StudentModel> studentOptional = studentRepository.findById(studentId);
            ResumeModel resume = null;

            if(studentOptional.isPresent()){
                StudentModel student = studentOptional.get();
                resume = student.getResume();

                // Desvincular Usuario e Curriculo
                student.setResume(null);
                resume.setStudent(null);

                // Exclui o curriculo
                resumeRepository.deleteById(resume.getId());

            }

            return ResponseEntity.status(HttpStatus.OK).body("Curriculo Excluído!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    // teste de envio de currículo
    @PostMapping("/vacancy/create")
    public ResponseEntity<Object> createVacancy(){
        try{
            LocalDate parsedClosingDate = LocalDate.parse("2023-12-21", DateTimeFormatter.ISO_DATE);
            LocalDateTime combinedDateTime = LocalDateTime.of(parsedClosingDate, LocalTime.of(23, 59));

            VacancyModel vacancy = new VacancyModel();
            vacancy.setTitle("Teste Enviar Currículo");
            vacancy.setDescription("Teste Enviar Currículo");
            vacancy.setType(1);
            vacancy.setMorning(1);
            vacancy.setAfternoon(0);
            vacancy.setNight(0);
            vacancy.setClosingDate(combinedDateTime);
            vacancy.setModifiedIn(LocalDateTime.now());

            CourseModel course = courseRepository.findByName("Tecnologia em Análise e Desenvolvimento de Sistemas");
            course.getVacancies().add(vacancy);
            vacancy.getCourses().add(course);

            vacancyRepository.save(vacancy);

            return ResponseEntity.status(HttpStatus.OK).body("vaga cadastrada!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    // teste de baixar currículo
    @PostMapping("/attach/{studentId}")
    public ResponseEntity<Object> attachVacancyStudentResume(@PathVariable Long studentId){
        try{
            Optional<StudentModel> studentOptional = studentRepository.findById(studentId);

            if(studentOptional.isPresent()){
                StudentModel student = studentOptional.get();

                // Criando Vaga
                LocalDate parsedClosingDate = LocalDate.parse("2023-12-19", DateTimeFormatter.ISO_DATE);
                LocalDateTime combinedDateTime = LocalDateTime.of(parsedClosingDate, LocalTime.of(23, 59));

                VacancyModel vacancy = new VacancyModel();
                vacancy.setTitle("Teste Baixar Currículo");
                vacancy.setDescription("Teste Baixar Currículo");
                vacancy.setType(1);
                vacancy.setMorning(1);
                vacancy.setAfternoon(0);
                vacancy.setNight(0);
                vacancy.setClosingDate(combinedDateTime);
                vacancy.setModifiedIn(LocalDateTime.now());

                CourseModel course = courseRepository.findByName("Tecnologia em Análise e Desenvolvimento de Sistemas");
                course.getVacancies().add(vacancy);
                vacancy.getCourses().add(course);

                // Criando Currículo
                ResumeModel resume = new ResumeModel();
                resume.setObjectiveDescription("Descrição teste");

                AddressModel address = new AddressModel();
                address.setCity("Nova Cruz");
                address.setStreet("Rua 1");
                address.setNumber(124);
                address.setResume(resume);

                ContactModel contact = new ContactModel();
                contact.setPhone("(84) 99999-9999");
                contact.setEmail("teste@gmail.com");
                contact.setLinkedin("Linkedin Teste");
                contact.setResume(resume);

                student.setResume(resume);

                List<ProjectModel> projects = new ArrayList<>();
                List<ExperienceModel> experiences = new ArrayList<>();
                List<AcademicFormationModel> academics = new ArrayList<>();
                List<SkillModel> skills = new ArrayList<>();
                resume.setProjects(projects);
                resume.setExperiences(experiences);
                resume.setAcademics(academics);
                resume.setSkills(skills);
                resume.setAddress(address);
                resume.setContact(contact);
                resumeRepository.save(resume);

                // Vinculando Aluno a Vaga
                student.getVacancies().add(vacancy);
                vacancy.getStudents().add(student);

                // salvando vaga e curriculo
                vacancyRepository.save(vacancy);
            }

            return ResponseEntity.status(HttpStatus.OK).body("vinculação concluida!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }
}
