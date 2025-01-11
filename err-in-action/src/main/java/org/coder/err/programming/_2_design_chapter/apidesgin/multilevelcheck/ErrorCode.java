package org.coder.err.programming._2_design_chapter.apidesgin.multilevelcheck;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public enum ErrorCode {
    OK(0, "OK"),
    PARAM_SKU_NULL_ERROR(100, "PARAM_SKU_NULL_ERROR"),
    PARAM_PRICE_NULL_ERROR(101, "PARAM_PRICE_NULL_ERROR"),
    PARAM_STOCK_NULL_ERROR(102, "PARAM_STOCK_NULL_ERROR"),
    PARAM_PRICE_ILLEGAL_ERROR(103, "PARAM_PRICE_ILLEGAL_ERROR"),
    PARAM_STOCK_ILLEGAL_ERROR(104, "PARAM_STOCK_ILLEGAL_ERROR"),
    PARAM_NULL_ERROR(105, "PARAM_NULL_ERROR");

    /**
     * 自定义状态码
     **/
    private final int code;
    /**
     * 自定义描述
     **/
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
