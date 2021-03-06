package com.gamecenter.handler;

import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;

import java.util.Date;

/**
 * HTTP请求的处理接口
 *
 * @author Ajita
 */
public interface HttpJsonHandler {
    /**
     * 自定义HTTP请求处理需要实现的方法
     *
     * @param request 一个HTTP请求对象
     * @return HTTP请求处理后的返回结果
     */
    HttpResponseMessage handle(HttpRequestMessage request);

    boolean await();

    Date getRequestTime();

    Date getUpdateTime();
}