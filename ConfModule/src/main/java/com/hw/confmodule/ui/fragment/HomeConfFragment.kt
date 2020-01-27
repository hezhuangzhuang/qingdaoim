package com.hw.confmodule.ui.fragment


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.alibaba.android.arouter.launcher.ARouter
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.fragment.BaseFragment
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.widgets.BaseDialog
import com.hw.confmodule.R
import com.hw.provider.router.RouterPath
import com.hw.provider.widget.SelectCreateDialog
import kotlinx.android.synthetic.main.fragment_home_conf.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 会议界面
 */
class HomeConfFragment : BaseFragment() {

    private val selectCreateDialog: SelectCreateDialog by lazy {
        SelectCreateDialog(mActivity)
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.fragment_home_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        rl_create.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.Conf.CREATE_CONF)
                .navigation()
        }

        rl_join.setOnClickListener {
            showJoinConfDialog()
        }

        rl_my_conf.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.Conf.MY_CONF_LIST)
                .navigation()
        }

        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
                ToastHelper.showShort("11")
            }

            override fun onRightClick(v: View?) {
                selectCreateDialog.setBackground(null)
                selectCreateDialog.showPopupWindow(v)
            }
        })
    }

    private var joinConfDialog: BaseDialog? = null

    private fun showJoinConfDialog() {
        if (null == joinConfDialog) {
            joinConfDialog = BaseDialog(mActivity)

            joinConfDialog?.contentView = initJoinConfView()
        }

        joinConfDialog?.show()
    }

    private fun initJoinConfView(): View {
        var view = View.inflate(mActivity, R.layout.diaolog_join_conf, null)
        val etAccessCode = view.findViewById<EditText>(R.id.et_accessCode)
        val btJoinConf = view.findViewById<Button>(R.id.bt_confirm)
        val btCancle = view.findViewById<Button>(R.id.bt_cancle)
        btJoinConf.setOnClickListener {
            if(etAccessCode.text.isEmpty()){
                ToastHelper.showShort("会议接入码不能为空")
                return@setOnClickListener
            }

        }
        btCancle.setOnClickListener {
            joinConfDialog?.dismiss()
        }
        return view
    }

    override fun onError(text: String) {
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

    /**
     * 启动沉浸式
     */
    override fun isStatusBarEnabled(): Boolean = !super.isStatusBarEnabled()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeConfFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeConfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
