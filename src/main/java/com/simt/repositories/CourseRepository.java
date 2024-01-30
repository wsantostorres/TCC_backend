package com.simt.repositories;

import com.simt.models.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RepositoryRestResource(exported = false)
public interface CourseRepository extends JpaRepository<CourseModel, Long> {
    @Query("SELECT C FROM CourseModel C WHERE C.name = :name")
    CourseModel findByName(@Param("name") String name);
}
