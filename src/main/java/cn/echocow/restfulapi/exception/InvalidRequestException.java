package cn.echocow.restfulapi.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

/**
 * 请求参数异常
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:36
 */
public class InvalidRequestException extends RuntimeException {
    @Getter
    private Errors errors;

    public InvalidRequestException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public InvalidRequestException(Errors errors) {
        this.errors = errors;
    }

}
