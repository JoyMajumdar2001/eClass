package com.qdot.eclass.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Query("SELECT * FROM noti_table")
    fun getAll(): List<NotificationModel>

    @Insert
    fun insertNotification(notiModel: NotificationModel)
}