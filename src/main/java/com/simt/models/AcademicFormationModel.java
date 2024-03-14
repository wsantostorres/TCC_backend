package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "ACADEMIC_FORMATIONS")
public class AcademicFormationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String schooling;
    @Column
    private String foundation;
    @Column
    private int initialYear;
    @Column
    private int closingYear;
    @Column
    private boolean delete = false;

    @ManyToOne
    @JoinColumn(name="resume_id")
    @JsonIgnore
    private ResumeModel resume;
}
