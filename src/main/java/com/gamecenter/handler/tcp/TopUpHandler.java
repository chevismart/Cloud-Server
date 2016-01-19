package com.gamecenter.handler.tcp;

import com.gamecenter.handler.TcpHandler;
import com.gamecenter.handler.queue.Queues;
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

import static org.gamecenter.serializer.constants.MessageType.TopUpRequest;

public class TopUpHandler implements TcpHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Queues queues;

    public TopUpHandler(Queues queues) {
        this.queues = queues;
    }

    @Override
    public byte[] handle(byte[] resp, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {

        TopUpResponse response = new TopUpResponse();
        response.parse(resp);

        logger.info("Reference No. is {}, result is {}", response.getReferenceId(), response.getTopUpResult());

        DeviceInfo deviceInfo = SessionUtil.getDeviceInfoByIoSession(session);

        if (deviceInfo != null) {
            Map<String, TopUp> topUpHistory = deviceInfo.getTopUpHistory();
            if (topUpHistory != null) {
                TopUp topUp = topUpHistory.get(response.getReferenceId());
                if (topUp != null) {
                    topUp.setTopUpResult(MessageUtil.isSuccess(response.getTopUpResult()));
                    topUp.setUpdateTime(new Date());
                    topUp.setDeviceReplied(true);

                    queues.consume(TopUpRequest, deviceInfo.getMac(), topUp);
                    logger.info("Handle top up for {} successfully: {}", topUp.getReferenceId(), topUp);
                    topUpHistory.remove(response.getReferenceId());
                } else {
                    logger.warn("Top up history not found for refId: [{}]", response.getReferenceId());
                }
            } else {
                logger.warn("Top up history is not found!");
            }
        }

        return null;
    }
}
