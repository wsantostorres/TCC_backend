package com.simt.controllers;

import com.simt.dtos.ResumeDto;
import com.simt.models.StudentModel;
import com.simt.repositories.StudentRepository;
import com.simt.services.GenerateResumeService;
import com.simt.services.ResumeService;
import com.simt.models.ResumeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/resumes")
public class ResumeController {
    @Autowired
    ResumeService resumeService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    GenerateResumeService generateResumeService;

    @GetMapping("/{studentId}/{resumeId}")
    public ResponseEntity<ResumeModel> getResume(@PathVariable long studentId, @PathVariable long resumeId){
        try{
            ResumeModel resumeFound = resumeService.getResume(resumeId);

            if(resumeFound == null){
                throw new Exception("Curriculo não encontrado");
            }

            return ResponseEntity.status(HttpStatus.OK).body(resumeFound);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/download/{studentId}/{resumeId}")
    public ResponseEntity<Object> downloadResumePDF(@PathVariable Long studentId, @PathVariable Long resumeId) {
        try {
            Optional<StudentModel> studentOptional = studentRepository.findById(studentId);

            if (studentOptional.isPresent()) {

                StudentModel student = studentOptional.get();
                if (student.getResume() == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currículo não encontrado");
                }

                byte[] resumePDF = generateResumeService.generateResumePDF(student);

                if (resumePDF != null) {
                    String fileName = student.getFullName();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType("application/pdf"));
                    headers.setContentDispositionFormData("attachment", fileName + ".pdf");
                    headers.setContentLength(resumePDF.length);

                    return new ResponseEntity<>(resumePDF, headers, HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar o arquivo PDF.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<ResumeModel> createResume(@RequestBody ResumeDto resumeDto,
                                                    @PathVariable Long studentId) {
        try {
            if(resumeDto.projects().size() > 2 || resumeDto.experiences().size() > 2 || resumeDto.academics().size() > 2
            || resumeDto.skills().size() > 5 || resumeDto.complementaryCourses().size() > 2){
                throw new Exception("quantidade nao permitida");
            }
            ResumeModel savedResume = resumeService.createResume(studentId, resumeDto);

            if(savedResume == null){
                throw new Exception("Este aluno ja tem curriculo ou aluno inexistente!");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedResume);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @PutMapping("/{studentId}/{resumeId}")
    public ResponseEntity<ResumeModel> updateResume(@PathVariable Long studentId, @PathVariable long resumeId , @RequestBody ResumeDto resumeDto){
        try{

            long validProjectsCount = resumeDto.projects().stream()
                    .filter(project -> !project.isDelete())
                    .count();

            long validExperiencesCount = resumeDto.experiences().stream()
                    .filter(experience -> !experience.isDelete())
                    .count();

            long validAcademicsCount = resumeDto.academics().stream()
                    .filter(academic -> !academic.isDelete())
                    .count();

            long validSkillsCount = resumeDto.skills().stream()
                    .filter(skill -> !skill.isDelete())
                    .count();

            long validComplemenryCoursesCount = resumeDto.complementaryCourses().stream()
                    .filter(complementaryCourse -> !complementaryCourse.isDelete())
                    .count();

            if(validProjectsCount > 2 || validExperiencesCount > 2 || validAcademicsCount > 2
                    || validSkillsCount > 5 || validComplemenryCoursesCount > 2){
                throw new Exception("quantidade nao permitida");
            }

            ResumeModel resumeFound = resumeService.updateResume(studentId, resumeId, resumeDto);

            if(resumeFound == null){
                throw new Exception("Curriculo nao encontrado");
            }

            return ResponseEntity.status(HttpStatus.OK).body(resumeFound);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
