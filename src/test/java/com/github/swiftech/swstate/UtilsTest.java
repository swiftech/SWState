package com.github.swiftech.swstate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 * @since 2.0
 */
public class UtilsTest {

    @Test
    public void payloadSummary() {
        Assertions.assertEquals("null", Utils.payloadSummary(null));
        Assertions.assertEquals("1234567890123456", Utils.payloadSummary("1234567890123456"));
        Assertions.assertEquals("1234567890123456", Utils.payloadSummary("12345678901234567"));
    }
}
