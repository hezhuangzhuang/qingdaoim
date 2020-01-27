package com.hw.confmodule.data.bean

/**
 * 会议列表
 */
data class ConfBean(
    var code: Int,
    var msg: String,
    var data: List<DataBean>
) {

    data class DataBean(
        var smcConfId: String,
        var confName: String,
        var confStatus: Int,
        var chairUri: String,
        var beginTime: String,
        var creatorUri: String,
        var creatorName: String,
        var endTime: String,
        var accessCode: String,
        var siteStatusInfoList: List<SiteStatusInfoListBean>
    ) {
    }

    data class SiteStatusInfoListBean(
        var siteUri: String,
        var siteName: String,
        var siteType: Int,
        var siteStatus: Int,
        var microphoneStatus: Int,
        var loudspeakerStatus: Int
    ) {

    }
}