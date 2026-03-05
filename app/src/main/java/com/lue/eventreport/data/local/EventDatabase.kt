package com.lue.eventreport.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lue.eventreport.model.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(EventTypeConverters::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}
