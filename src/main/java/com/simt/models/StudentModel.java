package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "STUDENTS")
public class StudentModel {
    @Id
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String registration;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    @Column(nullable = false)
    private String bondType;

    @ManyToOne
    @JoinColumn(name="course_id")
    private CourseModel course;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "VACANCIES_STUDENTS",
            joinColumns = @JoinColumn(name = "student_fk",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_fk",
                    referencedColumnName = "id"))
    @JsonIgnore
    private List<VacancyModel> vacancies = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private ResumeModel resume;
}
