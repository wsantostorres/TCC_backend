package com.simt.controllers;

import com.simt.dtos.VacancyPageResponse;
import com.simt.models.StudentModel;
import com.simt.repositories.StudentRepository;
import com.simt.services.GenerateResumeService;
import com.simt.utils.VacancyMapper;
import com.simt.dtos.VacancyDto;
import com.simt.dtos.VacancyGetAllDto;
import com.simt.models.CourseModel;
import com.simt.models.VacancyModel;
import com.simt.repositories.CourseRepository;
import com.simt.repositories.VacancyRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vacancies")
public class VacancyController {
    @Autowired
    VacancyRepository vacancyRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    GenerateResumeService generateResumeService;

    @GetMapping
    public ResponseEntity<VacancyPageResponse> getAllVacancies(@RequestHeader(name = "bondType") String bondType,
                                                               @RequestHeader(name = "courseId", required = false) Long courseId,
                                                               @RequestParam(defaultValue = "1") int page){

        Sort sort = null;
        Pageable pageable = null;
        Page<VacancyModel> pageVacanciesModel = null;

        try{

            if(bondType.equals("Servidor")){
                sort = Sort.by(Sort.Direction.DESC, "modifiedIn");
                pageable = PageRequest.of(page - 1, 9, sort);
                pageVacanciesModel = vacancyRepository.findAll(pageable);
            }else{
                sort = Sort.by(Sort.Direction.DESC, "modifiedIn");
                pageable = PageRequest.of(page - 1, 9, sort);
                pageVacanciesModel = vacancyRepository.findByCourses(courseId, pageable);
            }

            List<VacancyGetAllDto> listVacanciesDto = pageVacanciesModel.stream()
                    .map(VacancyMapper::mapToDto)
                    .collect(Collectors.toList());

            VacancyPageResponse response;

            if (listVacanciesDto.isEmpty()) {
                response = new VacancyPageResponse(Collections.emptyList(), 0, 0, 0, 0);
            } else {
                response = new VacancyPageResponse(
                        listVacanciesDto,
                        pageVacanciesModel.getTotalElements(),
                        pageVacanciesModel.getTotalPages(),
                        pageVacanciesModel.getNumber(),
                        pageVacanciesModel.getSize()
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable long id){

        Optional<VacancyModel> vacancyOptional = vacancyRepository.findById(id);

        if(vacancyOptional.isPresent()) {
            /* Tive que fazer isso para formatar a data antes de enviar pro front*/
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            VacancyModel vacancyModel = vacancyOptional.get();
            String formattedClosingDate = vacancyModel.getClosingDate().format(formatter);

            VacancyDto vacancyDto = new VacancyDto(
                    vacancyModel.getId(),
                    vacancyModel.getTitle(),
                    formattedClosingDate,
                    vacancyModel.getDescription(),
                    vacancyModel.getType(),
                    vacancyModel.getMorning(),
                    vacancyModel.getAfternoon(),
                    vacancyModel.getNight(),
                    vacancyModel.getCourses()

            );

            return ResponseEntity.status(HttpStatus.OK).body(vacancyDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/search")
    public ResponseEntity<VacancyPageResponse> searchByTitle(
            @RequestHeader(name = "bondType") String bondType,
            @RequestHeader(name = "courseId", required = false) Long courseId,
            @RequestParam(name = "title") String title,
            @RequestParam(defaultValue = "1") int page){

        Sort sort = null;
        Pageable pageable = null;
        Page<VacancyModel> pageVacanciesModel = null;

        try{

            if(bondType.equals("Servidor")){
                sort = Sort.by(Sort.Direction.DESC, "modifiedIn");
                pageable = PageRequest.of(page - 1, 9, sort);
                pageVacanciesModel = vacancyRepository.searchByTitle(title.trim().toUpperCase(), pageable);
            }else{

                sort = Sort.by(Sort.Direction.DESC, "modifiedIn");
                pageable = PageRequest.of(page - 1, 9, sort);
                pageVacanciesModel = vacancyRepository.searchByTitleAndCourse(title.trim().toUpperCase(), courseId, pageable);
            }

            List<VacancyGetAllDto> listVacanciesDto = pageVacanciesModel.stream()
                    .map(VacancyMapper::mapToDto)
                    .collect(Collectors.toList());

            VacancyPageResponse response;

            if (listVacanciesDto.isEmpty()) {
                response = new VacancyPageResponse(Collections.emptyList(), 0, 0, 0, 0);
            } else {
                response = new VacancyPageResponse(
                        listVacanciesDto,
                        pageVacanciesModel.getTotalElements(),
                        pageVacanciesModel.getTotalPages(),
                        pageVacanciesModel.getNumber(),
                        pageVacanciesModel.getSize()
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/download-resumes/{id}")
    public ResponseEntity<Object> downloadResumes(@PathVariable long id) {
        try {
            Optional<VacancyModel> vacancyOptional = vacancyRepository.findById(id);

            if (vacancyOptional.isPresent()) {
                VacancyModel vacancy = vacancyOptional.get();
                List<StudentModel> studentsByVacancy = vacancy.getStudents();

                if (studentsByVacancy.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ainda não há alunos participando desta vaga.");
                }

                byte[] zipBytes = generateResumeService.generateResumesZip(studentsByVacancy);

                if (zipBytes != null) {
                    String fileName = vacancy.getTitle();

                    // Configurar a resposta HTTP com o arquivo ZIP contendo os PDFs
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType("application/zip"));
                    headers.setContentDispositionFormData("attachment", fileName + ".zip");
                    headers.setContentLength(zipBytes.length);

                    return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar o arquivo ZIP.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }

    @PostMapping
    public ResponseEntity<VacancyModel> createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        LocalDate parsedClosingDate = LocalDate.parse(vacancyDto.closingDate(), DateTimeFormatter.ISO_DATE);
        LocalDateTime combinedDateTime = LocalDateTime.of(parsedClosingDate, LocalTime.of(23, 59));

        VacancyModel requestData = new VacancyModel();
        requestData.setTitle(vacancyDto.title());
        requestData.setDescription(vacancyDto.description());
        requestData.setType(vacancyDto.type());
        requestData.setMorning(vacancyDto.morning());
        requestData.setAfternoon(vacancyDto.afternoon());
        requestData.setNight(vacancyDto.night());
        requestData.setClosingDate(combinedDateTime);
        requestData.setModifiedIn(LocalDateTime.now());

        /* Aqui está ocorrendo o relacionamento entre
        vagas e cursos na hora do cadastro */
        List<CourseModel> courses = courseRepository.findAll();
        List<CourseModel> vacancyCoursesDto = vacancyDto.courses();

        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<CourseModel> selectedCourses = new ArrayList<>();

        for (CourseModel course : courses) {
            for (CourseModel vacancyCourseDto : vacancyCoursesDto) {
                if (vacancyCourseDto.getId() == course.getId()) {
                    course.getVacancies().add(requestData);
                    selectedCourses.add(course);
                }
            }
        }

        if(selectedCourses.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        requestData.setCourses(selectedCourses);

        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyRepository.save(requestData));
    }

    @PostMapping("/send-resume/{studentId}/{vacancyId}")
    public ResponseEntity<Object> sendResumeForVacancy(@PathVariable Long studentId, @PathVariable long vacancyId) {
        try{
            Optional<StudentModel> studentOptional = studentRepository.findById(studentId);
            Optional<VacancyModel> vacancyOptional = vacancyRepository.findById(vacancyId);

            if(studentOptional.isPresent() && vacancyOptional.isPresent()) {
                StudentModel student = studentOptional.get();
                VacancyModel vacancy = vacancyOptional.get();
                CourseModel studentCourse = student.getCourse();

                if (!vacancy.getCourses().contains(studentCourse)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }

                if (vacancy.getStudents().contains(student)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
                }

                if(student.getResume() == null){
                    return ResponseEntity.status(HttpStatus.LOCKED).body(null);
                }

                student.getVacancies().add(vacancy);
                vacancy.getStudents().add(student);

                studentRepository.save(student);
                vacancyRepository.save(vacancy);

                return ResponseEntity.status(HttpStatus.OK).body(null);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacancyModel> updateVacancy(@PathVariable long id, @Valid @RequestBody VacancyDto vacancyDto) {
        try {
            Optional<VacancyModel> existingVacancyOptional = vacancyRepository.findById(id);

            if (existingVacancyOptional.isPresent()) {

                VacancyModel existingVacancy = existingVacancyOptional.get();

                /* Aqui, na hora de atualizar a vaga eu
                estou removendo todos os cursos da vaga existente
                e depois colocando os novos cursos que vieram, isso pode ser melhorado
                porque às vezes os cursos enviados podem ser iguais aos que já tem, e aqui
                acaba removendo e adicionando de novo de qualquer forma. */
                List<CourseModel> currentCourses = existingVacancy.getCourses();
                List<CourseModel> coursesToRemove = new ArrayList<>();

                for (CourseModel currentCourse : currentCourses) {
                    currentCourse.getVacancies().remove(existingVacancy);
                    coursesToRemove.add(currentCourse);
                }

                existingVacancy.getCourses().removeAll(coursesToRemove);

                List<CourseModel> courses = courseRepository.findAll();
                List<CourseModel> vacancyCoursesDto = vacancyDto.courses();

                List<CourseModel> selectedCourses = new ArrayList<>();

                for (CourseModel course : courses) {
                    for (CourseModel vacancyCourseDto : vacancyCoursesDto) {
                        if (vacancyCourseDto.getId() == course.getId()) {
                            course.getVacancies().add(existingVacancy);
                            selectedCourses.add(course);
                        }
                    }
                }

                LocalDate parsedClosingDate = LocalDate.parse(vacancyDto.closingDate(), DateTimeFormatter.ISO_DATE);
                LocalDateTime combinedDateTime = LocalDateTime.of(parsedClosingDate, LocalTime.of(23, 59));

                existingVacancy.setTitle(vacancyDto.title());
                existingVacancy.setDescription(vacancyDto.description());
                existingVacancy.setType(vacancyDto.type());
                existingVacancy.setMorning(vacancyDto.morning());
                existingVacancy.setAfternoon(vacancyDto.afternoon());
                existingVacancy.setNight(vacancyDto.night());
                existingVacancy.setClosingDate(combinedDateTime);
                existingVacancy.setModifiedIn(LocalDateTime.now());
                existingVacancy.setCourses(selectedCourses);

                vacancyRepository.save(existingVacancy);

                return ResponseEntity.status(HttpStatus.OK).body(null);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVacancy(@PathVariable long id){
        try{
            Optional<VacancyModel> vacancyOptional = vacancyRepository.findById(id);

            if(vacancyOptional.isPresent()){
                VacancyModel vacancy = vacancyOptional.get();
                List<CourseModel> relatedCourses = vacancy.getCourses();

                for (CourseModel relatedCourse : relatedCourses) {
                    relatedCourse.getVacancies().remove(vacancy);
                }

                List<StudentModel> relatedStudents = vacancy.getStudents();
                for (StudentModel relatedStudent : relatedStudents) {
                    relatedStudent.getVacancies().remove(vacancy);
                }

                vacancyRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}