package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "PROJECTS")
public class ProjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String foundation;
    @Column
    private String titleProject;
    @Column
    private int initialYear;
    @Column
    private int closingYear;

    @ManyToOne
    @JoinColumn(name="resume_id")
    @JsonIgnore
    private ResumeModel resume;
}
