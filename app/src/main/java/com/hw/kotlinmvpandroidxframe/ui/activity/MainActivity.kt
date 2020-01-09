package com.hw.kotlinmvpandroidxframe.ui.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.flyco.tablayout.bean.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.confmodule.ui.fragment.HomeConfFragment
import com.hw.contactsmodule.ui.fragment.HomeContactsFragment
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.ui.fragment.MineFragment
import com.hw.messagemodule.ui.fragment.HomeMessageFragment
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = RouterPath.Main.PATH_MAIN)
class MainActivity : BaseActivity() {
    override fun setListeners() {
    }

    override fun onError(text: String) {
    }

    private val mTitles = arrayOf(
        "消息",
        "会议",
        "通讯录",
        "我的"
    )

    private val mIconUnselectIds = intArrayOf(
        R.mipmap.icon_hone_one_false,
        R.mipmap.icon_hone_two_false,
        R.mipmap.icon_hone_three_false,
        R.mipmap.icon_hone_four_false
    )

    private val mIconSelectIds = intArrayOf(
        R.mipmap.icon_hone_one_true,
        R.mipmap.icon_hone_two_true,
        R.mipmap.icon_hone_three_true,
        R.mipmap.icon_hone_four_true
    )

    private val mTabEntities: ArrayList<CustomTabEntity> by lazy {
        ArrayList<CustomTabEntity>()
    }

    private val fragments: ArrayList<Fragment> by lazy {
        ArrayList<Fragment>()
    }

    private var currentTab = 0

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        for (index in mTitles.indices) {
            mTabEntities.add(
                TabEntity(
                    mTitles[index],
                    mIconSelectIds[index],
                    mIconUnselectIds[index]
                )
            )
        }

        fragments.add(HomeMessageFragment.newInstance("",""))
        fragments.add(HomeConfFragment.newInstance("",""))
        fragments.add(HomeContactsFragment.newInstance("",""))
        fragments.add(MineFragment())

        tabLayout.setTabData(mTabEntities, this, R.id.flChange, fragments)
        tabLayout.currentTab = currentTab
    }

    override fun doBusiness() {
    }
}
