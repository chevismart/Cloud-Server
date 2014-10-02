package com.gamecenter.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonUtilTest {

    @Test
    public void converListToJson() throws Exception {
        List<String> listInStr = new ArrayList<String>();
        listInStr.add("a");
        listInStr.add("b");
        String exceptedStr = "[\"a\",\"b\"]";
        String jsonStr = JsonUtil.getJsonFromList(listInStr);
        assertEquals(exceptedStr, jsonStr);
    }

    @Test
    public void convertMapToJson() throws Exception {
        Map<String, String> normalMap = new HashMap<String, String>();
        normalMap.put("a", "abc");
        String exceptedStr = "{\"a\":\"abc\"}";
        String jsonStr = JsonUtil.getJsonFromMap(normalMap);
        assertEquals(exceptedStr, jsonStr);
    }
}