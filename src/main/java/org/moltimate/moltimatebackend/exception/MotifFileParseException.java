package org.moltimate.moltimatebackend.exception;

import org.springframework.web.multipart.MultipartFile;

public class MotifFileParseException extends RuntimeException {

    public MotifFileParseException(MultipartFile file) {
        super("Cannot parse custom motif with filename: " + file.getOriginalFilename());
    }
}
