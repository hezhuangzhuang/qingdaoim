package com.hw.contactsmodule.ui.fragment


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hw.baselibrary.ui.fragment.BaseMvpFragment
import com.hw.baselibrary.utils.ToastHelper
import com.hw.contactsmodule.R
import com.hw.contactsmodule.inject.component.DaggerContactsComponent
import com.hw.contactsmodule.inject.module.ContactsModule
import com.hw.contactsmodule.mvp.contract.ContactsContract
import com.hw.contactsmodule.mvp.presenter.ContactsPresenter
import com.hw.contactsmodule.ui.adapter.AllPeopleAdapter
import com.hw.contactsmodule.ui.adapter.GroupChatAdapter
import com.hw.contactsmodule.ui.adapter.OrganizationAdapter
import com.hw.contactsmodule.ui.adapter.item.OrganizationItem
import com.hw.provider.chat.bean.ConstactsBean
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.net.respone.contacts.GroupChatBean
import com.hw.provider.net.respone.contacts.OrganizationBean
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.fragment_contacts.*
import qdx.stickyheaderdecoration.NormalDecoration
import com.hw.contactsmodule.ui.adapter.OrganizationAdapter.onClildClickLis as onClildClickLis1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TYPE = "TYPE"
private const val ARG_PARAM2 = "param2"

/**
 * 联系人界面
 */
class ContactsFragment : BaseMvpFragment<ContactsPresenter>(), ContactsContract.View {

    //懒加载获取类型
    private val type by lazy {
        arguments?.get(TYPE)
    }

    //全部联系人适配器
    private lateinit var allPeopleAdapter: AllPeopleAdapter

    //组织结构适配器
    private lateinit var organizationAdapter: OrganizationAdapter

    //群聊适配器
    private lateinit var groupChatAdapter: GroupChatAdapter

    private val organPeopleMap: HashMap<Int, List<PeopleBean>> by lazy {
        HashMap<Int, List<PeopleBean>>()
    }

    private val errorView: View by lazy {
        View.inflate(mActivity, R.layout.error_view, null)
    }

    private val emptyView: View by lazy {
        View.inflate(mActivity, R.layout.empty_view, null)
    }

    override fun injectComponent() {
        DaggerContactsComponent.builder()
            .activityComponent(mActivityComponent)
            .contactsModule(ContactsModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun initData(bundle: Bundle?) {
        //设置强制更新
        forceLoad = true

        errorView.setOnClickListener {
            getData()
        }

        emptyView.setOnClickListener {
            getData()
        }

        initAdapter()
    }

    /**
     * 创建适配器
     */
    private fun initAdapter() {
        when (type) {
            //全部
            TYPE_ALL_PEOPLE -> initAllAdapter()

            //组织结构
            TYPE_ORGANIZATION -> initOrganAdapter()

            //群聊
            TYPE_GROUP_CHAT -> initGroupChatAdapter()
        }
    }

    /**
     * 获取数据
     */
    private fun getData() {
        when (type) {
            //全部
            TYPE_ALL_PEOPLE -> {
                mPresenter.getAllPeople()
            }

            //组织
            TYPE_ORGANIZATION -> {
                mPresenter.getAllOrganizations()
            }
            //群聊
            TYPE_GROUP_CHAT -> {
                mPresenter.getGroupChat()
            }
        }
    }

    /**
     * 创建全部适配器
     */
    private fun initAllAdapter() {
        allPeopleAdapter = AllPeopleAdapter(R.layout.item_all_people, ArrayList<PeopleBean>())
        allPeopleAdapter.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                val peopleBean = allPeopleAdapter.getItem(position)!!
                ARouter.getInstance()
                    .build(RouterPath.Contacts.CONTACT_DETAILS)
                    .withString(RouterPath.Contacts.FILED_RECEIVE_ID, peopleBean.sip)
                    .withString(RouterPath.Contacts.FILED_RECEIVE_NAME, peopleBean.name)
                    .navigation()
            }
        })
        rvList.layoutManager = LinearLayoutManager(mActivity)
        rvList.adapter = allPeopleAdapter

        getData()
    }

    /**
     * 创建组织结构适配器
     */
    private fun initOrganAdapter() {
        organizationAdapter = OrganizationAdapter(ArrayList<MultiItemEntity>())
        organizationAdapter.setChildClick(object : onClildClickLis1 {
            override fun onCollapseClick(pos: Int, depId: Int) {
                organizationAdapter.collapse(pos)
            }

            override fun onExpandClick(pos: Int, depId: Int) {
                //包含数据则直接展开
                if (organPeopleMap.containsKey(pos)) {
                    organizationAdapter.expand(pos)
                } else {//请求数据
                    mPresenter.getDepIdConstacts(pos, depId)
                }
            }

            override fun onPersonClick(peopleBean: PeopleBean) {
                ToastHelper.showShort(peopleBean.name)
            }
        })
        rvList.layoutManager = LinearLayoutManager(mActivity)
        rvList.adapter = organizationAdapter
    }

    /**
     * 创建群聊适配器
     */
    private fun initGroupChatAdapter() {
        groupChatAdapter = GroupChatAdapter(R.layout.item_group_chat, ArrayList<GroupChatBean>())
        groupChatAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.rl_root -> {
                    val groupChatBean = groupChatAdapter.getItem(position)!!
                    ARouter.getInstance()
                        .build(RouterPath.Chat.CHAT)
                        .withString(RouterPath.Chat.FILED_RECEIVE_ID, groupChatBean.id.toString())
                        .withString(RouterPath.Chat.FILED_RECEIVE_NAME, groupChatBean.groupName)
                        .withBoolean(RouterPath.Chat.FILED_IS_GROUP, true)
                        .navigation()
                }

                R.id.ivCreateConf -> {
                    val groupChatBean = groupChatAdapter.getItem(position)!!
                    mPresenter.createConf(
                        groupChatBean.groupName,
                        "120",
                        "",
                        groupChatBean.id.toString(),
                        1
                    )
                }
            }
        }
        rvList.layoutManager = LinearLayoutManager(mActivity)
        rvList.adapter = groupChatAdapter
    }

    override fun bindLayout(): Int = R.layout.fragment_contacts

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doLazyBusiness() {
        getData()
    }

    override fun onError(text: String) {
        ToastHelper.showShort("onError:" + text)
    }

    private var decoration: NormalDecoration? = null

    /**
     * 全部联系人获取完毕
     */
    override fun showAllPeople(allPeople: List<PeopleBean>) {
        allPeopleAdapter.replaceData(allPeople)

        var constactsBean: ConstactsBean? = null
        allPeople.forEach {
            constactsBean = ConstactsBean(it.sip, it.name)

            //保存联系人
            GreenDaoUtil.insertConstactsBean(constactsBean)
        }
        //如果已经有了item则删除
        if (null != decoration) {
            rvList.removeItemDecorationAt(0)
        }
        decoration = object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String {
                return allPeople.get(pos).firstLetter
            }
        }

        //添加item
        rvList.addItemDecoration(decoration!!)
    }

    //显示组织结构
    override fun showOrgan(allOrgan: List<OrganizationBean>) {
        var multiItems = ArrayList<MultiItemEntity>()

        allOrgan.forEach { organBean ->
            multiItems.add(OrganizationItem(organBean))
        }

        //刷新界面
        organizationAdapter.replaceData(multiItems)
    }

    //显示组织的下属成员
    override fun showOrganPeople(pos: Int, peoples: List<PeopleBean>) {
        var item = organizationAdapter.getItem(pos) as OrganizationItem;
        //添加到组织结构中
        peoples.forEach {
            item.addSubItem(it)
        }

        //添加到map中，避免重复请求
        organPeopleMap.put(pos, peoples)

        organizationAdapter.notifyDataSetChanged()
        organizationAdapter.expand(pos)
    }

    override fun showGroupChat(groupChats: List<GroupChatBean>) {
        groupChatAdapter.replaceData(groupChats)
    }

    override fun showError(errorMsg: String) {
        ToastHelper.showShort("showError:" + errorMsg)
        when (type) {
            //全部
            TYPE_ALL_PEOPLE -> allPeopleAdapter.emptyView = errorView

            //组织
            TYPE_ORGANIZATION -> organizationAdapter.emptyView = errorView

            //群聊
            TYPE_GROUP_CHAT -> groupChatAdapter.emptyView = errorView
        }
    }

    override fun showEmptyView() {
        when (type) {
            //全部
            TYPE_ALL_PEOPLE -> allPeopleAdapter.emptyView = emptyView

            //组织
            TYPE_ORGANIZATION -> organizationAdapter.emptyView = emptyView

            //群聊
            TYPE_GROUP_CHAT -> groupChatAdapter.emptyView = emptyView
        }
    }

    override fun showLoading() {
//        super.showLoading()
    }

    override fun dismissLoading() {
//        super.dismissLoading()
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(TYPE)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        const val TYPE_ALL_PEOPLE = "全部"
        const val TYPE_ORGANIZATION = "组织结构"
        const val TYPE_GROUP_CHAT = "群聊"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(type: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE, type)

                }
            }
    }
}
