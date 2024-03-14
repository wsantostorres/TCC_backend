package com.simt.services.Impl;

import com.simt.dtos.ResumeDto;
import com.simt.services.ResumeService;
import com.simt.models.*;
import com.simt.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ResumeRepository resumeRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ExperienceRepository experienceRepository;

    @Autowired
    AcademicFormationRepository academicFormationRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    ComplementaryCourseRepository complementaryCourseRepository;

    @Override
    @Transactional
    public ResumeModel getResume(long id) {
        Optional<ResumeModel> resumeModelOptional = resumeRepository.findById(id);
        return resumeModelOptional.orElse(null);
    }

    @Override
    @Transactional
    public ResumeModel createResume(Long studentId, ResumeDto resumeDto) {
        try {
            Optional<StudentModel> studentOptional = studentRepository.findById(studentId);

            /* Só posso cadastrar se o aluno não tiver currículo cadastrado */
            if(studentOptional.isPresent() && studentOptional.get().getResume() == null){
                ResumeModel resumeModel = new ResumeModel();

                List<ProjectModel> projects = new ArrayList<>();
                List<ExperienceModel> experiences = new ArrayList<>();
                List<AcademicFormationModel> academics = new ArrayList<>();
                List<SkillModel> skills = new ArrayList<>();
                List<ComplementaryCourseModel> complementaryCourses = new ArrayList<>();

                /* Adicionando Cursos Complementares */
                for (ComplementaryCourseModel complementaryCourse : resumeDto.complementaryCourses()) {
                    complementaryCourse.setResume(resumeModel);
                    complementaryCourses.add(complementaryCourse);
                }

                /* Adicionando Projetos */
                for (ProjectModel project : resumeDto.projects()) {
                    project.setResume(resumeModel);
                    projects.add(project);
                }

                /* Adicionando Experiencias */
                for (ExperienceModel experience : resumeDto.experiences()) {
                    experience.setResume(resumeModel);
                    experiences.add(experience);
                }

                /* Adicionando Formações Acadêmicas */
                for (AcademicFormationModel academic : resumeDto.academics()){
                    academic.setResume(resumeModel);
                    academics.add(academic);
                }

                /* Adicionando habilidades */
                for (SkillModel skill : resumeDto.skills()){
                    skill.setResume(resumeModel);
                    skills.add(skill);
                }

                /* Adicionando endereço */
                AddressModel address = resumeDto.address();
                address.setResume(resumeModel);

                /*Adicionando contato*/
                ContactModel contact = resumeDto.contact();
                contact.setResume(resumeModel);

                /* Relacionando Aluno e Currículo */
                studentOptional.get().setResume(resumeModel);

                resumeModel.setComplementaryCourses(complementaryCourses);
                resumeModel.setProjects(projects);
                resumeModel.setExperiences(experiences);
                resumeModel.setAcademics(academics);
                resumeModel.setSkills(skills);
                resumeModel.setAddress(resumeDto.address());
                resumeModel.setContact(resumeDto.contact());

                return resumeRepository.save(resumeModel);
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Ocorreu um erro ao salvar o currículo.", e);
        }
    }

    @Override
    @Transactional
    public ResumeModel updateResume(Long studentId, long id, ResumeDto resumeDto) {
        Optional<ResumeModel> resumeModelOptional = resumeRepository.findById(id);
        Optional<StudentModel> studentOptional = studentRepository.findById(studentId);

        if(resumeModelOptional.isPresent() && studentOptional.isPresent()
        && resumeModelOptional.get().getStudent().getId().equals(studentOptional.get().getId())){

            ResumeModel existingResumeModel = resumeModelOptional.get();

            /* Atualizando Cursos complementares */
            for(ComplementaryCourseModel complementaryCourse : resumeDto.complementaryCourses()){
                if(complementaryCourse.isDelete()){
                    complementaryCourseRepository.deleteById(complementaryCourse.getId());
                }else{
                    complementaryCourse.setResume(existingResumeModel);
                    existingResumeModel.getComplementaryCourses().add(complementaryCourse);
                    complementaryCourseRepository.save(complementaryCourse);
                }
            }


            /* Atualizando Projetos */
            for(ProjectModel project : resumeDto.projects()){
                if(project.isDelete()){
                    projectRepository.deleteById(project.getId());
                }else{
                    project.setResume(existingResumeModel);
                    existingResumeModel.getProjects().add(project);
                    projectRepository.save(project);
                }
            }

            /* Atualizando Experiencias */
            for(ExperienceModel experience : resumeDto.experiences()){
                if(experience.isDelete()){
                    experienceRepository.deleteById(experience.getId());
                }else{
                    experience.setResume(existingResumeModel);
                    existingResumeModel.getExperiences().add(experience);
                    experienceRepository.save(experience);
                }
            }

            /* Atualizando Formações Acadêmicas */
            for(AcademicFormationModel academicFormation : resumeDto.academics()){
                if(academicFormation.isDelete()){
                    academicFormationRepository.deleteById(academicFormation.getId());
                }else{
                    academicFormation.setResume(existingResumeModel);
                    existingResumeModel.getAcademics().add(academicFormation);
                    academicFormationRepository.save(academicFormation);
                }
            }

            /* Atualizando Habilidades */
            for(SkillModel skill : resumeDto.skills()){
                if(skill.isDelete()){
                    skillRepository.deleteById(skill.getId());
                }else{
                    skill.setResume(existingResumeModel);
                    existingResumeModel.getSkills().add(skill);
                    skillRepository.save(skill);
                }
            }

            /* Atualizando endereço */
            AddressModel address = resumeDto.address();
            if(existingResumeModel.getAddress() != null){
                existingResumeModel.getAddress().setCity(address.getCity());
                existingResumeModel.getAddress().setStreet(address.getStreet());
                existingResumeModel.getAddress().setNumber(address.getNumber());
            }

            /* Atualizando contato */
            ContactModel contact = resumeDto.contact();
            if(existingResumeModel.getContact() != null){
                existingResumeModel.getContact().setPhone(contact.getPhone());
                existingResumeModel.getContact().setEmail(contact.getEmail());
                existingResumeModel.getContact().setLinkedin(contact.getLinkedin());
            }

            return resumeRepository.save(existingResumeModel);
        }

        return null;

    }
}
