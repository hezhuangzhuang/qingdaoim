package com.hw.mylibrary.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.mylibrary.R
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_set_url.*

/**
 * 设置url
 */
@Route(path = RouterPath.UserCenter.PATH_SET_URL)
class SetUrlActivity : BaseActivity() {
    override fun initData(bundle: Bundle?) {

    }

    override fun bindLayout(): Int = R.layout.activity_set_url

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        //是否是正式环境
        var isFormalUrl = SPStaticUtils.getBoolean(UserContants.FORMAL_URL, true)

        swType.isChecked = isFormalUrl

        setUrl(isFormalUrl)
    }

    override fun doBusiness() {

    }

    override fun setListeners() {
        swType.setOnCheckedChangeListener { view, isChecked ->
            //正式环境
            setUrl(isChecked)
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
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

    /**
     * 设置url，
     */
    private fun setUrl(isFormalUrl: Boolean) {
        if (isFormalUrl) {
            tvType.text = "正式环境"

            etFilePath.setText(Urls.FILE_FORMAL)
            etImPath.setText(Urls.WEBSOCKET_FORMAL)

            SPStaticUtils.put(UserContants.FORMAL_URL, true)
        } else {//测试环境
            tvType.text = "测试环境"

            etFilePath.setText(Urls.FILE_TEST)
            etImPath.setText(Urls.WEBSOCKET_TEST)

            SPStaticUtils.put(UserContants.FORMAL_URL, false)
        }
    }

    override fun onError(text: String) {

    }
}
