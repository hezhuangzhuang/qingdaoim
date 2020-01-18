package com.hw.confmodule.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.DateUtils
import com.hw.baselibrary.utils.KeyboardUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.R
import com.hw.confmodule.inject.component.DaggerConfComponent
import com.hw.confmodule.inject.module.ConfModule
import com.hw.confmodule.mvp.contract.ConfContract
import com.hw.confmodule.mvp.presenter.ConfPresenter
import com.hw.confmodule.ui.adapter.CreateConfAdapter
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_create_conf.*

/**
 * 创建会议界面
 */
@Route(path = RouterPath.Conf.CREATE_CONF)
class CreateConfActivity : BaseMvpActivity<ConfPresenter>(), ConfContract.View {
    //是否是视频会议, 0：语音会议，1：视频会议
    var isVideoConf = 1

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

        setAllCheckStatus()

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
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
        tvVideoConf.setOnClickListener {
            isVideoConf = 1

            setConfTypeStyle()
        }

        tvVoiceConf.setOnClickListener {
            isVideoConf = 0

            setConfTypeStyle()
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                KeyboardUtils.hideSoftInput(etConfName)
                finish()
            }

            override fun onRightClick(v: View?) {
                if (selectPeoples.size <= 0) {
                    ToastHelper.showShort("参会人员不能为空")
                    return
                }
                //会议名称
                var confName: String =
                    if (etConfName.text.isEmpty()) etConfName.hint.toString() else etConfName.text.toString()

                //会议接入码
                var accessCode: String = if (etConfCode.text.isEmpty()) "" else etConfCode.text.toString()

                var selectUri = ""

                selectPeoples.map {
                    selectUri += it.sip + ","
                }

                //添加已选会场
                selectUri += SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT)

                mPresenter.createConf(confName, "120", accessCode, selectUri, "1", isVideoConf)
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

            //设置全选按钮状态
            setAllCheckStatus()

            //设置已选会场的数量
            setSelectNumber()
        }
    }

    /**
     * 设置
     */
    private fun setConfTypeStyle() {
        //视频
        tvVideoConf.setTextColor(
            ContextCompat.getColor(
                mActivity,
                if (isVideoConf == 1) R.color.color_418ad5 else R.color.color_999
            )
        )
        tvVideoConf.paint.isFakeBoldText = isVideoConf == 1
        viewVideo.isVisible = isVideoConf == 1

        //语音
        tvVoiceConf.setTextColor(
            ContextCompat.getColor(
                mActivity,
                if (isVideoConf == 1) R.color.color_999 else R.color.color_418ad5
            )
        )
        tvVoiceConf.paint.isFakeBoldText = !(isVideoConf == 1)
        viewVoice.isVisible = !(isVideoConf == 1)
    }

    override fun queryPeopleSuccess(showPeoples: List<PeopleBean>) {
        allPeoples.clear()
        allPeoples.addAll(showPeoples)
        createConfAdapter.replaceData(showPeoples)
    }

    override fun queryPeopleError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    override fun createConfSuccess() {
    }

    override fun createConfFaile() {
    }
}
