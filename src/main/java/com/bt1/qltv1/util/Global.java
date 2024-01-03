package com.bt1.qltv1.util;

import java.util.Random;
import java.util.UUID;

public class Global {
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_LIMIT = "10";
    public static final String DEFAULT_SORT_BY = "createdDate";
    public static final String NEW_RESET_PASSWORD = "Sparkminds1*";
    public static final String CSV_TYPE = "text/csv";
    public static final int FILE_SIZE = 5 * 1024 * 1024;
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 1800000;   //30 MINUTES
    public static final String DEFAULT_IMAGE = "https://cptudong.vmts.vn/content/images/thumbs/default-image_450.png";
    public static String randomNumber() {
        return UUID.randomUUID().toString().replace("-", "");

    }
    public static String getOTP() {
        long time = System.currentTimeMillis();
        return String.format("%06d", new Random(time).nextInt(999999));
    }
    public static final String DEFAULT_AVATAR = "https://firebasestorage.googleapis.com/v0/b/cnpm-30771.appspot.com/o/no-user.png?alt=media&token=517e08ab-6aa4-42eb-9547-b1b10f17caf0";
}
