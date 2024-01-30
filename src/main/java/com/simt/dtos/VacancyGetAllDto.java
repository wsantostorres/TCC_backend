package com.simt.dtos;

public record VacancyGetAllDto(long id, String title, String closingDate, String description, int type, int morning, int afternoon, int night){
}
