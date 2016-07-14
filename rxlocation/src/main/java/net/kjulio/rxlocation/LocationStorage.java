package net.kjulio.rxlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Utility class to store and retrieve location data from a dedicated shared preferences file.
 * We use .apply() instead of .commit() as we are not really sensitive to write delays in this
 * specific case.
 */
class LocationStorage {

    private static final String PREF_FILE_NAME = "RxLocationV1";
    private static final String KEY_LAST_LOC = "lastLoc";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    LocationStorage(Context context, Gson gson) {
        this.sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        this.gson = gson;
    }

    void putLastLoc(Location lastLoc) {
        sharedPreferences.edit().putString(KEY_LAST_LOC, gson.toJson(lastLoc)).apply();
    }

    @Nullable
    Location getLastLoc() {
        if (sharedPreferences.contains(KEY_LAST_LOC)) {
            return gson.fromJson(sharedPreferences.getString(KEY_LAST_LOC, null), Location.class);
        } else {
            return null;
        }
    }

    void deleteLastLoc() {
        if (sharedPreferences.contains(KEY_LAST_LOC)) {
            sharedPreferences.edit().remove(KEY_LAST_LOC).apply();
        }
    }

    void deleteAll() {
        sharedPreferences.edit().clear().apply();
    }
}
