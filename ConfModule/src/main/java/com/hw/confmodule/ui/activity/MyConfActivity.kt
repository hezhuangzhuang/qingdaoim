package com.hw.confmodule.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.FillEventHistory
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.DynamicTimeFormat
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.confmodule.R
import com.hw.confmodule.data.bean.ConfBean
import com.hw.confmodule.data.bean.HistoryConfBean
import com.hw.confmodule.inject.component.DaggerConfComponent
import com.hw.confmodule.inject.module.ConfModule
import com.hw.confmodule.mvp.contract.MyConfContract
import com.hw.confmodule.mvp.presenter.MyConfPresenter
import com.hw.confmodule.ui.adapter.ConfListAdapter
import com.hw.confmodule.ui.adapter.HistoryListAdapter
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_my_conf.*

/**
 * 我的会议
 */
@Route(path = RouterPath.Conf.MY_CONF_LIST)
class MyConfActivity : BaseMvpActivity<MyConfPresenter>(), MyConfContract.View {

    private val errorView: View by lazy {
        View.inflate(mActivity, R.layout.error_view, null)
    }

    private val emptyView: View by lazy {
        View.inflate(mActivity, R.layout.empty_view, null)
    }

    //会议列表
    lateinit var confAdapter: ConfListAdapter

    //历史会议适配器
    lateinit var historyListAdapter: HistoryListAdapter

    //true:历史会议,false:正在进行的会议
    var isHistory: Boolean = false

    //请求的页数
    var pageNum = 0

    override fun initComponent() {
        DaggerConfComponent
            .builder()
            .activityComponent(mActivityComponent)
            .confModule(ConfModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this

        if (isHistory) {
            refreshLayout.autoRefresh()
        } else {
            mPresenter.getAllConfList(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))
        }
    }


    override fun initData(bundle: Bundle?) {
        isHistory = intent.getBooleanExtra(RouterPath.Conf.FILED_IS_HISTORY, false)
    }

    override fun bindLayout(): Int = R.layout.activity_my_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        initRefreshLayout()

        if (isHistory) {
            titleBar.rightTitle = ""
            initHistoryConfAdapter()
        } else {
            initMyConfAdapter()
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
                if (!isHistory) {
                    ARouter.getInstance().build(RouterPath.Conf.MY_CONF_LIST)
                        .withBoolean(RouterPath.Conf.FILED_IS_HISTORY, true)
                        .navigation()
                }
            }

            override fun onTitleClick(v: View?) {
            }
        })
    }

    /**
     * 初始化刷新控件
     */
    private fun initRefreshLayout() {
        //设置头部
        refreshLayout.setRefreshHeader(
            ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate)
                .setTimeFormat(DynamicTimeFormat("更新于 %s"))
                .setAccentColor(ContextCompat.getColor(this, R.color.color_999))
        )
        //设置尾部
        refreshLayout.setRefreshFooter(ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate))

        refreshLayout.setOnRefreshListener {
            mPresenter.getHistoryConfList(1)
        }

        refreshLayout.setOnLoadMoreListener {
            mPresenter.getHistoryConfList(1 + pageNum)
        }

        //是否启用刷新
        refreshLayout.setEnableRefresh(isHistory)
        refreshLayout.setEnableLoadMore(isHistory)
    }

    /**
     * 初始化会议适配器
     */
    private fun initMyConfAdapter() {
        confAdapter = ConfListAdapter(R.layout.item_my_conf, ArrayList<ConfBean.DataBean>())
        confAdapter.setOnItemChildClickListener { adapter, view, position ->
            ToastHelper.showShort("加入会议")
        }
        rvList.adapter = confAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 初始化历史会议适配器
     */
    private fun initHistoryConfAdapter() {
        historyListAdapter =
            HistoryListAdapter(R.layout.item_history_conf, ArrayList<HistoryConfBean.DataBean>())
        historyListAdapter.setOnItemClickListener { adapter, view, position ->
        }
        rvList.adapter = historyListAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    override fun doBusiness() {
    }

    override fun setListeners() {
    }

    override fun onError(text: String) {
    }

    override fun showConfList(confList: List<ConfBean.DataBean>) {
        confAdapter.replaceData(confList)
    }

    override fun queryConfListError(errorMsg: String) {
//        ToastHelper.showShort(errorMsg)
        if(errorMsg.contains("查询不到会议")){
            confAdapter.setEmptyView(emptyView)
        }
    }

    /**
     * 查询到的数据为空
     */
    override fun queryHistoryEmpty(isFirst: Boolean) {
        if (isFirst) {
            refreshLayout.finishRefresh(true)
            //显示空布局
            historyListAdapter.setEmptyView(emptyView)
        } else {
            //完成加载并标记没有更多数据
            refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    /**
     * 查询到数据
     */
    override fun showHistoryList(
        isFirst: Boolean,
        historyConfList: List<HistoryConfBean.DataBean>) {
        if (isFirst) {
            pageNum = 1
            //刷新加载更多状态
            refreshLayout.resetNoMoreData()
            refreshLayout.finishRefresh(true)
            historyListAdapter.replaceData(historyConfList)
        } else {
            pageNum += 1
            refreshLayout.finishLoadMore(true)
            historyListAdapter.addData(historyConfList)
        }
    }

    /**
     * 查询失败
     */
    override fun queryHistoryError(isFirst: Boolean, errorMsg: String) {
        if (isFirst) {
            refreshLayout.finishRefresh(false)
            //显示错误布局
            historyListAdapter.setEmptyView(errorView)
        } else {
            refreshLayout.finishLoadMore(false)
        }
    }


    override fun showLoading() {
        if (isHistory) {

        } else {
            super.showLoading()
        }
    }

    override fun dismissLoading() {
        if (isHistory) {

        } else {
            super.dismissLoading()
        }
    }
}
