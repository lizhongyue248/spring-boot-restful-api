package cn.echocow.restfulapi.validator;

import cn.echocow.restfulapi.entity.Book;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Echo
 * @version 1.0
 * @date 2019-01-08 10:39
 */
@Component
public class BookValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name","NotNull","书名不能为空");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"author","NotNull","作者不能为空");
    }
}
