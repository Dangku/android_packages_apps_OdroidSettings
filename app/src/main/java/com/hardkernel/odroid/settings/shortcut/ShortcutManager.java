package com.hardkernel.odroid.settings.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class ShortcutManager {
    /*
     * pkg[] - shorcut function key index
     * 0 - F7 ~ 3 - F10
     */
    private static String pkg[] = new String[4];

    private static SharedPreferences pref;
    private static final String PREFERENCE_NAME = "shortcut_preference";

    private static WindowManager wm;
    private static PackageManager pm;

    public static void initShortcuts(Context context) {
        pm = context.getPackageManager();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        for (int i=0;i<4; i++)
            pkg[i] = pref.getString("shortcut_f" + (i+7), null);

        ArrayList<String> appList = getAvailableAppList(context);
        PackageManager pm = context.getPackageManager();
        for (int i = 0; i < 4; i++) {
            for (String app : appList) {
                if (app.equals(pkg[i])) {
                    wm.setApplicationShortcut(KeyEvent.KEYCODE_F7 + i, pm.getLaunchIntentForPackage(app));
                }
            }
        }
    }

    public static String pkgNameAt(int index) {
        return pkg[index];
    }

    private static List<ApplicationInfo> appList = null;

    public static ArrayList<String> getAvailableAppList(Context context) {
        appList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> launchApps = new ArrayList<>();
        for (ApplicationInfo appInfo: appList) {
            Intent launchApp = pm.getLaunchIntentForPackage(appInfo.packageName);
            if (launchApp != null)
                launchApps.add(appInfo.packageName);
        }

        return launchApps;
    }

    public static List<ApplicationInfo> getAppList() {
        return appList;
    }

    public static void setShortcutPreference(int keycode, String app) {
        SharedPreferences.Editor edit = pref.edit();

        String shortcut_pref =
                "shortcut_f" + ((keycode - KeyEvent.KEYCODE_F1) + 1);

        if (app == null) {
            wm.setApplicationShortcut(keycode, null);
            edit.putString(shortcut_pref, "No shortcut");
        } else {
            wm.setApplicationShortcut(keycode, pm.getLaunchIntentForPackage(app));
            edit.putString(shortcut_pref, app);
        }
        edit.commit();
    }
}