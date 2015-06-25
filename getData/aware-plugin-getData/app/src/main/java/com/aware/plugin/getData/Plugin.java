package com.aware.plugin.getData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Plugin;

import java.util.Timer;
import java.util.TimerTask;

public class Plugin extends Aware_Plugin {

    public static boolean endClick = true;
    public static long start = 0;
    public static long end = 0;
    public static long startSave = 0;
    public static long endSave = 0;
    public static long interval = 10000;

    @Override
    public void onCreate() {
        super.onCreate();
        if( DEBUG ) Log.d(TAG, "getData plugin running");

        //Initialize our plugin's settings
        if( Aware.getSetting(this, Settings.STATUS_PLUGIN_GETDATA).length() == 0 ) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_GETDATA, true);
        }

        // Activate sensor
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 60000);

        //Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_GYROSCOPE, true);
        //Aware.setSetting(this, Aware_Preferences.FREQUENCY_GYROSCOPE, 100000);

        //Any active plugin/sensor shares its overall context using broadcasts
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Ask AWARE to apply your settings
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
        TAG = "getData";
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if( DEBUG ) Log.d(TAG, "getData plugin terminated");
        Aware.setSetting(this, Settings.STATUS_PLUGIN_GETDATA, false);

        //Deactivate any sensors/plugins you activated here
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, false);
        //Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_GYROSCOPE, false);

        //Ask AWARE to apply your settings
        sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
    }
}
