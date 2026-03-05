package com.lue.eventreport.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lue.eventreport.data.repository.EventRepository
import com.lue.eventreport.data.remote.MockNetworkClient
import com.lue.eventreport.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val mockNetworkClient: MockNetworkClient
) : androidx.lifecycle.ViewModel() {

    private val _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> = _eventList

    private val _sendResult = MutableLiveData<String>()
    val sendResult: LiveData<String> = _sendResult

    private val _isSending = MutableLiveData<Boolean>(false)
    val isSending: LiveData<Boolean> = _isSending

    val predefinedEvents = listOf(
        "用户进入首页",
        "用户通过关卡",
        "用户点击按钮",
        "用户登录",
        "用户退出",
        "用户购买道具",
        "用户分享应用",
        "用户观看广告"
    )

    init {
        repository.getAllEvents().asLiveData().observeForever { events ->
            _eventList.value = events
        }
    }

    fun recordEvent(eventName: String, properties: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            repository.insertEvent(eventName, properties)
        }
    }

    fun sendAllEvents() {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val result = repository.sendPendingEvents()
                when (result) {
                    is EventRepository.SendResult.NoEvents -> {
                        _sendResult.value = "没有待发送的事件"
                    }
                    is EventRepository.SendResult.Completed -> {
                        _sendResult.value = "发送完成：成功 ${result.successCount}, 失败 ${result.failCount}"
                    }
                }
            } catch (e: Exception) {
                _sendResult.value = "发送失败：${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    fun setMockNetworkAlwaysFail(fail: Boolean) {
        mockNetworkClient.setAlwaysFail(fail)
    }
}
