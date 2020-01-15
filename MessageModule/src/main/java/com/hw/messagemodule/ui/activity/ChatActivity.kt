package com.hw.messagemodule.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.KeyboardUtils
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.messagemodule.R
import com.hw.messagemodule.data.bean.ChatBean
import com.hw.messagemodule.data.bean.ChatItem
import com.hw.messagemodule.data.bean.MessageBody
import com.hw.messagemodule.data.bean.MessageReal
import com.hw.messagemodule.db.GreenDaoUtil
import com.hw.messagemodule.inject.component.DaggerChatComponent
import com.hw.messagemodule.inject.module.ChatModule
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.presenter.ChatPresenter
import com.hw.messagemodule.ui.adapter.ChatAdapter
import com.hw.messagemodule.utils.GlideEngine
import com.hw.messagemodule.utils.MessageUtils
import com.hw.provider.chat.ChatMultipleItem
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.user.UserContants
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


/**
 * 聊天界面
 */
@Route(path = RouterPath.Chat.CHAT)
class ChatActivity : BaseMvpActivity<ChatPresenter>(), ChatContract.View, View.OnClickListener {

    //收件人id
    public var receiveId: String? = null

    //收件人名称
    public var receiveName: String? = null

    //是否是群聊
    public var isGroup: Boolean? = false

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
                //图片
                R.id.iv_voice ->
                    previewPhoto(item.chatBean.textContent)
            }
        }
        rvMsg.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMsg.adapter = chatAdapter
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
                finish()
            }

            override fun onRightClick(v: View?) {
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
    }

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
            receiveId!!,
            receiveName!!,
            if (isGroup!!) {
                MessageBody.TYPE_COMMON
            } else MessageBody.TYPE_PERSONAL,
            messageReal
        )
        return msg
    }

    override fun onError(text: String) {
    }

    override fun onDestroy() {
        super.onDestroy()
        //隐藏软键盘
        KeyboardUtils.hideSoftInput(etContent)
        //更新当前聊天人的阅读状态
        EventBusUtils.sendMessage(EventMsg.UPDATE_MESSAGE_READ_STATUS, receiveId!!)
        EventBus.getDefault().unregister(this)
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
            R.id.tvVideoCall ->
                ToastHelper.showShort("视频会议")

            //语音会议
            R.id.tvAudioCall ->
                ToastHelper.showShort("语音会议")

            //文件
            R.id.tvFile ->
                queryChatBeans()
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
        if (flEmotionView.isVisible) {
            flEmotionView.visibility = View.GONE
        } else {
            KeyboardUtils.hideSoftInput(etContent)
            flEmotionView.visibility = View.VISIBLE
        }
    }

    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //刷新消息
            EventMsg.RECEIVE_SINGLE_MESSAGE ->
                refreshReceiveMessage(messageEvent.messageData as MessageBody)
        }
    }

    /**
     * 刷新收到的消息
     */
    private fun refreshReceiveMessage(messageBody: MessageBody) {
        //判断收到的消息是否是当前聊天
        if (receiveId == messageBody.sendId) {
            //收到的消息转成chatbean
            var formChatBean = MessageUtils.receiveMessageToChatBean(messageBody)
            //设置消息未已读
            formChatBean.isRead = true

            chatAdapter.addData(ChatItem(formChatBean))
            //滑动底部
            smoothScrollToBottom()
        }
    }

    private fun smoothScrollToBottom() {
        rvMsg.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    /**
     * 获取收到消息的类型
     */
    private fun getReceiveMessageType(type: Int): Int {
        when (type) {
            //文字
            MessageReal.TYPE_STR ->
                return ChatMultipleItem.FORM_TEXT

            //图片
            MessageReal.TYPE_IMG ->
                return ChatMultipleItem.FORM_IMG

            //附件，语音
            MessageReal.TYPE_APPENDIX ->
                return ChatMultipleItem.FORM_VOICE

            //视频呼叫
            MessageReal.TYPE_VIDEO_CALL ->
                return ChatMultipleItem.FORM_VIDEO_CALL

            //音频呼叫
            MessageReal.TYPE_VOICE_CALL ->
                return ChatMultipleItem.FORM_VOICE_CALL

            else ->
                return ChatMultipleItem.FORM_TEXT
        }
    }

    /**
     * 获取收到消息的类型
     */
    private fun getReceiveMessageContent(messageBody: MessageBody): String {
        when (messageBody.real.type) {
            //文字
            MessageReal.TYPE_STR ->
                return messageBody.real.message

            //图片
            MessageReal.TYPE_IMG ->
                return messageBody.real.imgUrl

//            //附件，语音
//            MessageReal.TYPE_APPENDIX ->
//                return ChatMultipleItem.FORM_VOICE
//
//            //视频呼叫
//            MessageReal.TYPE_VIDEO_CALL ->
//                return ChatMultipleItem.FORM_VIDEO_CALL
//
//            //音频呼叫
//            MessageReal.TYPE_VOICE_CALL ->
//                return ChatMultipleItem.FORM_VOICE_CALL

            else ->
                return messageBody.real.message
        }
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
//                    .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
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
    override fun uploadFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    /**
     * 图片上传成功
     */
    override fun uploadSuccess(fileName: String, filePath: String) {
        mPresenter.sendImage(
            getMessageBody(
                "",
                filePath,
                MessageReal.TYPE_IMG
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

        smoothScrollToBottom()
    }

    /**
     * 发送消息失败
     */
    override fun sendMessageFaile(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }
}
