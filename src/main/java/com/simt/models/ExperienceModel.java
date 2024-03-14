package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "EXPERIENCES")
public class ExperienceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String company;
    @Column
    private String functionName;
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
