package com.hw.mylibrary.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.VersionUtils
import com.hw.mylibrary.R
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.activity_about.*

@Route(path = RouterPath.UserCenter.PATH_ABOUT)
class AboutActivity : BaseActivity() {
    override fun initData(bundle: Bundle?) {

    }

    override fun bindLayout(): Int = R.layout.activity_about

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        tvVerCode.text = "当前版本：${VersionUtils.getVerName()}"
    }

    override fun doBusiness() {

    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(v: View?) {
                finish()
            }


            override fun onRightClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }
        })

    }

    override fun onError(text: String) {

    }

}
