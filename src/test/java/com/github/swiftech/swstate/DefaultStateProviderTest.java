package com.github.swiftech.swstate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.swiftech.swstate.TestConstants.*;

/**
 * @author swiftech
 */
class DefaultStateProviderTest {

    @Test
    void setState() {
        DefaultStateProvider<String> provider = new DefaultStateProvider<>();
        provider.setState("state-1", s1);
        provider.setState("state-2", s1);
        provider.setState("state-1", s2);
        provider.setState("state-2", s2);
        provider.setState("state-1", s3);
        Assertions.assertEquals(s3, provider.getCurrentState("state-1"));
        Assertions.assertEquals(s2, provider.getCurrentState("state-2"));
        Assertions.assertTrue(provider.isState("state-1", s3));
        Assertions.assertTrue(provider.isState("state-2", s2));
        Assertions.assertTrue(provider.isStateIn("state-1", s2, s3));
        Assertions.assertTrue(provider.isStateIn("state-2", s2, s3));
    }

}