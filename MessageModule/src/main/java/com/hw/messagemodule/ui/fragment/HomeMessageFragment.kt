package com.hw.messagemodule.ui.fragment


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.utils.ToastHelper
import com.hw.messagemodule.R
import com.hw.messagemodule.mvp.contract.MessageContract
import com.hw.messagemodule.mvp.presenter.MessagePresenter
import com.hw.messagemodule.ui.adapter.HomeMessageAdapter
import kotlinx.android.synthetic.main.fragment_message.*
import com.hw.baselibrary.ui.fragment.BaseMvpFragment as BaseMvpFragment1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *  消息的fragment
 */
class HomeMessageFragment : BaseMvpFragment1<MessagePresenter>(), MessageContract.View {
    //自己初始化
    private lateinit var messageAdapter: HomeMessageAdapter

    override fun injectComponent() {

    }

    override fun initData(bundle: Bundle?) {
        initAdapter()
    }

    override fun bindLayout(): Int = R.layout.fragment_message

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        ImmersionBar.setTitleBar(mActivity, titleBar)
        titleBar.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }

            override fun onRightClick(v: View?) {
                ToastHelper.showShort("点击右侧按钮")
            }
        })
    }

    override fun doLazyBusiness() {
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
        messageAdapter = HomeMessageAdapter(R.layout.item_home_message, initTempData())
        rvList.layoutManager = LinearLayoutManager(activity)
        rvList.adapter = messageAdapter
    }

    private fun initTempData(): ArrayList<String> {
        var array = ArrayList<String>()
        array.add("第一条数据")
        array.add("第二条数据")
        array.add("第三条数据")
        array.add("第四条数据")
        return array
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
}
