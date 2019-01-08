package cn.echocow.restfulapi.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 字段错误
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:52
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class FieldResource {
    /**
     * 实体对象
     */
    private String resource;
    /**
     * 字段
     */
    private String field;
    /**
     * 代码
     */
    private String code;
    /**
     * 具体信息
     */
    private String message;
}
