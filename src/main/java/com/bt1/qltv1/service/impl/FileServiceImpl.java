package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.service.FileService;
import com.bt1.qltv1.util.Global;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class FileServiceImpl implements FileService {
    public boolean isCsvFormat(String fileType) {
        return Global.CSV_TYPE.equals(fileType);
    }

    @Override
    public List<String[]> readCsvFileWithoutHeader(MultipartFile file) throws IOException, CsvException {
        if (!isCsvFormat(file.getContentType())){
            throw new BadRequest("Just support CSV file!!","file.type.invalid");
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            // Skip the first row (header)
            reader.skip(1);

            return reader.readAll();
        }
    }
}
