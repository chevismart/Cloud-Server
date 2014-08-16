package server.Filter;

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

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.hasRemaining()) {
            int minLength = Codec.TOTAL_HEADER_LENGTH;
            //这里很关键，网上很多代码都没有这句，是用来当拆包时候剩余长度小于4的时候的保护，不加就出错咯
            if (in.remaining() < minLength) return false;

            in.mark();//标记当前位置，以便reset
            int msgLength = in.remaining();

//        if (minLength > msgLength) {//如果消息内容不够，则重置，相当于不读取size
//            System.out.println("package notenough  left=" + in.remaining() + " length=" + minLength);
//            in.reset();
//            return false;//接收新数据，以拼凑成完整数据
//        } else {
            System.out.println("Package =" + in.toString());

            byte[] headerByte = new byte[minLength];

            for (int i = 0; i < minLength; i++) {
                headerByte[i] = in.get();
            }
            MessageHeader header = HeaderFilter.getMessageHeader(headerByte, MessageLoader.INSTANCE());

            int bodyLength = header.getMsgBodyLength();
            int remainingLength = bodyLength + Codec.TAILER_LENGTH;

            if (null == header || remainingLength < in.remaining()) {
                logger.warn("The package is too short to be decoded. Body plus tailer require length: {}, but is {}", bodyLength, in.remaining());
                return false;
            }

            in.reset();
            int msgActualLength = Codec.TOTAL_HEADER_LENGTH + bodyLength + Codec.TAILER_LENGTH;
            byte[] msgByte = new byte[msgActualLength];
            for (int i = 0; i < msgActualLength; i++) {
                msgByte[i] = in.get();
            }

            Map<MessageType, byte[]> resultMap = new HashMap<MessageType, byte[]>();
            resultMap.put(header.getMsgType(), msgByte);

            if (in.hasRemaining()) { // 如果有剩余的数据，则放入Session中
                // 将数据移到buffer的最前面
                IoBuffer temp = IoBuffer.allocate(2048).setAutoExpand(
                        true);
                temp.put(in);
                temp.flip();
                in.clear();
                in.put(temp);

            } else { // 如果数据已经处理完毕，进行清空
                in.clear();
            }
            out.write(resultMap);

            return true;//这里有两种情况1：没数据了，那么就结束当前调用，有数据就再次调用
        }
        return false;
    }
}
