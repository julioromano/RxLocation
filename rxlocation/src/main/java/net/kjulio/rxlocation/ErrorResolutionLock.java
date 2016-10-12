package net.kjulio.rxlocation;

import java.util.WeakHashMap;

/**
 * A singleton object to enable communication between ErrorResolutionActivity and BaseHelper.
 *
 * The main use case is for ErrorResolutionActivity to return control
 * to the calling BaseHelper after its job is done.
 */
class ErrorResolutionLock {

    private final static ErrorResolutionLock instance = new ErrorResolutionLock();

    // WeakHashMap in order to keep WeakReferences of all listeners avoiding the
    // lapsed listener problem.
    private final WeakHashMap<BaseHelper, Integer> listeners = new WeakHashMap<>();

    private ErrorResolutionLock() {}

    static ErrorResolutionLock getInstance() {
        return instance;
    }

    void addListener(BaseHelper baseHelper) {
        listeners.put(baseHelper, baseHelper.hashCode());
    }

    void removeListener(BaseHelper baseHelper) {
        listeners.remove(baseHelper);
    }

    void notifyListeners() {
        for (BaseHelper baseHelper : listeners.keySet()) {
            if (baseHelper != null) {
                baseHelper.onErrorResolutionActivityDismissed();
                // Remove the listener as soon as its callback is invoked once.
                removeListener(baseHelper);
            }
        }
    }
}
