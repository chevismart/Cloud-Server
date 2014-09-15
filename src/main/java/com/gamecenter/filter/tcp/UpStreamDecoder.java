package com.gamecenter.filter.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.gamecenter.serializer.Codec;
import org.gamecenter.serializer.HeaderFilter;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.MessageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Boss on 2014/8/11.
 */
public class UpStreamDecoder extends CumulativeProtocolDecoder {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    // 可变的IoBuffer数据缓冲区
    private IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        logger.debug("doDecode starts!!!");

        int headerLength = Codec.TOTAL_HEADER_LENGTH;

        // If message is shorter than header's length, return false for waiting data.
        if (!isHeaderLengthValid(in, headerLength)) return false;

        if (in.hasRemaining()) {

            in.mark(); //标记当前位置，以便reset

            MessageHeader header = createMsgHeader(in, headerLength);

            // If the body message is shorter than the length defined in header, return false for waiting data.
            if (!isBodyLengthValid(in, header)) return false;

            in.reset();

            byte[] msgByte = getMessageInByteArray(header,in);

            Map<MessageType, byte[]> resultMap = new HashMap<MessageType, byte[]>();
            resultMap.put(header.getMsgType(), msgByte);

//            if (in.hasRemaining()) { // 如果有剩余的数据，则放入Session中
//                // 将数据移到buffer的最前面
//                IoBuffer temp = IoBuffer.allocate(2048).setAutoExpand(
//                        true);
//                temp.put(in);
//                temp.flip();
//                in.clear();
//                in.put(temp);
//
//            } else { // 如果数据已经处理完毕，进行清空
//                in.clear();
//            }
            out.write(resultMap);

            logger.info("Received message bytes = {}", ByteArrayUtil.toHexString(msgByte));

//            in.position(position);


            return true;//这里有两种情况1：没数据了，那么就结束当前调用，有数据就再次调用
        }
//        in.flip();
        return false;
    }

    private byte[] getMessageInByteArray(MessageHeader header, IoBuffer in){
        int totalMsgLength = Codec.TOTAL_HEADER_LENGTH + header.getMsgBodyLength() + Codec.TAILER_LENGTH;
        byte[] msgByte = new byte[totalMsgLength];
        int position = 0;
        for (int i = 0; i < totalMsgLength; i++) {
            msgByte[i] = in.get();
            position++;
        }

        return msgByte;

    }

    private boolean isHeaderLengthValid(IoBuffer in, int headerLength) {
        logger.debug("Position before length checking = " + String.valueOf(in.position()));
        if (in.hasRemaining() && in.remaining() < headerLength) {
            logger.debug("There is not enough data for decode.");
            in.reset();
            logger.debug("Position after length checking = " + String.valueOf(in.position()));
            return false;
        }
        return true;
    }

    private MessageHeader createMsgHeader(IoBuffer in, int headerLength) {
        byte[] headerByte = new byte[headerLength];

        for (int i = 0; i < headerLength; i++) {
            headerByte[i] = in.get();
        }
        MessageHeader header = HeaderFilter.getMessageHeader(headerByte, MessageLoader.INSTANCE());
        return header;
    }

    private boolean isBodyLengthValid(IoBuffer in, MessageHeader header) {

        int bodyLength = header.getMsgBodyLength();
        int remainingLength = bodyLength + Codec.TAILER_LENGTH;

        if (null == header || remainingLength > in.remaining()) {
            logger.warn("The package is too short to be decoded. Body plus tailer require length: {}, but is {}",
                    bodyLength, in.remaining());
            return false;
        }

        return true;
    }


//    // 是否可以解码
//    private boolean canDecode(IoBuffer buf) {
//        int protocalHeadLength = Codec.TOTAL_HEADER_LENGTH;// 协议头长度
//        int remaining = buf.remaining();
//        if (remaining < protocalHeadLength) {
//            logger.error("错误，协议不完整，协议头长度小于" + protocalHeadLength);
//            return false;
//        } else {
//            logger.debug("协议头完整");
//            // 获取协议tag
//            byte tag = buf.get();
//            if (tag == JConstant.REQ || tag == JConstant.RES) {
//                logger.debug("Tag=" + tag);
//            } else {
//                log.error("错误，未定义的Tag类型");
//                return false;
//            }
//            // 获取协议体长度
//            int length = buf.getInt();
//            if (buf.remaining() < length) {
//                log.error("错误，真实协议体长度小于消息头中取得的值");
//                return false;
//            } else {
//                log.debug("真实协议体长度:" + buf.remaining() + " = 消息头中取得的值:" + length);
//            }
//        }
//        return true;
//    }
}
