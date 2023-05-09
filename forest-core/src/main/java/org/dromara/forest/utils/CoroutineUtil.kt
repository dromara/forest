package org.dromara.forest.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author CHMing
 * @since 1.5.23
 **/
object CoroutineUtil {

    @OptIn(DelicateCoroutinesApi::class)
    fun launch(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch {
            block()
        }
    }
}
