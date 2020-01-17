package com.hw.contactsmodule.ui.fragment


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.adapter.MyPagerAdapter
import com.hw.baselibrary.ui.fragment.BaseFragment
import com.hw.contactsmodule.R
import com.hw.provider.widget.SelectCreateDialog
import kotlinx.android.synthetic.main.fragment_home_contacts.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 */
class HomeContactsFragment : BaseFragment(){
    private val selectCreateDialog: SelectCreateDialog by lazy {
        SelectCreateDialog(activity)
    }

    val currentIndex = 0

    private val mTitles = arrayOf(
        ContactsFragment.TYPE_ALL_PEOPLE,
        ContactsFragment.TYPE_ORGANIZATION,
        ContactsFragment.TYPE_GROUP_CHAT
    )

    private val mFragments by lazy {
        ArrayList<Fragment>()
    }

    override fun initData(bundle: Bundle?) {
        initTab()
    }

    override fun bindLayout(): Int = R.layout.fragment_home_contacts

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
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

    private fun initTab() {
        mFragments.add(ContactsFragment.newInstance(ContactsFragment.TYPE_ALL_PEOPLE))
        mFragments.add(ContactsFragment.newInstance(ContactsFragment.TYPE_ORGANIZATION))
        mFragments.add(ContactsFragment.newInstance(ContactsFragment.TYPE_GROUP_CHAT))

        var pagerAdapter = MyPagerAdapter(childFragmentManager, mTitles, mFragments)

        vpContent.adapter = pagerAdapter

        tbLayout.setViewPager(vpContent)

        vpContent.currentItem = currentIndex
        vpContent.offscreenPageLimit = mTitles.size

        tbLayout.currentTab = currentIndex
        tbLayout.onPageSelected(currentIndex)
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
         * @return A new instance of fragment HomeContactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
