package com.simt.utils;

import com.simt.dtos.VacancyGetAllDto;
import com.simt.models.VacancyModel;

import java.time.format.DateTimeFormatter;

public class VacancyMapper {
    public static VacancyGetAllDto mapToDto(VacancyModel vacancyModel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new VacancyGetAllDto(
                vacancyModel.getId(),
                vacancyModel.getTitle(),
                vacancyModel.getClosingDate().format(formatter),
                vacancyModel.getDescription(),
                vacancyModel.getType(),
                vacancyModel.getMorning(),
                vacancyModel.getAfternoon(),
                vacancyModel.getNight()
        );
    }
}
