package com.gamecenter.server.http;

import com.gamecenter.model.HttpResponseMessage;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

public class HttpServerProtocolCodecFactory extends
        DemuxingProtocolCodecFactory {
    public HttpServerProtocolCodecFactory() {
        super.addMessageDecoder(HttpRequestDecoder.class);
        super.addMessageEncoder(HttpResponseMessage.class,
                HttpResponseEncoder.class);
    }

}