package com.bt1.qltv1.service;

import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface FileService {
    List<String[]> readCsvFileWithoutHeader(MultipartFile file) throws IOException, CsvException;
}
