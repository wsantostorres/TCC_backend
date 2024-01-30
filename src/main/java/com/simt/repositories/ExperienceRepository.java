package com.simt.repositories;

import com.simt.models.ExperienceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RepositoryRestResource(exported = false)
public interface ExperienceRepository extends JpaRepository<ExperienceModel, Long> {
}
