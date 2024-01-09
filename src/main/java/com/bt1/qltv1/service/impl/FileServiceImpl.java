package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.service.FileService;
import com.bt1.qltv1.util.Global;
import com.opencsv.CSVReader;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FileServiceImpl implements FileService {
    public boolean isCsvFormat(String fileType) {
        return Global.CSV_TYPE.equals(fileType);
    }

    @Override
    public <T> List<String[]> readCsvFileWithoutHeader(MultipartFile file, Class<T> bean) throws IOException,
            CsvException {
        if (!isCsvFormat(file.getContentType())) {
            throw new BadRequest("Just support CSV file!!", "file.type.invalid");
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {

            //validation header
            validationHeader(reader, bean);
            
            return reader.readAll();
        }
    }

    public <T> void validationHeader(CSVReader reader, Class<T> bean) throws CsvRequiredFieldEmptyException,
            IOException, CsvValidationException {

        HeaderColumnNameMappingStrategy<T> strategy =
                new HeaderColumnNameMappingStrategy<>();
        strategy.setType(bean);

        //read header from file csv
        String[] actualHeaders = reader.readNext();

        //read header from entity
        String[] expectedHeaders = strategy.generateHeader((T) bean);
        Set<String> expectedHeaderSet = new HashSet<>(Arrays.asList(expectedHeaders));

        if (actualHeaders.length < expectedHeaders.length) {
            throw new CsvRequiredFieldEmptyException("Missing header column.");
        } else if (actualHeaders.length > expectedHeaders.length) {
            throw new IOException("Unexpected extra header column.");
        }

        for (String header : actualHeaders) {
            if (!expectedHeaderSet.contains(header.toUpperCase())) {
                throw new BadRequest("The header " + header + " of file .csv not correct!",
                        "import-" + bean.getSimpleName() + ".header.invalid");
            }
        }

    }

}
