package net.kjulio.rxlocation;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton object to enable communication between ErrorResolutionActivity and BaseHelper.
 *
 * The main use case is for ErrorResolutionActivity to return control
 * to the calling BaseHelper after its job is done.
 *
 * Listeners are kept into WeakReferences that are deleted as soon as notifyListeners() is called.
 * There is no need for a removeListener() method therefore.
 */
class ErrorResolutionLock {

    private final static ErrorResolutionLock instance = new ErrorResolutionLock();

    private final ConcurrentHashMap<WeakReference<BaseHelper>, Integer> listeners =
            new ConcurrentHashMap<>();

    private ErrorResolutionLock() {}

    static ErrorResolutionLock getInstance() {
        return instance;
    }

    void addListener(BaseHelper baseHelper) {
        listeners.put(new WeakReference<>(baseHelper), baseHelper.hashCode());
    }

    void notifyListeners() {
        for (WeakReference<BaseHelper> weakReference : listeners.keySet()) {
            BaseHelper baseHelper = weakReference.get();
            if (baseHelper != null) {
                baseHelper.onErrorResolutionActivityDismissed();
            }
            // Remove the listener as soon as its callback is invoked once.
            listeners.remove(weakReference);
        }
    }
}
