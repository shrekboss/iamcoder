package org.coder.err.programming._2_design_chapter.apidesgin.apiresponse;

import lombok.Getter;

public class APIException extends RuntimeException {
    @Getter
    private int errorCode;
    @Getter
    private String errorMessage;

    public APIException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public APIException(Throwable cause, int errorCode, String errorMessage) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
