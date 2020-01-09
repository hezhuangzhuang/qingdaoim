package com.hw.baselibrary.utils

import android.content.res.Resources

/**
 *author：pc-20171125
 *data:2019/11/7 16:08
 */
object DisplayUtil {
    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dp2px(dipValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun getScreenWidth(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.widthPixels
    }

    fun getScreenHeight(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.heightPixels
    }
}