package cn.echocow.restfulapi.config;

import cn.echocow.restfulapi.validator.BookValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

/**
 * @author Echo
 * @version 1.0
 * @date 2019-01-08 10:56
 */
@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeSave", new BookValidator());
        validatingListener.addValidator("beforeCreate", new BookValidator());
    }
}
