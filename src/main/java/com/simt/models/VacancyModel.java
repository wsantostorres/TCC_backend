package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "VACANCIES")
public class VacancyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDateTime closingDate;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int type;

    @Column
    private int morning;

    @Column
    private int afternoon;

    @Column
    private int night;

    @Column(nullable = false)
    private LocalDateTime lastModified;

    @ManyToMany(mappedBy = "vacancies", fetch = FetchType.LAZY)
    private List<CourseModel> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "vacancies", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StudentModel> students = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="employee_id")
    private EmployeeModel employee;
}
