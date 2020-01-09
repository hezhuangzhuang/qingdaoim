package com.hw.baselibrary.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.hw.baselibrary.common.BaseApp

/**
 *author：pc-20171125
 *data:2019/11/7 17:17
 */
object KeyboardUtils {

    private var sDecorViewInvisibleHeightPre: Int = 0
    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var onSoftInputChangedListener: OnSoftInputChangedListener? = null
    private var sContentViewInvisibleHeightPre5497: Int = 0

    private fun KeyboardUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 显示软键盘
     * Show the soft input.
     *
     * @param activity The activity.
     */
    fun showSoftInput(activity: Activity) {
        showSoftInput(activity, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 显示软键盘
     * Show the soft input.
     *
     * @param activity The activity.
     * @param flags    Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    fun showSoftInput(activity: Activity, flags: Int) {
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        showSoftInput(view, flags)
    }

    /**
     * 显示软键盘
     * Show the soft input.
     *
     * @param view The view.
     */
    fun showSoftInput(view: View) {
        showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * 显示软键盘
     * Show the soft input.
     *
     * @param view  The view.
     * @param flags Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    fun showSoftInput(view: View, flags: Int) {
        val imm =
            BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, flags, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN || resultCode == InputMethodManager.RESULT_HIDDEN) {
                    toggleSoftInput()
                }
            }
        })
    }

    /**
     * 隐藏软键盘
     *
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        hideSoftInput(view)
    }

    /**
     * 隐藏软键盘
     * Hide the soft input.
     *
     * @param view The view.
     */
    fun hideSoftInput(view: View) {
        val imm =
            BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN || resultCode == InputMethodManager.RESULT_SHOWN) {
                    toggleSoftInput()
                }
            }
        })
    }

    /**
     * 切换软键盘的显示
     * Toggle the soft input display or not.
     */
    fun toggleSoftInput() {
        val imm =
            BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * 返回软键盘是否显示
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSoftInputVisible(activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity) > 0
    }

    private var sDecorViewDelta = 0

    private fun getDecorViewInvisibleHeight(activity: Activity): Int {
        val decorView = activity.window.decorView ?: return sDecorViewInvisibleHeightPre
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        Log.d(
            "KeyboardUtils",
            "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom)
        )
        val delta = Math.abs(decorView.bottom - outRect.bottom)
        if (delta <= getNavBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    /**
     * 注册软件监听
     * Register soft input changed listener.
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        activity: Activity,
        listener: OnSoftInputChangedListener
    ) {
        val flags = activity.window.attributes.flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity)
        onSoftInputChangedListener = listener
        onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (onSoftInputChangedListener != null) {
                val height = getDecorViewInvisibleHeight(activity)
                if (sDecorViewInvisibleHeightPre != height) {
                    onSoftInputChangedListener!!.onSoftInputChanged(height)
                    sDecorViewInvisibleHeightPre = height
                }
            }
        }
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    /**
     * 注销键盘监听
     * Unregister soft input changed listener.
     *
     * @param activity The activity.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun unregisterSoftInputChangedListener(activity: Activity) {
        val contentView = activity.findViewById<View>(android.R.id.content)
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        onSoftInputChangedListener = null
        onGlobalLayoutListener = null
    }

    /**
     * 修正了Android系统中5497的错误
     *
     * Fix the bug of 5497 in Android.
     *
     * Don't set adjustResize
     *
     * @param activity The activity.
     */
    fun fixAndroidBug5497(activity: Activity) {
        //        Window window = activity.getWindow();
        //        int softInputMode = window.getAttributes().softInputMode;
        //        window.setSoftInputMode(softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
        val contentViewChild = contentView.getChildAt(0)
        val paddingBottom = contentViewChild.paddingBottom
        sContentViewInvisibleHeightPre5497 = getContentViewInvisibleHeight(activity)
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val height = getContentViewInvisibleHeight(activity)
                if (sContentViewInvisibleHeightPre5497 != height) {
                    contentViewChild.setPadding(
                        contentViewChild.paddingLeft,
                        contentViewChild.paddingTop,
                        contentViewChild.paddingRight,
                        paddingBottom + getDecorViewInvisibleHeight(activity)
                    )
                    sContentViewInvisibleHeightPre5497 = height
                }
            }
    }

    private fun getContentViewInvisibleHeight(activity: Activity): Int {
        val contentView = activity.findViewById<View>(android.R.id.content)
            ?: return sContentViewInvisibleHeightPre5497
        val outRect = Rect()
        contentView.getWindowVisibleDisplayFrame(outRect)
        Log.d(
            "KeyboardUtils",
            "getContentViewInvisibleHeight: " + (contentView.bottom - outRect.bottom)
        )
        val delta = Math.abs(contentView.bottom - outRect.bottom)
        return if (delta <= getStatusBarHeight() + getNavBarHeight()) {
            0
        } else delta
    }

    /**
     * Fix the leaks of soft input.
     *
     * Call the function in [Activity.onDestroy].
     *
     * @param activity The activity.
     */
    fun fixSoftInputLeaks(activity: Activity?) {
        if (activity == null) return
        val imm =
            BaseApp.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField =
                    InputMethodManager::class.java.getDeclaredField(leakView) ?: continue
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField.get(imm)
                if (obj !is View) continue
                if (obj.rootView === activity.window.decorView.rootView) {
                    leakViewField.set(imm, null)
                }
            } catch (ignore: Throwable) { /**/
            }

        }
    }

    /**
     * Click blank area to hide soft input.
     *
     * Copy the following code in ur activity.
     */
    fun clickBlankArea2HideSoftInput() {
        Log.i("KeyboardUtils", "Please refer to the following code.")
        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS
                    );
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // Return whether touch the view.
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        */
    }

    private fun getStatusBarHeight(): Int {
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    private fun getNavBarHeight(): Int {
        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }
}