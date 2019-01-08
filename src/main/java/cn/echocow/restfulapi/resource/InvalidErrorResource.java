package cn.echocow.restfulapi.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 信息封装
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:52
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class InvalidErrorResource {
    private String message;
    private Object errors;
}
