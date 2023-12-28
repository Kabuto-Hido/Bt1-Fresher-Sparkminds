package com.bt1.qltv1.util;

import java.util.Random;
import java.util.UUID;

public class Global {
    public static final String NEW_RESET_PASSWORD = "Sparkminds1*";
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 1800000;   //30 MINUTES
    public static String randomNumber() {
        return UUID.randomUUID().toString().replace("-", "");

    }
    public static String getOTP() {
        long time = System.currentTimeMillis();
        return String.format("%06d", new Random(time).nextInt(999999));
    }
    public static final String DEFAULT_AVATAR = "https://firebasestorage.googleapis.com/v0/b/cnpm-30771.appspot.com/o/no-user.png?alt=media&token=517e08ab-6aa4-42eb-9547-b1b10f17caf0";
}
