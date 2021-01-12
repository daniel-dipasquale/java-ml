package com.experimental.stress;

public interface StressTestNotification {
    StressTestNotification EMPTY = new StressTestNotification() {
        @Override
        public void notifyStarting(final StressTest stressTest) {
        }

        @Override
        public void notifyCompleted(final StressTest stressTest) {
        }
    };

    void notifyStarting(StressTest stressTest);

    void notifyCompleted(StressTest stressTest);
}
