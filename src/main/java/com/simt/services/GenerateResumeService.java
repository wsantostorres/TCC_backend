package com.simt.services;


import com.simt.models.StudentModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GenerateResumeService {
    public byte[] generateResumesZip(List<StudentModel> students);
    public byte[] generateResumePDF(StudentModel student);
}
