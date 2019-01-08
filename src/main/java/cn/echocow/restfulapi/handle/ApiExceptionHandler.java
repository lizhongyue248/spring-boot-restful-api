package cn.echocow.restfulapi.handle;

import cn.echocow.restfulapi.exception.InvalidRequestException;
import cn.echocow.restfulapi.exception.ResourceNoFoundException;
import cn.echocow.restfulapi.resource.ErrorResource;
import cn.echocow.restfulapi.resource.FieldResource;
import cn.echocow.restfulapi.resource.InvalidErrorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;

/**
 * 对异常进行拦截然后封装到响应体
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:59
 */
@RestControllerAdvice(basePackages = "cn.echocow.restfulapi.controller")
public class ApiExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(ResourceNoFoundException.class)
    public HttpEntity<?> handleNotFound(ResourceNoFoundException e) {
        ErrorResource errorResource = new ErrorResource(e.getMessage());
        logger.error(errorResource.toString());
        return new ResponseEntity<>(errorResource, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public HttpEntity<?> handleInvalidRequest(InvalidRequestException e) {
        Errors errors = e.getErrors();
        List<FieldResource> fieldResources = new ArrayList<>();
        List<FieldError> fieldErrors = errors.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            fieldResources.add(
                    new FieldResource(fieldError.getObjectName(),
                            fieldError.getField(),
                            fieldError.getCode(),
                            fieldError.getDefaultMessage())
            );
        }
        InvalidErrorResource invalidErrorResource = new InvalidErrorResource(e.getMessage(), fieldResources);
        logger.error(invalidErrorResource.toString());
        return new ResponseEntity<>(invalidErrorResource, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public HttpEntity<?> handleException(Exception e){
        logger.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
