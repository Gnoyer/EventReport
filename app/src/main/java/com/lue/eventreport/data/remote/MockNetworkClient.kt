package com.lue.eventreport.data.remote

import com.lue.eventreport.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockNetworkClient @Inject constructor() {

    private var shouldFail = false

    /**
     * 模拟发送事件到服务器
     * 可以控制成功率来测试重试机制
     */
    suspend fun sendEvent(event: Event): Result<Unit> = withContext(Dispatchers.IO) {
        kotlinx.coroutines.delay(500)

        if (shouldFail || Random.nextFloat() < 0.3f) {
            Result.failure(Exception("Network error: Failed to send event"))
        } else {
            println("✅ 事件发送成功：${event.eventName}")
            Result.success(Unit)
        }
    }

    /**
     * 设置是否总是失败（用于测试重试）
     */
    fun setAlwaysFail(fail: Boolean) {
        shouldFail = fail
    }
}