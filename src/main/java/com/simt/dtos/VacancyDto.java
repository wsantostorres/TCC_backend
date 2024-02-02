package com.simt.dtos;

import com.simt.models.CourseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VacancyDto (long id,
                          @NotBlank @Size(max = 100) String title,
                          @NotBlank String closingDate,
                          @NotBlank @Size(max = 500) String description,
                          @NotNull int type, @NotNull int morning,
                          @NotNull int afternoon,
                          @NotNull int night,
                          @NotEmpty
                          List<CourseModel> courses,
                          @NotNull Long employeeId){
}

