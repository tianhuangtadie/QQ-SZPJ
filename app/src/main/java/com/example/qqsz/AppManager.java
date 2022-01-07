package com.example.qqsz;

/**
 * Created by MXY on 2018/9/18.
 */
import android.app.Activity;

import java.util.Stack;

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    public AppManager() {
    }

    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    // 添加Activity到堆栈
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
                if (i == activityStack.size() ) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }
        }
        activityStack.clear();
    }
}