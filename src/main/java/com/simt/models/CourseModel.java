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
@Table(name = "COURSES")
public class CourseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentModel> students = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "VACANCIES_COURSES",
            joinColumns = @JoinColumn(name = "course_fk",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_fk",
                    referencedColumnName = "id"))
    @JsonIgnore
    private List<VacancyModel> vacancies = new ArrayList<>();


}
