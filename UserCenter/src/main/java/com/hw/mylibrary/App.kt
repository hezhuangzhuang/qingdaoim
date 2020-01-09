package com.hw.mylibrary

import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.image.ImageLoader

/**
 *author：Thinkpad
 *data:2019/12/7 19:47
 */
class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()

        ImageLoader.init(this)
    }

}