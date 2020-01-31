package com.hw.confmodule.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.DateUtils
import com.hw.baselibrary.utils.KeyboardUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.R
import com.hw.confmodule.inject.component.DaggerConfComponent
import com.hw.confmodule.inject.module.ConfModule
import com.hw.confmodule.mvp.contract.CreateConfContract
import com.hw.confmodule.mvp.presenter.CreateConfPresenter
import com.hw.confmodule.ui.adapter.CreateConfAdapter
import com.hw.provider.conf.ConfContants
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_create_conf.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

/**
 * 创建会议界面
 */
@Route(path = RouterPath.Conf.CREATE_CONF)
class CreateConfActivity : BaseMvpActivity<CreateConfPresenter>(), CreateConfContract.View {

    /**
     * 添加人员到群组成功
     */
    override fun addPeopleToGroupChatSuccess() {
        EventBusUtils.sendMessage(EventMsg.ADD_PEOPLE_TO_GROUPCHAT, "")
        finish()
    }

    /**
     * 添加人员到群组失败
     */
    override fun addPeopleToGroupChatFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    //是否是视频会议, 0：语音会议，1：视频会议,
    var isVideoConf = ConfContants.VIDEO_CONF

    //是否是预约会议，0:即时会议，1：预约会议
    var isReservedConf = ConfContants.INSTANT_CONF

    //true:创建群聊，false:创建会议
    var isCreateGroup = false

    /**
     * 控制类型
     * const val CREATE_CONF = 0         //创建会议
     * const val CREATE_GROUP_CHAT = 1   //创建群组
     * const val GROUP_CHAT_ADD_PEOPLE =2           //添加人员
     */
    var controlType = ConfContants.CREATE_CONF

    //添加人员的群组id
    var groupId: String? = null

    //群组中已经存在的人员
    var existPeopls = ArrayList<PeopleBean>()

    lateinit var createConfAdapter: CreateConfAdapter

    //已选择的适配器
    val selectPeoples by lazy {
        ArrayList<PeopleBean>()
    }

    //所有联系人
    val allPeoples by lazy {
        ArrayList<PeopleBean>()
    }

    //是否全选
    var isAllCheck = false

    override fun initComponent() {
        DaggerConfComponent.builder()
            .activityComponent(mActivityComponent)
            .confModule(ConfModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this

        //查询所有人员
        mPresenter.queryAllPeople()
    }

    override fun onError(text: String) {
    }

    override fun initData(bundle: Bundle?) {
        isVideoConf = intent.getIntExtra(RouterPath.Conf.FILED_VIDEO_CONF, 1)

        //是否是创建群组
        isCreateGroup = intent.getBooleanExtra(RouterPath.Conf.FILED_IS_CREATE_GROUP, false)

        //界面的控制类型
        controlType =
            intent.getIntExtra(RouterPath.Conf.FILED_CONTROL_TYPE, ConfContants.CREATE_CONF)

        //群组id
        groupId = intent.getStringExtra(RouterPath.Conf.FILED_GROUP_ID)

        var tempPeople = intent.getSerializableExtra(RouterPath.Conf.FILED_EXIST_PEOPLS)
        if (null != tempPeople) {
            existPeopls = tempPeople as ArrayList<PeopleBean>
        }
    }

    override fun bindLayout(): Int = R.layout.activity_create_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        etConfName.hint =
            DateUtils.longToString(System.currentTimeMillis(), DateUtils.FORMAT_DATE1_TIME)

        createConfAdapter = CreateConfAdapter(R.layout.item_create_conf, ArrayList<PeopleBean>())
        createConfAdapter.setOnItemClickListener { adapter, view, position ->
            itemClick(position)
        }
        rvList.adapter = createConfAdapter
        rvList.layoutManager = LinearLayoutManager(this)

        when (controlType) {
            //创建会议
            ConfContants.CREATE_CONF -> {
                //显示会议类型
                clTypeContent.isVisible = true
                //显示会议接入码
                llConfCode.isVisible = true
                //显示会议类型
                llConfType.isVisible = true

                //设置会议类型
                setConfTypeStyle()
            }

            //创建群组
            ConfContants.CREATE_GROUP_CHAT -> {
                //隐藏会议类型
                clTypeContent.isVisible = false
                //隐藏会议接入码
                llConfCode.isVisible = false
                //隐藏会议类型
                llConfType.isVisible = false

                tvConfNameLable.text = "群组名称"
                etConfName.hint = "名称（最长10个字符）"
                tvTopTitle.text = "创建群组"
                titleBar.rightTitle = "创建群组"
            }

            //群组添加人员
            ConfContants.GROUP_CHAT_ADD_PEOPLE -> {
                //隐藏会议类型
                clTypeContent.isVisible = false
                //隐藏会议接入码
                llConfCode.isVisible = false
                //隐藏会议类型
                llConfType.isVisible = false

                //隐藏会议名称
                llConfName.isVisible = false

                tvTopTitle.text = "添加新成员"
                titleBar.rightTitle = "确认"
            }
        }
    }

    /**
     * item的点击事件
     */
    private fun itemClick(position: Int) {
        var peopleBean = createConfAdapter.getItem(position)
        //选中状态取反
        peopleBean?.isCheck = !peopleBean!!.isCheck

        if (peopleBean.isCheck) {
            selectPeoples.add(peopleBean)
        } else {
            selectPeoples.remove(peopleBean)
        }

        //设置全选的状态
        isAllCheck = selectPeoples.size == allPeoples.size

        //设置已选会场的数量
        setSelectNumber()

        createConfAdapter.notifyItemChanged(position)
    }

    /**
     * 设置全选按钮状态
     */
    private fun setAllCheckStatus() {
        tvAllSelect.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isAllCheck) R.mipmap.ic_blue_check_true else R.mipmap.ic_blue_check_false,
            0,
            0,
            0
        )
    }

    /**
     * 设置已选会场的数量
     */
    private fun setSelectNumber() {
        tvSelectNumber.isVisible = selectPeoples.size > 0

        tvSelectNumber.text = "已选择: ${selectPeoples.size}人"

        //设置全选的状态
        isAllCheck = selectPeoples.size == allPeoples.size

        setAllCheckStatus()
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
        swConfType.setOnCheckedChangeListener { view, isChecked ->
            tvContType.text = if (isChecked) "预约会议" else "即时会议"

            //是否是预约会议，0:即时会议，1：预约会议
            isReservedConf = if (isChecked) ConfContants.RESERVED_CONF else ConfContants.INSTANT_CONF

            //显示开始时间
            llStartTime.isVisible = isChecked
        }

        tvStartTime.setOnClickListener {
            showTimePicker()
        }

        tvVideoConf.setOnClickListener {
            isVideoConf = ConfContants.VIDEO_CONF

            setConfTypeStyle()
        }

        tvVoiceConf.setOnClickListener {
            isVideoConf = ConfContants.AUDIO_CONF

            setConfTypeStyle()
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                KeyboardUtils.hideSoftInput(etConfName)
                finish()
            }

            override fun onRightClick(v: View?) {
                when (controlType) {
                    //创建会议
                    ConfContants.CREATE_CONF -> {
                        createConf()
                    }

                    //创建群组
                    ConfContants.CREATE_GROUP_CHAT -> {
                        createGroupChat()
                    }

                    //群聊添加人员
                    ConfContants.GROUP_CHAT_ADD_PEOPLE -> {
                        mPresenter.addPeoplesToGroup(groupId!!,
                            selectPeoples.joinToString(separator = ",") { it.id })
                    }
                }
            }

            override fun onTitleClick(v: View?) {
            }
        })

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                content: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val filterList = allPeoples.filter {
                    it.name.contains(content.toString())
                }
                createConfAdapter.replaceData(filterList)
            }
        })

        tvAllSelect.setOnClickListener {
            //全选状态取反
            isAllCheck = !isAllCheck

            //同步人员的选中状态
            allPeoples.forEach {
                it.isCheck = isAllCheck
            }

            //清空已选列表
            selectPeoples.clear()
            //如果是全选则添加所有联系人
            if (isAllCheck) {
                selectPeoples.addAll(allPeoples)
            }

            //刷新适配器
            createConfAdapter.notifyDataSetChanged()

            //设置已选会场的数量
            setSelectNumber()
        }

        tvSelectNumber.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.Conf.SELECTED_PEOPLE)
                .withSerializable(RouterPath.Conf.FILED_SELECTED_PEOPLE, selectPeoples)
                .navigation(this, RouterPath.Conf.SELECTED_PEOPLE_REQUEST)
        }
    }


    //创建会议
    private fun createConf() {
        if (selectPeoples.size <= 0) {
            ToastHelper.showShort("参会人员不能为空")
            return
        }

        //如果是预约会议
        if (ConfContants.RESERVED_CONF == isReservedConf && startTime.isEmpty()) {
            ToastHelper.showShort("请选择会议开始时间")
            return
        }

        //会议名称
        var confName: String =
            if (etConfName.text.isEmpty()) etConfName.hint.toString() else etConfName.text.toString()

        //会议接入码
        var accessCode: String = if (etConfCode.text.isEmpty()) "" else etConfCode.text.toString()

        var selectUri =
            selectPeoples.joinToString { it.sip } + ", ${SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)}"

        //预约会议
        if (ConfContants.RESERVED_CONF == isReservedConf) {
            ToastHelper.showShort("开始预约会议")
            mPresenter.reservedConf(
                confName,
                "120",
                accessCode,
                selectUri,
                "1",
                isVideoConf,
                isReservedConf.toString(),
                startTime
            )
        } else {
            mPresenter.createConf(confName, "120", accessCode, selectUri, "1", isVideoConf)
        }
    }

    /**
     * 预约会议成功
     */
    override fun reservedConfSuccess() {

    }

    /**
     * 预约会议失败
     */
    override fun reservedConfFaile() {
    }

    //创建群聊
    private fun createGroupChat() {
        if (selectPeoples.size <= 0) {
            ToastHelper.showShort("群组人员不能为空")
            return
        }

        if (etConfName.text.isEmpty()) {
            ToastHelper.showShort("群组名称不能为空")
            return
        }
        //会议名称
        var groupChatName: String = etConfName.text.toString()

        var selectUri = "${SPStaticUtils.getInt(UserContants.USER_ID)},"

        selectPeoples.map {
            selectUri += it.id + ","
        }

        //添加已选会场
        mPresenter.createGroupChat(
            groupChatName,
            SPStaticUtils.getInt(UserContants.USER_ID).toString(),
            selectUri.toString()
        )
    }

    /**
     * 创建群聊成功
     */
    override fun createGroupChatSuccess() {
        EventBusUtils.sendMessage(EventMsg.UPDATE_GROUP_CHAT, Any())
        finish()
    }

    override fun createGroupChatError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    /**
     * 设置
     */
    private fun setConfTypeStyle() {
        //视频
        tvVideoConf.setTextColor(
            ContextCompat.getColor(
                mActivity,
                if (isVideoConf == ConfContants.VIDEO_CONF) R.color.color_418ad5 else R.color.color_999
            )
        )
        tvVideoConf.paint.isFakeBoldText = isVideoConf == ConfContants.VIDEO_CONF
        viewVideo.isVisible = isVideoConf == ConfContants.VIDEO_CONF

        //语音
        tvVoiceConf.setTextColor(
            ContextCompat.getColor(
                mActivity,
                if (isVideoConf == ConfContants.VIDEO_CONF) R.color.color_999 else R.color.color_418ad5
            )
        )
        tvVoiceConf.paint.isFakeBoldText = !(isVideoConf == ConfContants.VIDEO_CONF)
        viewVoice.isVisible = !(isVideoConf == ConfContants.VIDEO_CONF)
    }

    override fun queryPeopleSuccess(showPeoples: List<PeopleBean>) {
        allPeoples.clear()


        //如果是添加人员则进行人员筛选
        if (ConfContants.GROUP_CHAT_ADD_PEOPLE.equals(controlType)) {
            val filter = showPeoples.filter {
                !existPeopls.contains(it)
            }
            allPeoples.addAll(filter)
            createConfAdapter.replaceData(filter)
        } else {
            allPeoples.addAll(showPeoples)
            createConfAdapter.replaceData(showPeoples)
        }
    }

    override fun queryPeopleError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    override fun createConfSuccess() {

    }

    override fun createConfFaile() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RouterPath.Conf.SELECTED_PEOPLE_REQUEST -> {
                    selectPeoples.clear()
                    selectPeoples.addAll(data?.getSerializableExtra(RouterPath.Conf.FILED_SELECTED_PEOPLE) as ArrayList<PeopleBean>)

                    createConfAdapter.data.forEach {
                        //如果包含则代表在已选列表中
                        it.isCheck = selectPeoples.contains(it)
                    }
                    //设置已选成员数量
                    setSelectNumber()
                    //刷新列表
                    createConfAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private var startTime: String = ""

    private var pvCustomLunar: TimePickerView? = null
    private fun showTimePicker() {
        if (null == pvCustomLunar) {
            initLunarPicker()
        }
        pvCustomLunar?.show()
    }

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private fun initLunarPicker() {
        val selectedDate = Calendar.getInstance()//系统当前时间
        selectedDate.set(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedDate.get(Calendar.HOUR_OF_DAY),
            selectedDate.get(Calendar.MINUTE)
        )

        val startDate = Calendar.getInstance()
        startDate.set(1900, 1, 23)

        val endDate = Calendar.getInstance()
        endDate.set(2100, 1, 23)
        //时间选择器 ，自定义布局
        pvCustomLunar = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                //选中事件回调
                startTime = DateUtils.dateToString(date, DateUtils.FORMAT_DATE_TIME)
                tvStartTime.text = startTime
            })
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.pickerview_custom_lunar) { v ->
                val tvSubmit = v.findViewById<View>(R.id.tv_finish) as TextView
                val tvCancle = v.findViewById<View>(R.id.tv_cancel) as TextView
                tvSubmit.setOnClickListener {
                    pvCustomLunar!!.returnData()
                    pvCustomLunar!!.dismiss()
                }
                tvCancle.setOnClickListener { pvCustomLunar!!.dismiss() }
            }
            .setType(booleanArrayOf(true, true, true, true, true, false))
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(Color.RED)
            .build()
    }

}
