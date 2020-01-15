package com.hw.messagemodule.data.bean

/**
 *author：pc-20171125
 *data:2020/1/14 18:07
 * 上传图片的返回数据
 */
data class UploadBeanRespone(
    var code: Int,
    var msg: String,
    var data: DataBean
) {
    /**
     * code : 0
     * msg : success
     * data : {"fileName":"会控.jpg","filePath":"http://113.57.147.178:9021/upload/f4897559-bc1c-40a9-9ca7-c7ac3e807500.jpg"}
     */


    data class DataBean(
        var fileName: String,
        var filePath: String
    ) {
        /**
         * fileName : 会控.jpg
         * filePath : http://113.57.147.178:9021/upload/f4897559-bc1c-40a9-9ca7-c7ac3e807500.jpg
         */
    }
}