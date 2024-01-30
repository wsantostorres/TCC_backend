package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "CONTACTS")
public class ContactModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String phone;
    @Column
    private String email;
    @Column
    private String linkedin;

    @OneToOne(mappedBy = "contact")
    @JsonIgnore
    private ResumeModel resume;
}
