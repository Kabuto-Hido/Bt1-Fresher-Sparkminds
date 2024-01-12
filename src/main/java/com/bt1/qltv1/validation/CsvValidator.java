package com.bt1.qltv1.validation;

import com.bt1.qltv1.util.Global;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CsvValidator implements ConstraintValidator<CsvType, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        String contentType = file.getContentType();
        if (contentType==null){
            return false;
        }
        return isCsvFormat(contentType);
    }
    public boolean isCsvFormat(String fileType) {
        return Global.CSV_TYPE.equals(fileType);
    }
}
