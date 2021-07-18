package com.github.swiftech.swstate;

import org.apache.commons.lang3.RandomUtils;

import java.util.Iterator;

/**
 * @author swiftech
 */
public class TestConstants {

    public static final String s1 = "Open";
    public static final String s2 = "Fixed";
    public static final String s3 = "Tested";
    public static final String s4 = "Closed";

    public static final String[] states = new String[]{s1, s2, s3};

    public static final String[] stateSequence = new String[]{s2, s1, s2, s3, s1, s4};

    /**
     * Choose a state randomly in all pre-defined stats.
     *
     * @return
     */
    public static String randomState() {
        int i = RandomUtils.nextInt(0, 3);
        return states[i];
    }

    /**
     * Iterate all pre-defined states again and again until at the specified size.
     */
    public static class StateIterator implements Iterator<String> {
        private final int size;
        private int idx = 0;

        public StateIterator(int size) {
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return idx < size - 1;
        }

        @Override
        public String next() {
            int i = idx % stateSequence.length;
            idx += 1;
            return stateSequence[i];
        }
    }

    public static void main(String[] args) {
        StateIterator si = new StateIterator(100);
        while (si.hasNext()) {
            System.out.println(si.next());
        }
    }

}
