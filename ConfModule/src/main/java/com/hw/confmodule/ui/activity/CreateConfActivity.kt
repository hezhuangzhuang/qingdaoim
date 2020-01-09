package com.hw.confmodule.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.confmodule.R
import com.hw.confmodule.inject.component.DaggerConfComponent
import com.hw.confmodule.inject.module.ConfModule
import com.hw.confmodule.mvp.contract.ConfContract
import com.hw.confmodule.mvp.presenter.ConfPresenter
import com.hw.confmodule.router.service.ContactsModuleService
import com.hw.provider.router.RouterPath



@Route(path = RouterPath.Conf.CREATE_CONF)
class CreateConfActivity : BaseMvpActivity<ConfPresenter>(), ConfContract.View {

    @Autowired
    lateinit var contactsModuleService: ContactsModuleService

    override fun initComponent() {
        DaggerConfComponent.builder()
            .activityComponent(mActivityComponent)
            .confModule(ConfModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun onError(text: String) {
    }

    override fun initData(bundle: Bundle?) {
        ToastHelper.showShort("")
        contactsModuleService?.getAllPeople();
    }

    override fun bindLayout(): Int = R.layout.activity_create_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
    }
}
