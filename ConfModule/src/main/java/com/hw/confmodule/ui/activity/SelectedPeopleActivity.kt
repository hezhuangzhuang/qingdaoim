package com.hw.confmodule.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.confmodule.R
import com.hw.confmodule.ui.adapter.SelectedPeopleAdapter
import com.hw.provider.net.respone.contacts.PeopleBean
import com.hw.provider.router.RouterPath
import kotlinx.android.synthetic.main.activity_my_conf.*
import java.io.Serializable

/**
 * 已选择的成员
 */
@Route(path = RouterPath.Conf.SELECTED_PEOPLE)
class SelectedPeopleActivity : BaseActivity() {

    lateinit var selectedAdapter: SelectedPeopleAdapter

    //已选择的成员
    lateinit var selectPeoples: List<PeopleBean>

    override fun initData(bundle: Bundle?) {
        selectPeoples =
            intent.getSerializableExtra(RouterPath.Conf.FILED_SELECTED_PEOPLE) as List<PeopleBean>
    }

    override fun bindLayout(): Int = R.layout.activity_selected_people

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        selectedAdapter = SelectedPeopleAdapter(R.layout.item_selected_people, selectPeoples)
        selectedAdapter.setOnItemChildClickListener { adapter, view, position ->
            selectedAdapter.remove(position)
        }
        rvList.adapter = selectedAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    override fun doBusiness() {

    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
                var intent = Intent()
                intent.putExtra(RouterPath.Conf.FILED_SELECTED_PEOPLE, selectedAdapter.data as Serializable)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onTitleClick(v: View?) {

            }
        })
    }

    override fun onError(text: String) {

    }

}
