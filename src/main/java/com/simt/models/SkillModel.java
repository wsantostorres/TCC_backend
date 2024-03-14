package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "SKILLS")
public class SkillModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String nameSkill;
    @Column
    private boolean delete = false;

    @ManyToOne
    @JoinColumn(name="resume_id")
    @JsonIgnore
    private ResumeModel resume;
}
