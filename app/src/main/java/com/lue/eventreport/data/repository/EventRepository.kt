package com.lue.eventreport.data.repository

import com.lue.eventreport.data.local.EventDao
import com.lue.eventreport.data.remote.MockNetworkClient
import com.lue.eventreport.model.Event
import com.lue.eventreport.model.EventStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val networkClient: MockNetworkClient
) {

    fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }

    fun getPendingEvents(): Flow<List<Event>> {
        return eventDao.getEventsByStatus(EventStatus.PENDING)
    }

    suspend fun insertEvent(eventName: String, properties: Map<String, String> = emptyMap()) {
        val event = Event(
            eventName = eventName,
            properties = properties,
            status = EventStatus.PENDING
        )
        eventDao.insertEvent(event)
    }

    suspend fun sendPendingEvents(): SendResult {
        return withContext(Dispatchers.IO) {
            val pendingEvents = eventDao.getEventsByStatusSync(EventStatus.PENDING)

            if (pendingEvents.isEmpty()) {
                return@withContext SendResult.NoEvents
            }

            var successCount = 0
            var failCount = 0

            for (event in pendingEvents) {
                try {
                    eventDao.updateEvent(event.copy(status = EventStatus.SENDING))

                    val result = networkClient.sendEvent(event)

                    if (result.isSuccess) {
                        eventDao.updateEvent(
                            event.copy(
                                status = EventStatus.SUCCESS
                            )
                        )
                        successCount++
                    } else {
                        val newRetryCount = event.retryCount + 1
                        if (newRetryCount >= 3) {
                            eventDao.updateEvent(
                                event.copy(
                                    status = EventStatus.FAILED,
                                    retryCount = newRetryCount
                                )
                            )
                            failCount++
                        } else {
                            eventDao.updateEvent(
                                event.copy(
                                    status = EventStatus.PENDING,
                                    retryCount = newRetryCount
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    failCount++
                    e.printStackTrace()
                }
            }

            SendResult.Completed(successCount, failCount)
        }
    }

    sealed class SendResult {
        object NoEvents : SendResult()
        data class Completed(val successCount: Int, val failCount: Int) : SendResult()
    }
}
