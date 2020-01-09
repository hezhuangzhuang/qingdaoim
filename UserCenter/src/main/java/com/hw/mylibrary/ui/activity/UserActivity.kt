package com.hw.mylibrary.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.mylibrary.R

class UserActivity : BaseActivity() {
    override fun initData(bundle: Bundle?) {
        ARouter.getInstance().inject(this)
    }

    override fun bindLayout(): Int {
        return R.layout.activity_user
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
    }

    override fun onError(text: String) {
    }

}
