package com.deliveroo.rider.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;

    public CommonResult<T> generateOK(String message, T data) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setMessage(message);
        commonResult.setCode(HttpStatus.OK.value());
        commonResult.setData(data);
        return commonResult;
    }

    public CommonResult<T> generateBadRequest(String message, T data) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setMessage(message);
        commonResult.setCode(HttpStatus.BAD_REQUEST.value());
        commonResult.setData(data);
        return commonResult;
    }

    public CommonResult (int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
