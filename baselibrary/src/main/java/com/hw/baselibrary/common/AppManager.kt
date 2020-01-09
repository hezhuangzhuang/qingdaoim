package com.hw.baselibrary.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 *author：pc-20171125
 *data:2019/11/7 15:40
 */
class AppManager private constructor() {
    private val activityStack: Stack<Activity> = Stack();

    companion object {
        val instance: AppManager by lazy {
            AppManager()
        }
    }

    /**
     * activity入栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * activity出栈
     */
    fun pushActivity(activity: Activity) {
        activity.finish()
        activityStack.remove(activity)
    }

    /**
     * 清理栈
     */
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
    }

    /**
     * 退出应用程序
     */
    fun exitApp(context: Context) {
        finishAllActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }

}