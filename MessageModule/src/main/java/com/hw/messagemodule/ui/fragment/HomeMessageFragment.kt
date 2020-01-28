package com.hw.messagemodule.ui.fragment


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hw.messagemodule.R
import com.hw.messagemodule.inject.component.DaggerMessageComponent
import com.hw.messagemodule.inject.module.MessageModule
import com.hw.messagemodule.mvp.contract.MessageContract
import com.hw.messagemodule.mvp.presenter.MessagePresenter
import com.hw.messagemodule.ui.adapter.HomeMessageAdapter
import com.hw.provider.chat.bean.ChatBeanLastMessage
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.widget.SelectCreateDialog
import kotlinx.android.synthetic.main.fragment_message.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.hw.baselibrary.ui.fragment.BaseMvpFragment as BaseMvpFragment1

//TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *  消息的fragment
 */
class HomeMessageFragment : BaseMvpFragment1<MessagePresenter>(), MessageContract.View {
    //自己初始化
    private lateinit var messageAdapter: HomeMessageAdapter

    private val selectCreateDialog: SelectCreateDialog by lazy {
        SelectCreateDialog(activity)
    }

    override fun injectComponent() {
        DaggerMessageComponent.builder()
            .activityComponent(mActivityComponent)
            .messageModule(MessageModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun initData(bundle: Bundle?) {
        EventBus.getDefault().register(this)
        initAdapter()
    }

    override fun bindLayout(): Int = R.layout.fragment_message

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        ImmersionBar.setTitleBar(mActivity, titleBar)
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }

            override fun onRightClick(v: View?) {
                selectCreateDialog.setBackground(null)
                selectCreateDialog.showPopupWindow(v)
            }
        })
    }

    override fun doLazyBusiness() {
        //获取最新的消息
        mPresenter.queryLastChatBeans()
    }

    override fun onError(text: String) {
    }

    override fun isStatusBarEnabled(): Boolean {
        return !super.isStatusBarEnabled()
    }

    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        messageAdapter =
            HomeMessageAdapter(R.layout.item_home_message, ArrayList<ChatBeanLastMessage>())
        messageAdapter.setOnItemClickListener { adapter, view, position ->
            //进入聊天界面
            startChatActivity(adapter, position)
        }
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = messageAdapter
    }

    /**
     * 跳转到聊天界面
     */
    private fun startChatActivity(
        adapter: BaseQuickAdapter<Any, BaseViewHolder>,
        position: Int) {
        val chatBeanLastMessage = adapter.getItem(position) as ChatBeanLastMessage
        //刷新消息阅读状态
        chatBeanLastMessage.isRead = true
        mPresenter.updateMessageReadStauts(chatBeanLastMessage)

        messageAdapter.notifyItemChanged(position)

        ARouter.getInstance()
            .build(RouterPath.Chat.CHAT)
            .withString(RouterPath.Chat.FILED_RECEIVE_ID, chatBeanLastMessage.conversationId)
            .withString(
                RouterPath.Chat.FILED_RECEIVE_NAME,
                chatBeanLastMessage.conversationUserName
            )
            .withBoolean(RouterPath.Chat.FILED_IS_GROUP, chatBeanLastMessage.isGroup)
            .navigation()

        //刷新首页的消息提醒
        showMainShowRead()
    }

    /**
     * 刷新界面
     */
    override fun refreshLastMessage(list: List<ChatBeanLastMessage>) {
        messageAdapter.replaceData(list)

        //刷新首页的消息提醒
        showMainShowRead()
    }

    /**
     * 判断
     */
    fun showMainShowRead() {
        val filter = messageAdapter.data.filter {
            !it.isRead
        }
        EventBusUtils.sendMessage(EventMsg.UPDATE_MAIN_NOTIF, filter.size > 0)
    }

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeMessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //刷新消息
            EventMsg.REFRESH_HOME_MESSAGE ->{

                //查询消息
                mPresenter.queryLastChatBeans()
            }

            //更新消息阅读状态
            EventMsg.UPDATE_MESSAGE_READ_STATUS -> {
                //获得传递过来的用户id
                val userId = messageEvent.messageData as String
                //遍历消息查看是否有未读消息
                messageAdapter.data.forEach { chatBean ->
                    if (chatBean.conversationId == userId) {
                        chatBean.isRead = true
                        return@forEach
                    }
                }
                messageAdapter.notifyDataSetChanged()

                //更新首页的未读状态
                showMainShowRead()
            }

            //更新群聊名称
            EventMsg.UPDATE_GROUP_CHAT ->{
                //查询消息
                mPresenter.queryLastChatBeans()
            }

        }
    }
}

