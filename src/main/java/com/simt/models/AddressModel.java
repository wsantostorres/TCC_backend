package com.simt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "ADDRESS")
public class AddressModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String city;
    @Column
    private String street;
    @Column
    private int number;

    @OneToOne(mappedBy = "address")
    @JsonIgnore
    private ResumeModel resume;
}
