package com.deha.app.utils;

import java.util.UUID;

public class Utils {

    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
