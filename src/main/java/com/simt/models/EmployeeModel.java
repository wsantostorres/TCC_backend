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
@Table(name = "EMPLOYEES")
public class EmployeeModel {
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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VacancyModel> vacancies = new ArrayList<>();
}
