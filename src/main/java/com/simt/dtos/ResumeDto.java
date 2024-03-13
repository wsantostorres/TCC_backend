package com.simt.dtos;

import com.simt.models.*;

import java.util.List;

public record ResumeDto ( List<ComplementaryCourseModel> complementaryCourses,
                        List<ProjectModel> projects,
                         List<ExperienceModel> experiences,
                         List<AcademicFormationModel> academics,
                         List<SkillModel> skills,
                         AddressModel address,
                         ContactModel contact){
}