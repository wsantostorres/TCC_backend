package com.simt.services.Impl;

import com.simt.models.*;
import com.simt.services.GenerateResumeService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class GenerateResumeServiceImpl implements GenerateResumeService {
    public byte[] generateResumesZip(List<StudentModel> students) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

            for (StudentModel student : students) {
                String resumeHtml = generateResumeHtml(student);

                /* Converter HTML para XHTML */
                Document resumeXhtml = Jsoup.parse(resumeHtml);

                /* Converter XHTML em PDF com OpenHTMLToPDF */
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(resumeXhtml.toString(), null);
                builder.toStream(pdfOutputStream);
                builder.run();

                // Adicionar o arquivo PDF do currículo ao arquivo ZIP
                ZipEntry entry = new ZipEntry(student.getFullName() + student.getRegistration() + ".pdf");
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(pdfOutputStream.toByteArray());
                zipOutputStream.closeEntry();
            }

            zipOutputStream.finish();
            zipOutputStream.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateResumePDF(StudentModel student){
        try{
            String resumeHtml = generateResumeHtml(student);

            /* Converter HTML para XHTML */
            Document resumeXhtml = Jsoup.parse(resumeHtml);

            /* Converter XHTML em PDF com OpenHTMLToPDF */
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(resumeXhtml.toString(), null);
            builder.toStream(pdfOutputStream);
            builder.run();

            return pdfOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateResumeHtml(StudentModel student) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ResumeTemplate.html");

            if (inputStream != null) {
                String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                /* ENDEREÇO E CONTATO */
                StringBuilder addressAndContactHtml = new StringBuilder();
                AddressModel addressStudent = student.getResume().getAddress();
                String address = "<p> <span>Endereço: </span>" + addressStudent.getStreet() + ", "
                        + addressStudent.getNumber() + " - " + addressStudent.getCity() + "</p>";
                String email = "<p> <span>Email: </span>" + student.getResume().getContact().getEmail() + "</p>";
                String phone = "<p> <span>Nº Telefone: </span>" + student.getResume().getContact().getPhone() + "</p>";
                String linkedin = "";
                if(!student.getResume().getContact().getLinkedin().isEmpty()){
                    linkedin = "<p> <span>Linkedin: </span>" + student.getResume().getContact().getLinkedin() + "</p>";
                }
                addressAndContactHtml.append(email);
                addressAndContactHtml.append(phone);
                addressAndContactHtml.append(linkedin);
                addressAndContactHtml.append(address);

                /* FORMAÇÕES ACADÊMICAS */
                StringBuilder formationsHtml = new StringBuilder();
                if(student.getResume().getAcademics().size() > 0){
                    htmlTemplate = htmlTemplate.replace("{TITLE_FORMACAO}", "<h2>FORMAÇÃO</h2>");
                }else{
                    htmlTemplate = htmlTemplate.replace("{TITLE_FORMACAO}", "");
                }
                for(AcademicFormationModel academic : student.getResume().getAcademics()){
                    String valuesAcademic = "<li> <p> <b>" + academic.getSchooling() +  "</b> </p>" +
                            "<span>" + academic.getInitialYear() +
                            "-" + academic.getClosingYear() +
                            " | " + academic.getFoundation() + "</span> </li>";
                    formationsHtml.append(valuesAcademic);
                }

                /* CURSOS COMPLEMENTARES */
                StringBuilder complementaryCoursesHtml = new StringBuilder();
                if(student.getResume().getComplementaryCourses().size() > 0){
                    htmlTemplate = htmlTemplate.replace("{TITLE_CURSOS_COMPLEMENTARES}", "<h2>CURSOS COMPLEMENTARES</h2>");
                }else{
                    htmlTemplate = htmlTemplate.replace("{TITLE_CURSOS_COMPLEMENTARES}", "");
                }
                for(ComplementaryCourseModel complementaryCourse : student.getResume().getComplementaryCourses()){
                    String valuesComplementaryCourse = "<li> <p> <b>"  + complementaryCourse.getCourseName() + "</b></p>" +
                            "<span>" + complementaryCourse.getInitialYear() +
                            "-" + complementaryCourse.getClosingYear() +
                            " | " + complementaryCourse.getFoundation() + "</span> </li>";
                    complementaryCoursesHtml.append(valuesComplementaryCourse);


                }

                /* EXPERIÊNCIAS */
                StringBuilder experiencesHtml = new StringBuilder();
                if(student.getResume().getExperiences().size() > 0){
                    htmlTemplate = htmlTemplate.replace("{TITLE_EXPERIENCIA}", "<h2>EXPERIÊNCIAS</h2>");
                }else{
                    htmlTemplate = htmlTemplate.replace("{TITLE_EXPERIENCIA}", "");
                }
                for(ExperienceModel experience : student.getResume().getExperiences()){
                    String valuesExperience = "<li>" + "<p> <b>" + experience.getFunctionName() +  " </b> </p>" +
                            "<span>"+ experience.getInitialYear() + "-" +
                            experience.getClosingYear() +
                            " | " + experience.getCompany() + "</span>" +
                            "<p class=\"activities\">" + experience.getActivities() + "</p>" +
                            "</li>";
                    experiencesHtml.append(valuesExperience);
                }

                /* PROJETOS */
                StringBuilder projectsHtml = new StringBuilder();
                if(student.getResume().getProjects().size() > 0){
                    htmlTemplate = htmlTemplate.replace("{TITLE_PROJETO}", "<h2>PROJETOS</h2>");
                }else{
                    htmlTemplate = htmlTemplate.replace("{TITLE_PROJETO}", "");
                }
                for(ProjectModel project : student.getResume().getProjects()){
                    String valuesProject = "<li>" + "<p> <b>" + project.getTitleProject() + "</b> </p>" +
                            "<span>" + project.getInitialYear() +
                            "-" + project.getClosingYear() +
                            " | " + project.getFoundation() + "</span>" +
                            "<p class=\"activities\">" + project.getActivities() + "</p>" +
                            "</li>";
                    projectsHtml.append(valuesProject);
                }

                /* HABILIDADES */
                StringBuilder skillsHtml = new StringBuilder();
                if(student.getResume().getSkills().size() > 0){
                    htmlTemplate = htmlTemplate.replace("{TITLE_HABILIDADES}", "<h2>HABILIDADES</h2>");
                }else{
                    htmlTemplate = htmlTemplate.replace("{TITLE_HABILIDADES}", "");
                }
                for(SkillModel skill : student.getResume().getSkills()){
                    String valueSkill = "<li>" + skill.getNameSkill() + "</li>";
                    skillsHtml.append(valueSkill);
                }

                /* Substituir informações do currículo do Aluno aqui */
                htmlTemplate = htmlTemplate.replace("{NOMECOMPLETO}", student.getFullName());
                htmlTemplate = htmlTemplate.replace("{CONTATOENDERECO}", addressAndContactHtml);
                htmlTemplate = htmlTemplate.replace("{HABILIDADES}", skillsHtml);
                htmlTemplate = htmlTemplate.replace("{FORMACOES}", formationsHtml);
                htmlTemplate = htmlTemplate.replace("{PROJETOS}", projectsHtml);
                htmlTemplate = htmlTemplate.replace("{EXPERIENCIAS}", experiencesHtml);
                htmlTemplate = htmlTemplate.replace("{CURSOSCOMPLEMENTARES}", complementaryCoursesHtml);

                return htmlTemplate;
            } else {
                return "Erro: Arquivo HTML não encontrado.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro na leitura do arquivo HTML.";
        }
    }
}
