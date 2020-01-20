package com.hw.kotlinmvpandroidxframe.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.flyco.tablayout.bean.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.hw.baselibrary.net.networkmonitor.NetType
import com.hw.baselibrary.net.networkmonitor.Network
import com.hw.baselibrary.net.networkmonitor.NetworkManager
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.ui.fragment.HomeConfFragment
import com.hw.contactsmodule.ui.fragment.HomeContactsFragment
import com.hw.kotlinmvpandroidxframe.R
import com.hw.messagemodule.service.KotlinMessageSocketService
import com.hw.messagemodule.ui.fragment.HomeMessageFragment
import com.hw.mylibrary.ui.fragment.MineFragment
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPath.Main.PATH_MAIN)
class MainActivity : BaseActivity() {
    //
    var TAG = MainActivity::javaClass.name

    var test:String? = null

    override fun setListeners() {
        ToastHelper.showShort(test?:"这是test为空时显示的文字")
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
        //初始化网络监听
        NetworkManager.init()
        NetworkManager.registerObserver(this)
        KotlinMessageSocketService.startService(this)
    }

    @Network(netType = NetType.AUTO)
    fun onNetChanged(netType: NetType) {
        when (netType) {
            NetType.WIFI -> {
                LogUtils.e(TAG, "AUTO监控：WIFI CONNECT")
            }

            NetType.MOBILE -> {
                LogUtils.e(TAG, "AUTO监控：MOBILE CONNECT")
            }

            NetType.AUTO -> {
                LogUtils.e(TAG, "AUTO监控：AUTO CONNECT")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "AUTO监控：NONE CONNECT")
            }

            else -> {
            }
        }
    }

    @Network(netType = NetType.WIFI)
    fun onWifiChanged(netType: NetType) {
        when (netType) {
            NetType.WIFI -> {
                LogUtils.e(TAG, "wifi监控：WIFI CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_CONNECT,"")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "wifi监控：NONE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_DISCONNECT,"")
            }
        }
    }

    @Network(netType = NetType.MOBILE)
    fun onMobileChanged(netType: NetType) {
        when (netType) {
            NetType.MOBILE -> {
                LogUtils.e(TAG, "Mobile监控：MOBILE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_CONNECT,"")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "Mobile监控：NONE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_DISCONNECT,"")
            }
        }
    }

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        //初始化数据库
//        MessageModuleRouteService.initDb()
        GreenDaoUtil.initDataBase(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))

        EventBus.getDefault().register(this)

        initTab()
    }

    private fun initTab() {
        for (index in mTitles.indices) {
            mTabEntities.add(
                TabEntity(
                    mTitles[index],
                    mIconSelectIds[index],
                    mIconUnselectIds[index]
                )
            )
        }

        fragments.add(HomeMessageFragment.newInstance("", ""))
        fragments.add(HomeConfFragment.newInstance("", ""))
        fragments.add(HomeContactsFragment.newInstance("", ""))
        fragments.add(MineFragment.newInstance("1", "2"))

        tabLayout.setTabData(mTabEntities, this, R.id.flChange, fragments)
        tabLayout.currentTab = currentTab
    }

    override fun doBusiness() {
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //更新消息提醒
            EventMsg.UPDATE_MAIN_NOTIF -> {
                if (messageEvent.messageData as Boolean) {
                    tabLayout.showDot(0)
                } else {
                    tabLayout.hideMsg(0)
                }
            }
        }
    }

    /**
     * 保存数据状态
     *
     * @param outState
     */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
    }
}
