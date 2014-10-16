/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.service;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by fsuda on 26.08.2014.
 */
public class SettingsService {
    private static final String LOG_TAG = SettingsService.class.getSimpleName();
    public static final String PREFS_NAME = "WoNprefs";

    public static final String PREF_SHOW_STARTUP = "showStartupScreen";
    public static final String PREF_APP_STARTS = "appStarts";

    public static SharedPreferences settings;

    public static boolean showStartupScreen; //PREF_SHOW_STARTUP
    public static int appStarts;        //PREF_APP_STARTS

    public static void init(SharedPreferences settings){
        readPreferences(settings);
        appStarts++;
        writePreferences();
    }

    public static void readPreferences(SharedPreferences settings){
        SettingsService.settings = settings;

        showStartupScreen = settings.getBoolean(PREF_SHOW_STARTUP, true);
        appStarts = settings.getInt(PREF_APP_STARTS, 0);
    }

    /*ONLY WRITE PREFERENCES THAT CAN'T BE SET WITHIN THE PREFERENCE SCREEN
      Other Preferences are persisted by the PreferenceScreen already
    */
    public static void writePreferences() {
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(PREF_APP_STARTS, appStarts);

        editor.commit();
    }
}
