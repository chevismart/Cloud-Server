package com.gamecenter.constants;

/**
 * Created by Boss on 2014/9/8.
 */
public class ServerEnum {
    public static class Json {
        public enum DataType {
            JSON("json"),
            JSONP("jsonp");
            String name;

            DataType(String name) {
                this.name = name;
            }
        }

        public enum RequestType {
            CLIENT_LIST("client_list"),
            COUNTER_STATUS("counter_status"),
            COUNTER_QTY("counter_qty"),
            COUNTER_RESET("counter_reset"),
            TOP_UP("top_up");
            String name;

            RequestType(String name) {
                this.name = name;
            }
        }
    }
}
