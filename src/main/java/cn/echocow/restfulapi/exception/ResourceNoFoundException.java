package cn.echocow.restfulapi.exception;

/**
 * 资源未找到异常
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 22:36
 */
public class ResourceNoFoundException extends RuntimeException {
    public ResourceNoFoundException(String message) {
        super(message);
    }
}
