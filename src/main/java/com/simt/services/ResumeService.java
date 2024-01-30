package com.simt.services;

import com.simt.dtos.ResumeDto;
import com.simt.models.ResumeModel;
import org.springframework.stereotype.Service;

@Service
public interface ResumeService {
    public ResumeModel getResume(long id);
    public ResumeModel createResume(Long studentId, ResumeDto resumeDto);
    public ResumeModel updateResume(Long studentId, long id, ResumeDto resumeDto);
}
