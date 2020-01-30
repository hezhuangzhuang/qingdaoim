package com.hw.messagemodule.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.utils.NetWorkUtils
import com.hw.provider.chat.bean.MessageBody
import com.hw.messagemodule.mvp.contract.ChatContract
import com.hw.messagemodule.mvp.model.ChatService
import java.io.File
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2020/1/8 14:35
 */
class ChatPresenter @Inject constructor() : BasePresenter<ChatContract.View>(),
    ChatContract.Presenter {

    @Inject
    lateinit var chatService: ChatService

    /**
     * 发送消息
     */
    override fun sendMessage(messageBody: MessageBody) {
        if (!NetWorkUtils.isConnected()) {
            mRootView.sendMessageFaile("网络异常,发送失败")
            return
        }
        //是否发送成功
        val sendSuccess = chatService.sendMessage(messageBody)
        checkViewAttached()

        mRootView?.apply {
            if (sendSuccess) {
                mRootView.sendMessageSuccess(messageBody)
            } else {
                mRootView.sendMessageFaile("发送失败")
            }
        }
    }

    /**
     * 发送图片消息
     */
    override fun sendImage(messageBody: MessageBody) {
        //是否发送成功
        val sendSuccess = chatService.sendMessage(messageBody)

        checkViewAttached()
        mRootView?.apply {
            if (sendSuccess) {
                mRootView.sendMessageSuccess(messageBody)
            } else {
                mRootView.sendMessageFaile("发送图片失败")
            }
        }
    }

    /**
     * 上传图片
     */
    override fun uploadPhoto(file: File) {
        checkViewAttached()
        mRootView?.showLoading()

        chatService.uploadPhoto(file)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.SUCCESS == baseData.msg) {
                        mRootView.uploadPhotoSuccess(baseData.data.fileName, baseData.data.filePath)
                    } else {
                        mRootView.uploadPhotoFaile(baseData.msg)
                    }
                }
            }, { e ->
                mRootView?.apply {
                    dismissLoading()
                    mRootView.uploadPhotoFaile(ExceptionHandle.handleException(e))
                }
            })
    }

    /**
     * 上传语音
     */
    override fun uploadVoice(file: File, duration: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        chatService.uploadVoice(file)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.SUCCESS == baseData.msg) {
                        mRootView.uploadVoiceSuccess(
                            file.path,
                            baseData.data.fileName,
                            baseData.data.filePath,
                            duration
                        )
                    } else {
                        mRootView.uploadVoiceFaile(baseData.msg)
                    }
                }
            }, { e ->
                mRootView?.apply {
                    dismissLoading()
                    mRootView.uploadVoiceFaile(ExceptionHandle.handleException(e))
                }
            })
    }

    /**
     * 发送语音消息
     */
    override fun sendVoice(messageBody: MessageBody) {
        //是否发送成功
        val sendSuccess = chatService.sendMessage(messageBody)

        checkViewAttached()
        mRootView?.apply {
            if (sendSuccess) {
                mRootView.sendMessageSuccess(messageBody)
            } else {
                mRootView.sendMessageFaile("发送语音失败")
            }
        }
    }

}