package com.simt.dtos;

import java.util.List;

public record VacancyPageResponse(List<VacancyGetAllDto> vacancies,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize) {
}
