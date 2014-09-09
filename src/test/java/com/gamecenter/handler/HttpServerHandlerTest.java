package com.gamecenter.handler;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.constants.SeverEnum;
import com.gamecenter.model.HttpRequestMessage;
import junit.framework.TestCase;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpServerHandlerTest{

    Map<String, String> context = new HashMap<String, String>();

    String centerId = "center";
    String token = "chevis";
    String type = SeverEnum.Json.RequestType.CLIENT_LIST.name();
    String dataType = SeverEnum.Json.DataType.JSONP.name();

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

        verify(ioSession,times(1)).write(any(Byte[].class)).addListener(IoFutureListener.CLOSE);
//        context.put(ServerConstants.JsonConst.CENTER_ID, centerId);
//        context.put(ServerConstants.JsonConst.TOKEN, token);
//        context.put(ServerConstants.JsonConst.REQUEST_TYPE, type);
//        context.put(ServerConstants.JsonConst.DATA_TYPE,dataType);
    }
}