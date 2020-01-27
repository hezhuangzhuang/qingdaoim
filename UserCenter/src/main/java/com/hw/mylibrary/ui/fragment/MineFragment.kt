package com.hw.mylibrary.ui.fragment


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.dialog.MaterialDialogs
import com.hw.baselibrary.ui.fragment.BaseLazyFragment
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.mylibrary.R
import com.hw.mylibrary.mvp.model.UserService
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.net.respone.user.LoginBean
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService.logOut
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.fragment_mine.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 我的界面
 */
class MineFragment : BaseLazyFragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v!!.id) {
            tvAbout.id -> {
                ARouter.getInstance()
                    .build(RouterPath.UserCenter.PATH_ABOUT)
                    .navigation()
            }

            tvBindPhone.id ->
                ToastHelper.showShort("绑定手机")

            tvAbout.id ->
                ToastHelper.showShort("修改密码")

            btLogOut.id -> {
                //发出登出的消息
                EventBusUtils.sendMessage(EventMsg.LOGOUT, "")
            }
        }
    }

    override fun initData(bundle: Bundle?) {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


        tvAbout.setOnClickListener(this)
        tvBindPhone.setOnClickListener(this)
        tvChangePassWord.setOnClickListener(this)
        btLogOut.setOnClickListener(this)
    }

    override fun bindLayout(): Int = R.layout.fragment_mine

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        tvName.text = SPStaticUtils.getString(UserContants.DISPLAY_NAME)
    }

    override fun doLazyBusiness() {
    }

    override fun onError(text: String) {
    }

    private var param1: String? = null
    private var param2: String? = null


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
