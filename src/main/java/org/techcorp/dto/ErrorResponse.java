package org.techcorp.dto;

import java.time.Instant;

public class ErrorResponse {
    public String message;
    public Instant timestamp;
    public int status;
    public String path;

    public ErrorResponse(String message, int status, String path) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
    }
}