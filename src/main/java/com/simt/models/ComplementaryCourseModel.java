package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "COMPLEMENTARY_COURSES")
public class ComplementaryCourseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String foundation;
    @Column
    private String courseName;
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
