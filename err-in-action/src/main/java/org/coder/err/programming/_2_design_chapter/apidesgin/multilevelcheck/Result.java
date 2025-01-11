package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck;

import lombok.Data;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class Result<T> {
    private int status;
    private String message;
    private T data;
    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }


    public static <T> Result<T> success(T data) {
        Result<T> resultData = new Result<>();
        resultData.setStatus(ErrorCode.OK.getCode());
        resultData.setMessage(ErrorCode.OK.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> Result<T> success() {
        Result<T> resultData = new Result<>();
        resultData.setStatus(ErrorCode.OK.getCode());
        resultData.setMessage(ErrorCode.OK.getMessage());
        return resultData;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> resultData = new Result<>();
        resultData.setStatus(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> Result<T> failure(ErrorCode paramNullError) {
        Result<T> resultData = new Result<>();
        resultData.setStatus(paramNullError.getCode());
        resultData.setMessage(paramNullError.getMessage());
        return resultData;
    }

    public boolean isSuccess() {
        return true;
    }
}
