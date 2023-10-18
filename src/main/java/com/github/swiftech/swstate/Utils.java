package com.github.swiftech.swstate;

/**
 * @author swiftech
 * @since 2.0
 */
public class Utils {

    public static String payloadSummary(Object payload) {
        return payload == null ? "null" :
                payload.toString().substring(0, Math.min(16, payload.toString().length()));
    }
}
