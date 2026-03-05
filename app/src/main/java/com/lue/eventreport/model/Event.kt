package com.lue.eventreport.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @SerializedName("event_name")
    val eventName: String,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("properties")
    val properties: Map<String, String> = emptyMap(),

    val status: EventStatus = EventStatus.PENDING,

    val retryCount: Int = 0
)

enum class EventStatus {
    PENDING,      // 待发送
    SENDING,      // 发送中
    SUCCESS,      // 发送成功
    FAILED        // 发送失败
}