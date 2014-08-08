package server;

import org.apache.mina.core.session.IoSession;

import java.util.HashMap;

/**
 * Created by Boss on 2014/8/6.
 */
public class Initialization {

    private static HashMap<String, IoSession> sessionHashMap;

    private static Initialization instence;

    public Initialization() {
        sessionHashMap = new HashMap<String, IoSession>();
    }

    public static Initialization getInstance() {

        if (null == instence) {
            instence = new Initialization();
        }
        return instence;
    }

    public HashMap<String,IoSession> getClientMap() {
        return sessionHashMap;
    }
}
