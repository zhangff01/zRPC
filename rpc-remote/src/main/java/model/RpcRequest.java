package model;

import lombok.Data;

/**
 * @author zhangfeifei
 * @Description 请求dto
 * @create 2019/12/17
 */
@Data
public class RpcRequest {

    private String serviceName;

    private String serviceMethod;

    private Object[] arguments;

    private Class<?>[] parameterTypes;

    private Class<?> returnType;

    private String localAddress;

    private String remoteAddress;

    /**
     * 超时时间 默认10秒
     */
    private long timeout = 10000;
}
