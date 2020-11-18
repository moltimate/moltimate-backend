package org.moltimate.moltimatebackend.exception;

import org.springframework.web.multipart.MultipartFile;

public class DockingJobFailedException extends RuntimeException {
	private MultipartFile error;
	public DockingJobFailedException(String message, MultipartFile error) {
		super(message);
		this.error = error;
	}

	public MultipartFile getError() {
		return error;
	}
}
