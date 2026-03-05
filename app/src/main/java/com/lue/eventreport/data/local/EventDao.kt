package com.lue.eventreport.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lue.eventreport.model.Event
import com.lue.eventreport.model.EventStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Query("SELECT * FROM events WHERE status = :status ORDER BY timestamp ASC")
    fun getEventsByStatus(status: EventStatus): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE status = :status ORDER BY timestamp ASC")
    suspend fun getEventsByStatusSync(status: EventStatus): List<Event>

    @Query("DELETE FROM events WHERE status = :status")
    suspend fun deleteEventsByStatus(status: EventStatus)

    @Query("SELECT * FROM events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<Event>>
}