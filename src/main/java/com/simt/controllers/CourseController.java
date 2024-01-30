package com.simt.controllers;

import com.simt.models.CourseModel;
import com.simt.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<List<CourseModel>> getAllCourses(){
        return ResponseEntity.status(HttpStatus.OK).body(courseRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<CourseModel> createCourse(@RequestBody CourseModel course){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseRepository.save(course));
    }
}
