package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.TopUp;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.upStream.TopUpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Boss on 2014/9/16.
 */
public class TopUpHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        TopUpResponse response = new TopUpResponse();
        response.parse(resp);

        logger.info("Reference No is {}, result is {}", response.getReferenceId(), response.getTopUpResult());

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);

        Map<String, TopUp> topUpHistory = deviceInfo.getTopUpHistory();

        TopUp topUp = topUpHistory.get(response.getReferenceId());

        topUp.setTopUpResult(MessageUtil.isSuccess(response.getTopUpResult()));
        topUp.setUpdateTime(new Date());

        logger.info("Handle top up for {} successfully: {}", topUp.getReferenceId(), topUp);
        return null;
    }
}
