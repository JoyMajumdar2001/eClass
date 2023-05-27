package com.qdot.eclass.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noti_table")
data class NotificationModel(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "noti_class") val notiClass: String,
    @ColumnInfo(name = "noti_desc") val notiDesc: String,
    @ColumnInfo(name = "noti_time") val notiTime : Long,
    @ColumnInfo(name = "noti_seen") val notiSeen :Boolean
)