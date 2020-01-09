package com.hw.confmodule.ui.activity

import android.os.Bundle
import android.view.View
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.confmodule.R
import com.hw.confmodule.mvp.contract.ConfContract
import com.hw.confmodule.mvp.presenter.ConfPresenter

class CreateConfActivity : BaseMvpActivity<ConfPresenter>(), ConfContract.View {


    override fun onError(text: String) {
    }

    override fun initComponent() {
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.activity_create_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
    }
}
