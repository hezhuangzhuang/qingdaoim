package com.hw.messagemodule.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.KeyboardUtils
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.NotificationUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatItem
import com.hw.messagemodule.inject.component.DaggerChatComponent
import com.hw.messagemodule.inject.module.ChatModule
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.presenter.ChatPresenter
import com.hw.messagemodule.ui.adapter.ChatAdapter
import com.hw.messagemodule.utils.GlideEngine
import com.hw.messagemodule.utils.MediaConstant
import com.hw.provider.chat.ChatMultipleItem
import com.hw.provider.chat.bean.ChatBean
import com.hw.provider.chat.bean.LocalFileBean
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.bean.MessageReal
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.chat.utils.MessageUtils
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.lqr.audio.AudioPlayManager
import com.lqr.audio.AudioRecordManager
import com.lqr.audio.IAudioPlayListener
import com.lqr.audio.IAudioRecordListener
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_bottom_send_layout.*
import kotlinx.android.synthetic.main.include_chat_more_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * 聊天界面c
 */
@Route(path = RouterPath.Chat.CHAT)
class ChatActivity : BaseMvpActivity<ChatPresenter>(), ChatContract.View, View.OnClickListener {
    //收件人id
    public lateinit var receiveId: String

    //收件人名称
    public lateinit var receiveName: String

    //是否是群聊
    public var isGroup: Boolean = false

    //当前登录的账号
    val account = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT, "")

    //当前登录账户的名称
    val disPlayName = SPStaticUtils.getString(UserContants.DISPLAY_NAME, "")

    //聊天适配器
    lateinit var chatAdapter: ChatAdapter

    override fun initComponent() {
        DaggerChatComponent.builder()
            .activityComponent(mActivityComponent)
            .chatModule(ChatModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun initData(bundle: Bundle?) {
        EventBus.getDefault().register(this)
        //初始化下载控件
        FileDownloader.setup(this)

        receiveId = intent.getStringExtra(RouterPath.Chat.FILED_RECEIVE_ID)
        receiveName = intent.getStringExtra(RouterPath.Chat.FILED_RECEIVE_NAME)
        isGroup = intent.getBooleanExtra(RouterPath.Chat.FILED_IS_GROUP, false)
    }

    override fun bindLayout() = R.layout.activity_chat

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        tvName.text = receiveName

        getStatusBarConfig()
            //解决软键盘与底部输入框冲突问题
            .keyboardEnable(true)
            .init()

        chatAdapter = ChatAdapter(0, ArrayList<ChatItem>())
        chatAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as ChatItem;
            when (view.id) {
                R.id.iv_voice -> {
                    when (item.chatBean.messageType) {
                        //图片
                        ChatMultipleItem.SEND_IMG, ChatMultipleItem.FORM_IMG -> {
                            //查看图片
                            previewPhoto(item.chatBean.textContent)
                        }
                    }
                }

                R.id.fl_voice -> {
                    when (item.chatBean.messageType) {
                        //发出的语音
                        ChatMultipleItem.SEND_VOICE -> {
                            val localFileBean =
                                GreenDaoUtil.queryLocalFileByRemotePath(item.chatBean.textContent)
                            //判断文件是否存在
                            if (null != localFileBean && File(localFileBean.localPath).exists()) {
                                //播放文件
                                playAudio(view, localFileBean.localPath)
                            } else {
                                //下载文件
                                downloaderVoiceFile(view, item.chatBean.textContent, true)
                            }
                        }
                        //收到的语音
                        ChatMultipleItem.FORM_VOICE -> {
                            val localFileBean =
                                GreenDaoUtil.queryLocalFileByRemotePath(item.chatBean.textContent)
                            //判断文件是否存在
                            if (null != localFileBean && File(localFileBean.localPath).exists()) {
                                //播放文件
                                playAudio(view, localFileBean.localPath)
                            } else {
                                //下载文件
                                downloaderVoiceFile(view, item.chatBean.textContent, true)
                            }
                        }
                    }
                }
            }
        }

        if (isGroup) {
            titleBar.setRightIcon(R.mipmap.ic_group_details)
        }

        rvMsg.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMsg.adapter = chatAdapter

        //初始化语音控件
        initAudioRecordManager()

        //清楚notif
        NotificationUtils.cancel(receiveId.toInt())
    }

    /**
     * 下载语音文件并播放
     */
    private fun downloaderVoiceFile(view: View?, remotePath: String, isPlay: Boolean) {
        //获取文件名称
        val voiceName = remotePath.substring(remotePath.lastIndexOf("/"))

        var localFilePath = MediaConstant.AUDIO_SAVE_DIR + voiceName
        FileDownloader.getImpl()
            .create(remotePath)
            .setPath(localFilePath)
            .setForceReDownload(true)
            .setListener(object : FileDownloadListener() {
                //等待
                override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                //下载进度回调
                override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                //完成下载
                override fun completed(task: BaseDownloadTask) {
                    //保存本地消息到本地
                    GreenDaoUtil.insertLocalFileBean(LocalFileBean(remotePath, localFilePath))
                    //播放语音
                    if (isPlay) {
                        playAudio(null, localFilePath)
                    }
                }

                //暂停
                override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {

                }

                //下载出错
                override fun error(task: BaseDownloadTask, e: Throwable) {
                    ToastHelper.showShort("语音下载出错")
                    Toast.makeText(this@ChatActivity, "下载出错", Toast.LENGTH_SHORT).show()
                }

                //已存在相同下载
                override fun warn(task: BaseDownloadTask) {}
            }).start()
    }

    /**
     * 下载语音文件
     */
    private fun downloaderVoiceFile(view: View?, remotePath: String) {
        downloaderVoiceFile(view, remotePath, false)
    }

    /**
     * 播放语音
     *
     * @param view
     * @param position
     */
    private fun playAudio(view: View?, fileLocalPath: String) {
        AudioPlayManager.getInstance().stopPlay()

        //创建文件
        val item = File(fileLocalPath)

        val audioUri = Uri.fromFile(item)
        Log.e("LQR", audioUri.toString())

        AudioPlayManager.getInstance()
            .startPlay(this@ChatActivity, audioUri, object : IAudioPlayListener {
                override fun onStart(var1: Uri) {
                    //if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                    //    AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                    //    animation.start();
                    //}
                }

                override fun onStop(var1: Uri) {
                    //if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                    //    AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                    //    animation.stop();
                    //    animation.selectDrawable(0);
                    //}
                }

                override fun onComplete(var1: Uri) {
                    //if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                    //    AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                    //    animation.stop();
                    //    animation.selectDrawable(0);
                    //}
                }
            })
    }

    /**
     * 查看图片
     */
    private fun previewPhoto(imgUrl: String?) {
        var localMedia = LocalMedia()
        localMedia.setPath(imgUrl)

        var selectListImage = ArrayList<LocalMedia>()
        selectListImage.add(localMedia)

        PictureSelector.create(this)
            .themeStyle(R.style.picture_default_style)
            .isNotPreviewDownload(true)
            .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
            .openExternalPreview(0, selectListImage)
    }

    override fun doBusiness() {
        //通过id查询聊天记录
        val queryByIdChatBeans = GreenDaoUtil.queryByIdChatBeans(receiveId);
        val chatBeanmap = queryByIdChatBeans.map {
            ChatItem(it)
        }
        if (chatBeanmap.size > 6) {
            val layoutManager = rvMsg.layoutManager as LinearLayoutManager;
            layoutManager.stackFromEnd = true
        }
        chatAdapter.replaceData(chatBeanmap)
    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                KeyboardUtils.hideSoftInput(etContent)
                finish()
            }

            override fun onRightClick(v: View?) {
                if (isGroup) {
                    ARouter.getInstance().build(RouterPath.Contacts.GROUP_CHAT_DETAILS)
                        .withString(RouterPath.Contacts.FILED_RECEIVE_ID, receiveId)
                        .withString(RouterPath.Contacts.FILED_RECEIVE_NAME, receiveName)
                        .navigation()
                }
            }

            override fun onTitleClick(v: View?) {

            }
        })

        etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(content: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (content!!.length > 0) {
                    //隐藏更多按钮
                    ivMore.visibility = View.GONE
                    //显示发送按钮
                    btSendMsg.visibility = View.VISIBLE
                } else {
                    //显示更多按钮
                    ivMore.visibility = View.VISIBLE
                    //隐藏发送按钮
                    btSendMsg.visibility = View.GONE
                }
            }
        })
        ivAudioSwitch.setOnClickListener(this)
        etContent.setOnClickListener(this)
        btSendMsg.setOnClickListener(this)
        ivMore.setOnClickListener(this)

        //相册
        tvPhotoAlbum.setOnClickListener(this)
        //拍照
        tvPhotoGraph.setOnClickListener(this)
        //视频会议
        tvVideoCall.setOnClickListener(this)
        //语音会议
        tvAudioCall.setOnClickListener(this)
        //文件
        tvFile.setOnClickListener(this)

        //在触摸列表时候隐藏软键盘
        rvMsg.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isTouchRecycler = true
                    KeyboardUtils.hideSoftInput(etContent)
                    flEmotionView.visibility = View.GONE
                }
            }
            false
        }

        //语音按钮
        btAudio.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btAudio.text = "松开结束"
                    LogUtils.i("语音按钮-->ACTION_DOWN")
                    AudioRecordManager.getInstance(this@ChatActivity).startRecord()
                }

                MotionEvent.ACTION_MOVE -> {
                    LogUtils.i("语音按钮-->ACTION_MOVE")
                    if (isAudioCancelled(v, event)) {
                        AudioRecordManager.getInstance(this@ChatActivity).willCancelRecord()
                    } else {
                        AudioRecordManager.getInstance(this@ChatActivity).continueRecord()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    btAudio.text = "按住说话"
                    LogUtils.i("语音按钮-->ACTION_UP")
                    AudioRecordManager.getInstance(this@ChatActivity).stopRecord()
                    AudioRecordManager.getInstance(this@ChatActivity).destroyRecord()
                }
                MotionEvent.ACTION_CANCEL -> {
                    LogUtils.i("语音按钮-->ACTION_CANCEL")
                    AudioRecordManager.getInstance(this@ChatActivity).stopRecord()
                    AudioRecordManager.getInstance(this@ChatActivity).destroyRecord()
                }
            }
            false
        }
    }

    private var audioDir: File? = null

    /**
     * 初始化音频管理
     */
    private fun initAudioRecordManager() {
        //设置最长录音时间
        AudioRecordManager.getInstance(this).maxVoiceDuration =
            MediaConstant.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND
        audioDir = File(MediaConstant.AUDIO_SAVE_DIR)
        if (!audioDir!!.exists()) {
            audioDir!!.mkdirs()
        }
        AudioRecordManager.getInstance(this).setAudioSavePath(audioDir!!.absolutePath)

        //设置语言监听
        AudioRecordManager.getInstance(this).audioRecordListener = object : IAudioRecordListener {
            private var mTimerTV: TextView? = null
            private var mStateTV: TextView? = null
            private var mStateIV: ImageView? = null
            private var mRecordWindow: PopupWindow? = null

            override fun initTipView() {
                Log.i(getString(R.string.Record_TAG), "initTipView")

                val view = View.inflate(this@ChatActivity, R.layout.popup_audio_wi_vo, null)
                mStateIV = view.findViewById<View>(R.id.rc_audio_state_image) as ImageView
                mStateTV = view.findViewById<View>(R.id.rc_audio_state_text) as TextView
                mTimerTV = view.findViewById<View>(R.id.rc_audio_timer) as TextView
                mRecordWindow = PopupWindow(view, -1, -1)
                mRecordWindow!!.showAtLocation(llRoot, 17, 0, 0)
                mRecordWindow!!.isFocusable = true
                mRecordWindow!!.isOutsideTouchable = false
                mRecordWindow!!.isTouchable = false
            }

            override fun setTimeoutTipView(counter: Int) {
                Log.i(getString(R.string.Record_TAG), "setTimeoutTipView")

                if (null != this.mRecordWindow) {
                    this.mStateIV!!.visibility = View.GONE
                    this.mStateTV!!.visibility = View.VISIBLE
                    this.mStateTV!!.setText(R.string.voice_rec)
                    this.mStateTV!!.setBackgroundResource(R.drawable.bg_voice_popup)
                    this.mTimerTV!!.text =
                        String.format("%s", *arrayOf<Any>(Integer.valueOf(counter)))
                    this.mTimerTV!!.visibility = View.VISIBLE
                }
            }

            override fun setRecordingTipView() {
                Log.i(getString(R.string.Record_TAG), "setRecordingTipView")
                if (this.mRecordWindow != null) {
                    this.mStateIV!!.visibility = View.VISIBLE
                    this.mStateIV!!.setImageResource(R.mipmap.ic_volume_1)
                    this.mStateTV!!.visibility = View.VISIBLE
                    this.mStateTV!!.setText(R.string.voice_rec)
                    this.mStateTV!!.setBackgroundResource(R.drawable.bg_voice_popup)
                    this.mTimerTV!!.visibility = View.GONE
                }
            }

            override fun setAudioShortTipView() {
                Log.i(getString(R.string.Record_TAG), "setAudioShortTipView")
                if (this.mRecordWindow != null) {
                    mStateIV!!.setImageResource(R.mipmap.ic_volume_wraning)
                    mStateTV!!.setText(R.string.voice_short)
                }
            }

            override fun setCancelTipView() {
                Log.i(getString(R.string.Record_TAG), "setCancelTipView")
                if (this.mRecordWindow != null) {
                    this.mTimerTV!!.visibility = View.GONE
                    this.mStateIV!!.visibility = View.VISIBLE
                    this.mStateIV!!.setImageResource(R.mipmap.ic_volume_cancel)
                    this.mStateTV!!.visibility = View.VISIBLE
                    this.mStateTV!!.setText(R.string.voice_cancel)
                    this.mStateTV!!.setBackgroundResource(R.drawable.shape_corner_voice)

                    btAudio.text = "松开取消"
                }
            }

            override fun destroyTipView() {
                try {
                    Log.i(getString(R.string.Record_TAG), "destroyTipView")
                    if (this.mRecordWindow != null) {
                        this.mRecordWindow!!.dismiss()
                        this.mRecordWindow = null
                        this.mStateIV = null
                        this.mStateTV = null
                        this.mTimerTV = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            //开始录音
            override fun onStartRecord() {
                Log.i(getString(R.string.Record_TAG), "onStartRecord")
            }

            //录音完成
            override fun onFinish(audioPath: Uri, duration: Int) {
                Log.i(getString(R.string.Record_TAG), "finish")
                //发送文件
                val newFile = File(audioPath.path!!)

                if (newFile.exists()) {
                    //上传语音
                    mPresenter.uploadVoice(newFile, duration)
                } else {
                    ToastHelper.showShort("发送语音失败")
                }
            }

            override fun onAudioDBChanged(db: Int) {
                Log.i("Record", "onAudioDBChanged")

                when (db / 5) {
                    0 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_1)
                    1 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_2)
                    2 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_3)
                    3 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_4)
                    4 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_5)
                    5 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_6)
                    6 -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_7)
                    else -> this.mStateIV!!.setImageResource(R.mipmap.ic_volume_8)
                }
            }
        }
    }

    /**
     * 语音按钮是否取消
     */
    private fun isAudioCancelled(view: View, event: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

//        return if (event.rawX < location[0] ||
//            event.rawX > location[0] + view.width ||
//            event.rawY < location[1] - 40) {
//            true
//        } else false
        return (event.rawX < location[0] ||
                event.rawX > location[0] + view.width ||
                event.rawY < location[1] - 40)
    }

    //是否是触摸列表
    var isTouchRecycler = false

    /**
     * 获取发送的消息
     */
    private fun getMessageBody(
        message: String,//消息内容
        imgUrl: String,//图片路径
        messageType: Int//消息类型
    ): MessageBody {
        val messageReal = MessageReal(message, messageType, imgUrl)

        val msg = MessageBody(
            account,
            disPlayName,
            receiveId,
            receiveName,
            if (isGroup) {
                MessageBody.TYPE_COMMON
            } else MessageBody.TYPE_PERSONAL,
            messageReal
        )
        return msg
    }

    override fun onError(text: String) {
    }

    override fun onDestroy() {
        //更新当前聊天人的阅读状态
        EventBusUtils.sendMessage(EventMsg.UPDATE_MESSAGE_READ_STATUS, receiveId!!)
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /**
     * 第一次加载消息
     */
    override fun firstLoadMessage(chatBean: Array<ChatBean>) {
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            //发送消息
            R.id.btSendMsg ->
                mPresenter.sendMessage(
                    getMessageBody(
                        etContent.text.toString(),
                        "",
                        MessageReal.TYPE_STR
                    )
                )

            //更多的按钮
            R.id.ivMore ->
                switchShowMore()

            //点击输入框隐藏键盘
            R.id.etContent ->
                flEmotionView.visibility = View.GONE

            //相册
            R.id.tvPhotoAlbum ->
                startPictureSelectAct()

            //拍照
            R.id.tvPhotoGraph ->
                startCameraAct()

            //视频会议
            R.id.tvVideoCall -> {
                if (isGroup) {

                } else {
                    HuaweiModuleService.callSite(receiveId, true)
                }
            }

            //语音会议
            R.id.tvAudioCall ->
                if (isGroup) {

                } else {
                    HuaweiModuleService.callSite(receiveId, false)
                }

            //文件
            R.id.tvFile ->
                queryChatBeans()

            //语音和文字切换
            R.id.ivAudioSwitch -> {
                //输入框显示
                if (etContent.isVisible) {
                    btAudio.text = "按住说话"

                    //显示语音按钮
                    btAudio.isVisible = true

                    //隐藏输入框
                    etContent.isVisible = false

                    //隐藏更多
                    flEmotionView.isVisible = false

                    //隐藏输入框
                    KeyboardUtils.hideSoftInput(etContent)
                } else {
                    //隐藏语音框
                    btAudio.isVisible = false

                    //显示输入框
                    etContent.isVisible = true
                    //输入框获取焦点
                    etContent.requestFocus()

                    //显示输入框
                    KeyboardUtils.showSoftInput(etContent)
                }
            }
        }
    }

    private fun queryChatBeans() {
        val queryByIdChatBeans = GreenDaoUtil.queryByIdChatBeans(receiveId)
        val queryLastChatBeans = GreenDaoUtil.queryLastChatBeans()

        queryLastChatBeans.forEach {
            LogUtils.i("这是最后的消息数据-->" + it.toString())
        }
    }

    /**messageType
     * 切换更多界面的显示
     */
    private fun switchShowMore() {
        //如果更多布局显示
        if (flEmotionView.isVisible) {
            //隐藏更多布局
            flEmotionView.visibility = View.GONE
            //输入框获取焦点
            etContent.requestFocus()
        } else {
            KeyboardUtils.hideSoftInput(etContent)
            //显示更多布局
            flEmotionView.visibility = View.VISIBLE

            etContent.isVisible = true
            btAudio.isVisible = false

            //输入框隐藏焦点
            etContent.clearFocus()
        }
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //刷新收到的消息
            EventMsg.RECEIVE_SINGLE_MESSAGE -> {
                refreshReceiveMessage(messageEvent.messageData as MessageBody)
            }

            //刷新发出的消息
            EventMsg.SEND_SINGLE_MESSAGE -> {
                refreshSendMessage(messageEvent.messageData as MessageBody)
            }

            //更新群聊名称
            EventMsg.UPDATE_GROUP_CHAT -> {
                var newData = messageEvent.messageData as String
                val split = newData.split(",")

                if (receiveId.equals(split[0])) {
                    if (EventMsg.DELETE_GROUP_CHAT.equals(split[1])) {
                        finish()
                    } else {
                        receiveName = split[1]

                        tvName.setText(receiveName)
                    }
                }
            }
        }
    }

    /**
     * 刷新收到的消息
     */
    private fun refreshReceiveMessage(messageBody: MessageBody) {
        //如果是语音则下载
        if (messageBody.real.type == MessageReal.TYPE_APPENDIX) {
            downloaderVoiceFile(null, messageBody.real.message)
        }
        //单聊
        var isSingleChat =
            receiveId == messageBody.sendId && messageBody.type == MessageBody.TYPE_PERSONAL

        //群聊
        var isGroupChat =
            receiveId == messageBody.receiveId && messageBody.type == MessageBody.TYPE_COMMON

        //判断收到的消息是否是当前聊天
        if (isSingleChat || isGroupChat) {
            //收到的消息转成chatbean
            var formChatBean = MessageUtils.receiveMessageToChatBean(messageBody)

            //设置消息未已读
            formChatBean.isRead = true

            chatAdapter.addData(ChatItem(formChatBean))

            //滑动底部
            smoothScrollToBottom()
        }
    }

    /**
     * 刷新收到的消息
     */
    private fun refreshSendMessage(messageBody: MessageBody) {
        //判断收到的消息是否是当前聊天
        if (account == messageBody.sendId) {
            //发出的消息转成chatbean
            var sendChatBean = MessageUtils.sendMessageToChatBean(messageBody)

            chatAdapter.addData(ChatItem(sendChatBean))
            //滑动底部
            smoothScrollToBottom()
        }
    }

    private fun smoothScrollToBottom() {
        rvMsg.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    /**
     * 跳转到图片选择
     */
    private fun startPictureSelectAct() {
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
//            .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
            .isWeChatStyle(true)// 是否开启微信图片选择风格
            .isUseCustomCamera(false)// 是否使用自定义相机
//            .setLanguage(language)// 设置语言，默认中文
//            .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
//            .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
//            .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
            .isWithVideoImage(false)// 图片和视频是否可以同选
            .maxSelectNum(1)// 最大图片选择数量
            //.minSelectNum(1)// 最小选择数量
            //.minVideoSelectNum(1)// 视频最小选择数量，如果没有单独设置的需求则可以不设置，同用minSelectNum字段
            .maxVideoSelectNum(1) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
            .imageSpanCount(4)// 每行显示个数
            .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
            //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && enableCrop(false);有效,默认处理
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 设置相册Activity方向，不设置默认使用系统
            .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
            //.cameraFileName("test.png")    // 重命名拍照文件名、注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
            //.renameCompressFile("test.png")// 重命名压缩文件名、 注意这个不要重复，只适用于单张图压缩使用
            //.renameCropFileName("test.png")// 重命名裁剪文件名、 注意这个不要重复，只适用于单张图裁剪使用
            .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
            .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
            .previewImage(true)// 是否可预览图片
            .previewVideo(true)// 是否可预览视频
            //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
            .enablePreviewAudio(false) // 是否可播放音频
            .isCamera(true)// 是否显示拍照按钮
            //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
            .enableCrop(false)// 是否裁剪
            .compress(true)// 是否压缩
            .compressQuality(80)// 图片压缩后输出质量 0~ 100
            .synOrAsy(true)//同步false或异步true 压缩 默认同步
            //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
            //.compressSavePath(getPath())//压缩图片保存地址
            //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
            //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
            .withAspectRatio(16, 9)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
            .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
            .isGif(false)// 是否显示gif图片
            .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
            .circleDimmedLayer(false)// 是否圆形裁剪
            //.setCircleDimmedColor(ContextCompat.getColor(this, R.color.app_color_white))// 设置圆形裁剪背景色值
            //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
            //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
            .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
            .openClickSound(false)// 是否开启点击声音
//            .selectionMedia(selectList)// 是否传入已选图片
            //.isDragFrame(false)// 是否可拖动裁剪框(固定)
            //                        .videoMaxSecond(15)
            //                        .videoMinSecond(10)
            //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
            //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
            .cutOutQuality(90)// 裁剪输出质量 默认100
            .minimumCompressSize(100)// 小于100kb的图片不压缩
            //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
            //.rotateEnabled(true) // 裁剪是否可旋转图片
            //.scaleEnabled(true)// 裁剪是否可放大缩小图片
            //.videoQuality()// 视频录制质量 0 or 1
            //.videoSecond()//显示多少秒以内的视频or音频也可适用
            //.recordVideoSecond()//录制视频秒数 默认60s
            //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径  注：已废弃
            .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code
    }

    /**
     * 跳转到拍照界面
     */
    private fun startCameraAct() {
        PictureSelector.create(this)
            .openCamera(PictureMimeType.ofImage())
            //.loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
            .forResult(PictureConfig.REQUEST_CAMERA);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                //选择图片的回调
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    var selectList: List<LocalMedia> = PictureSelector.obtainMultipleResult(data);

                    //上传图片
                    mPresenter.uploadPhoto(File(selectList.get(0).path))
                }

                //拍照的回调
                PictureConfig.REQUEST_CAMERA -> {
                    // 图片选择结果回调
                    var selectList: List<LocalMedia> = PictureSelector.obtainMultipleResult(data);

                    //上传图片
                    mPresenter.uploadPhoto(File(selectList.get(0).path))
                }
            }
        }
    }

    /**
     * 图片上传失败
     */
    override fun uploadPhotoFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    /**
     * 图片上传成功
     */
    override fun uploadPhotoSuccess(fileName: String, filePath: String) {
        mPresenter.sendImage(
            getMessageBody(
                "",
                filePath,
                MessageReal.TYPE_IMG
            )
        )
    }

    /**
     * 语音上传失败
     */
    override fun uploadVoiceFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    /**
     *  语音上传成功
     */
    override fun uploadVoiceSuccess(
        //图片本地路径
        fileLocalPath: String,
        //图片名称
        fileNetWorkName: String,
        //图片的网络路径
        fileNetWorkPath: String,
        duration: Int
    ) {

        //保存本地消息到本地
        GreenDaoUtil.insertLocalFileBean(LocalFileBean(fileNetWorkPath, fileLocalPath))

        //发送消息
        mPresenter.sendVoice(
            getMessageBody(
                fileNetWorkPath,
                "",
                MessageReal.TYPE_APPENDIX
            )
        )
    }

    /**
     * 发送消息成功
     */
    override fun sendMessageSuccess(messageBody: MessageBody) {
        //将消息转换成chatbean
        var sendChatBean = MessageUtils.sendMessageToChatBean(messageBody)

        etContent.setText("")

        //插入到数据库
        GreenDaoUtil.insertChatBean(sendChatBean)

        //插入到最后消息数据
        GreenDaoUtil.insertLastChatBean(sendChatBean.toLastMesage())

        //刷新首页消息
        EventBusUtils.sendMessage(EventMsg.REFRESH_HOME_MESSAGE, Any())

        chatAdapter.addData(ChatItem(sendChatBean))

        //滑动到底部
        smoothScrollToBottom()
    }

    /**
     * 发送消息失败
     */
    override fun sendMessageFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ToastHelper.showShort("Configuration")
    }
}
