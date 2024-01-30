package com.simt.configs;

import com.simt.models.CourseModel;
import com.simt.repositories.CourseRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegisterCourses {

    @Autowired
    CourseRepository courseRepository;

    @PostConstruct
    public void registerCourses() {
        if (courseRepository.count() == 0) {

            CourseModel tads = new CourseModel();
            tads.setName("Tecnologia em Análise e Desenvolvimento de Sistemas");
            courseRepository.save(tads);

            CourseModel tpq = new CourseModel();
            tpq.setName("Tecnologia em Processos Químicos");
            courseRepository.save(tpq);

            CourseModel adm = new CourseModel();
            adm.setName("Técnico de Nível Médio em Administração");
            courseRepository.save(adm);

            CourseModel qui = new CourseModel();
            qui.setName("Técnico de Nível Médio em Química");
            courseRepository.save(qui);

            CourseModel inf = new CourseModel();
            inf.setName("Técnico de Nível Médio em Informática");
            courseRepository.save(inf);

        }
    }
}