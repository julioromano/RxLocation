package net.kjulio.rxlocation;

import java.util.WeakHashMap;

/**
 * A singleton object to enable communication between PermissionsActivity and BaseHelper.
 *
 * The main use case is for PermissionsActivity to return control
 * to the calling BaseHelper after its job is done.
 */
class PermissionsRequestLock {

    private final static PermissionsRequestLock instance = new PermissionsRequestLock();

    // WeakHashMap in order to keep WeakReferences of all listeners avoiding the
    // lapsed listener problem.
    private final WeakHashMap<BaseHelper, Integer> listeners = new WeakHashMap<>();

    private PermissionsRequestLock() {}

    static PermissionsRequestLock getInstance() {
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
                baseHelper.onLocationPermissionDialogDismissed();
                // Remove the listener as soon as its callback is invoked once.
                removeListener(baseHelper);
            }
        }
    }
}
