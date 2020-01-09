package com.hw.confmodule.ui.fragment


import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.ui.fragment.BaseFragment
import com.hw.confmodule.R
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.fragment_home_conf.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 会议界面
 */
class HomeConfFragment : BaseFragment() {
    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.fragment_home_conf

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        rl_create.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.Conf.CREATE_CONF)
                .navigation()
        }
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
