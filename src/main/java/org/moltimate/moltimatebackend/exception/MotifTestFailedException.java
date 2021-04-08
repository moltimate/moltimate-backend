package org.moltimate.moltimatebackend.exception;

import org.springframework.web.multipart.MultipartFile;

public class MotifTestFailedException extends RuntimeException {

    public MotifTestFailedException(String message) {
        super(message);
    }

}
