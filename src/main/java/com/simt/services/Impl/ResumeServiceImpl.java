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
                resumeModel.setObjectiveDescription(resumeDto.objectiveDescription());

                List<ProjectModel> projects = new ArrayList<>();
                List<ExperienceModel> experiences = new ArrayList<>();
                List<AcademicFormationModel> academics = new ArrayList<>();
                List<SkillModel> skills = new ArrayList<>();

                /* Adicionando Projetos */
                for (ProjectModel project : resumeDto.projects()) {
                    if(!project.getTitleProject().isEmpty() && !project.getFoundation().isEmpty()){
                        project.setResume(resumeModel);
                        projects.add(project);
                    }
                }

                /* Adicionando Experiencias */
                for (ExperienceModel experience : resumeDto.experiences()) {
                    if(!experience.getCompany().isEmpty() && !experience.getFunctionName().isEmpty()){
                        experience.setResume(resumeModel);
                        experiences.add(experience);
                    }
                }

                /* Adicionando Formações Acadêmicas */
                for (AcademicFormationModel academic : resumeDto.academics()){
                    if(!academic.getFoundation().isEmpty() || !academic.getSchooling().isEmpty()){
                        academic.setResume(resumeModel);
                        academics.add(academic);
                    }
                }

                /* Adicionando habilidades */
                for (SkillModel skill : resumeDto.skills()){
                    if(!skill.getNameSkill().isEmpty()) {
                        skill.setResume(resumeModel);
                        skills.add(skill);
                    }
                }

                /* Adicionando endereço */
                AddressModel address = resumeDto.address();
                address.setResume(resumeModel);

                /*Adicionando contato*/
                ContactModel contact = resumeDto.contact();
                contact.setResume(resumeModel);

                /* Relacionando Aluno e Currículo */
                studentOptional.get().setResume(resumeModel);

                resumeModel.setProjects(projects);
                resumeModel.setExperiences(experiences);
                resumeModel.setAcademics(academics);
                resumeModel.setSkills(skills);
                resumeModel.setAddress(resumeDto.address());
                resumeModel.setContact(resumeDto.contact());

                ResumeModel savedResume;
                return savedResume = resumeRepository.save(resumeModel);
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
            existingResumeModel.setObjectiveDescription(resumeDto.objectiveDescription());

            /* Atualizando Projetos */
            for (ProjectModel project : resumeDto.projects()) {
                if (project.getId() != null) {
                    Optional<ProjectModel> existingProject = projectRepository.findById(project.getId());
                    if (existingProject.isPresent()) {
                        /* Se por acaso um projeto existir e tentar atualizar com campos vazios
                          vou excluir o projeto */
                        if(project.getTitleProject().isEmpty() && project.getFoundation().isEmpty()){
                            projectRepository.deleteById(project.getId());
                        }else{
                            existingProject.get().setFoundation(project.getFoundation());
                            existingProject.get().setTitleProject(project.getTitleProject());
                            existingProject.get().setInitialYear(project.getInitialYear());
                            existingProject.get().setClosingYear(project.getClosingYear());
                        }
                    }
                }else{
                    if(!project.getTitleProject().isEmpty() && !project.getFoundation().isEmpty()){
                        project.setResume(existingResumeModel);
                        existingResumeModel.getProjects().add(project);
                    }
                }
            }

            /* Atualizando Experiencias */
            for (ExperienceModel experience : resumeDto.experiences()) {
                if (experience.getId() != null) {
                    Optional<ExperienceModel> existingExperience = experienceRepository.findById(experience.getId());
                    if (existingExperience.isPresent()) {
                        if(experience.getCompany().isEmpty() && experience.getFunctionName().isEmpty()){
                            experienceRepository.deleteById(experience.getId());
                        }else{
                            existingExperience.get().setCompany(experience.getCompany());
                            existingExperience.get().setFunctionName(experience.getFunctionName());
                            existingExperience.get().setInitialYear(experience.getInitialYear());
                            existingExperience.get().setClosingYear(experience.getClosingYear());
                        }
                    }
                }else{
                    if(!experience.getCompany().isEmpty() && !experience.getFunctionName().isEmpty()){
                        experience.setResume(existingResumeModel);
                        existingResumeModel.getExperiences().add(experience);
                    }
                }
            }

            /* Atualizando Formações Acadêmicas */
            for (AcademicFormationModel academic : resumeDto.academics()){
                if(academic.getId() != null){
                    Optional<AcademicFormationModel> existingAcademicFormation
                            = academicFormationRepository.findById(academic.getId());
                    if(existingAcademicFormation.isPresent()){
                        if(academic.getFoundation().isEmpty() || academic.getSchooling().isEmpty()){
                            academicFormationRepository.deleteById(academic.getId());
                        }else {
                            existingAcademicFormation.get().setFoundation(academic.getFoundation());
                            existingAcademicFormation.get().setSchooling(academic.getSchooling());
                            existingAcademicFormation.get().setInitialYear(academic.getInitialYear());
                            existingAcademicFormation.get().setClosingYear(academic.getClosingYear());
                        }
                    }
                }else{
                    if(!academic.getFoundation().isEmpty() || !academic.getSchooling().isEmpty()){
                        academic.setResume(existingResumeModel);
                        existingResumeModel.getAcademics().add(academic);
                    }
                }
            }

            /* Atualizando Habilidades */
            for (SkillModel skill : resumeDto.skills()){
                if(skill.getId() != null){
                    Optional<SkillModel> existingSkill
                            = skillRepository.findById(skill.getId());
                    if(existingSkill.isPresent()){
                        if(skill.getNameSkill().isEmpty()){
                            skillRepository.deleteById(skill.getId());
                        }else{
                            existingSkill.get().setNameSkill(skill.getNameSkill());
                        }
                    }
                }else{
                    if(!skill.getNameSkill().isEmpty()) {
                        skill.setResume(existingResumeModel);
                        existingResumeModel.getSkills().add(skill);
                    }
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

            ResumeModel updatedResume;
            return updatedResume = resumeRepository.save(existingResumeModel);
        }

        return null;

    }
}
