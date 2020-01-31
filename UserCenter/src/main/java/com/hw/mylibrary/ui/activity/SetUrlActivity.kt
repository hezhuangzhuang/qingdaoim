package com.hw.mylibrary.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.mylibrary.R
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.activity_set_url.*

/**
 * 设置url
 */
@Route(path = RouterPath.UserCenter.PATH_SET_URL)
class SetUrlActivity : BaseActivity() {
    //是否是测试环境
    var isTest = false

    override fun initData(bundle: Bundle?) {

    }

    override fun bindLayout(): Int = R.layout.activity_set_url

    override fun initView(savedInstanceState: Bundle?, contentView: View) {

    }

    override fun doBusiness() {

    }

    override fun setListeners() {
        swType.setOnCheckedChangeListener { view, isChecked ->
            //正式环境
            if (isChecked) {
                tvType.text = "正式环境"

                etFilePath.setText(Urls.FILE_FORMAL)
                etImPath.setText(Urls.WEBSOCKET_FORMAL)
            } else {//测试环境
                tvType.text = "测试环境"

                etFilePath.setText(Urls.FILE_TEST)
                etImPath.setText(Urls.WEBSOCKET_TEST)
            }
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {

            }

            override fun onRightClick(v: View?) {
                Urls.FILE_URL = etFilePath.text.toString()
                Urls.WEBSOCKET_URL = etImPath.text.toString()
                finish()
            }

            override fun onTitleClick(v: View?) {
            }
        })
    }

    override fun onError(text: String) {

    }
}
