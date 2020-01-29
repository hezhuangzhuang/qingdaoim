package com.hw.confmodule.data.bean

data class HistoryConfBean(
    var responseCode: Int,
    var message: String,
    var data: List<DataBean>
) {

    data class DataBean(
        var smcConfId: String,
        var creatorUriName: String,
        var confName: String,
        var accessCode: String,
        var duration: String,
        var confMediaType: Any,
        var creatorUri: String,
        var createTime: String,
        var sites: String,
        var sitesName: String,
        var ifSuccess: Int = 0,
        //参会人数
        var accountNumber: Int = 0,
        //会议时长
        var confLength: Int = 0
    ) {

    }
}