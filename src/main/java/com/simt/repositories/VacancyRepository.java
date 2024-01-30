package com.simt.repositories;

import com.simt.models.VacancyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "*")
@RepositoryRestResource(exported = false)
public interface VacancyRepository extends JpaRepository<VacancyModel, Long> {
    @Query(value = "SELECT V FROM VacancyModel V WHERE upper(trim(V.title)) like %?1%")
    List<VacancyModel> searchByTitle(String title);
}
