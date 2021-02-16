package com.flextech.building.aws.s3;

public class UploadFailedException extends RuntimeException {
    public UploadFailedException() {
        super();
    }

    public UploadFailedException(String message) {
        super(message);
    }
}
