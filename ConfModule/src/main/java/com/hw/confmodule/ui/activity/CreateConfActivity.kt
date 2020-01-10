package com.hw.confmodule.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.confmodule.R
import com.hw.confmodule.inject.component.DaggerConfComponent
import com.hw.confmodule.mvp.contract.ConfContract
import com.hw.confmodule.mvp.presenter.ConfPresenter
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.ModuleRouteService

/**
 * 创建会议界面
 */
@Route(path = RouterPath.Conf.CREATE_CONF)
class CreateConfActivity : BaseMvpActivity<ConfPresenter>(), ConfContract.View {


    override fun initComponent() {
        DaggerConfComponent.builder()
            .activityComponent(mActivityComponent)
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun onError(text: String) {
    }

    override fun initData(bundle: Bundle?) {
        ModuleRouteService.getAllPeople()
            .subscribe({ baseData ->
                ToastHelper.showShort("${baseData.data.size}")
            }, {
                ToastHelper.showShort(it.toString())
            })
    }

    override fun bindLayout(): Int = R.layout.activity_create_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
    }

    override fun createConfSuccess() {
    }

    override fun createConfFaile() {
    }
}
