package com.hw.baselibrary

import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable

/**
 *author：Thinkpad
 *data:2019/12/7 08:52
 * kotlin的通用扩展
 */
/**
 * 扩展observable执行
 */
fun <T> Observable<T>.bindLife(lifecycleProvider: LifecycleProvider<*>): Observable<T> {
    return this.compose(lifecycleProvider.bindToLifecycle())
}


