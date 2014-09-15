package com.gamecenter.handler;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.constants.ServerEnum;
import com.gamecenter.model.HttpRequestMessage;
import com.sun.deploy.net.HttpResponse;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpServerHandlerTest{

    Map<String, String> context = new HashMap<String, String>();

    String centerId = "centerId";
    String token = "tokenStr";
    String type = ServerEnum.Json.RequestType.CLIENT_LIST.name();
    String dataType = ServerEnum.Json.DataType.JSONP.name();

    HttpRequestMessage requestMessage = mock(HttpRequestMessage.class);
    IoSession ioSession = mock(IoSession.class);

    @Test
    public void receiveHttpRequestWithMandatoryParametersCheck(){
        when(requestMessage.getParameter(ServerConstants.JsonConst.CENTER_ID)).thenReturn(centerId);
        when(requestMessage.getParameter(ServerConstants.JsonConst.TOKEN)).thenReturn(token);
        when(requestMessage.getParameter(ServerConstants.JsonConst.REQUEST_TYPE)).thenReturn(type);
        when(requestMessage.getParameter(ServerConstants.JsonConst.DATA_TYPE)).thenReturn(dataType);
        WriteFuture writeFuture = mock (WriteFuture.class);
        when(ioSession.write(any(Byte[].class))).thenReturn(writeFuture);
        when(writeFuture.addListener(any(IoFutureListener.class))).thenReturn(null);

        HttpServerHandler handler = new HttpServerHandler();
        handler.messageReceived(ioSession, requestMessage);

        verify(requestMessage, atLeastOnce()).getParameter(ServerConstants.JsonConst.CENTER_ID);
        verify(requestMessage, atLeastOnce()).getParameter(ServerConstants.JsonConst.TOKEN);
        verify(requestMessage, atLeastOnce()).getParameter(ServerConstants.JsonConst.REQUEST_TYPE);
        verify(requestMessage, atLeastOnce()).getParameter(ServerConstants.JsonConst.DATA_TYPE);

        verify(ioSession,times(1)).write(any(Byte[].class));
    }

    @Test
    public void requestDataWithInvalidCredentialAndReturn404() throws Exception {
        Map<String, String> mockTokenMap = new HashMap<String, String>();
        mockTokenMap.put("mockId","mockToken");
        HttpServerHandler handler = new HttpServerHandler();
        Whitebox.setInternalState(handler,"tokenMap", mockTokenMap);

        when(requestMessage.getParameter(ServerConstants.JsonConst.CENTER_ID)).thenReturn(centerId);
        when(requestMessage.getParameter(ServerConstants.JsonConst.TOKEN)).thenReturn(token);
        when(requestMessage.getParameter(ServerConstants.JsonConst.REQUEST_TYPE)).thenReturn(type);
        when(requestMessage.getParameter(ServerConstants.JsonConst.DATA_TYPE)).thenReturn(dataType);
        WriteFuture writeFuture = mock (WriteFuture.class);
        when(ioSession.write(any(Byte[].class))).thenReturn(writeFuture);
        when(writeFuture.addListener(any(IoFutureListener.class))).thenReturn(null);


        handler.messageReceived(ioSession,requestMessage);


    }
}