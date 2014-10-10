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
            CLIENT_LIST("CLIENT_LIST"),
            COUNTER_STATUS("COUNTER_STATUS"),
            COUNTER_QTY("COUNTER_QTY"),
            COUNTER_RESET("COUNTER_RESET"),
            COUNTER_SWITCH("COUNTER_SWITCH"),
            TOP_UP("TOP_UP"),
            POWER_STATUS("POWER_STATUS"),
            POWER_CONTROL("POWER_CONTROL");
            String name;

            RequestType(String name) {
                this.name = name;
            }
        }
    }
}
