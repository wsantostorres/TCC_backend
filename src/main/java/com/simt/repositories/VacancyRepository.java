package com.simt.repositories;

import com.simt.models.VacancyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RepositoryRestResource(exported = false)
public interface VacancyRepository extends JpaRepository<VacancyModel, Long> {
    @Query("SELECT v FROM VacancyModel v WHERE v.deletedAt IS NULL AND upper(trim(v.title)) like %:title%")
    Page<VacancyModel> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT v FROM VacancyModel v JOIN v.courses c WHERE v.deletedAt IS NULL AND upper(trim(v.title)) like %:title% AND c.id = :courseId")
    Page<VacancyModel> searchByTitleAndCourse(@Param("title") String title, @Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT v FROM VacancyModel v JOIN v.courses c WHERE v.deletedAt IS NULL AND c.id = :courseId")
    Page<VacancyModel> findByCourses(@Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT v FROM VacancyModel v WHERE v.deletedAt IS NULL")
    Page<VacancyModel> findAllVacancies(Pageable pageable);


}
