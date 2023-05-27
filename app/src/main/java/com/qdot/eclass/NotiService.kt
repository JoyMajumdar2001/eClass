package com.qdot.eclass

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.frogobox.notification.FrogoNotification
import com.qdot.eclass.room.AppDatabase
import com.qdot.eclass.room.NotificationModel
import io.appwrite.Client
import io.appwrite.extensions.toJson
import io.appwrite.services.Realtime
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NotiService : Service() {

    private lateinit var realtime:Realtime
    private lateinit var teams: Teams
    private val listTeamId = mutableListOf<String>()
    private var isRunning = false

    override fun onCreate() {
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        realtime =  Realtime(client)
        teams = Teams(client)
        isRunning= false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning=true
            CoroutineScope(Dispatchers.IO).launch {
                val teamList = teams.list()
                teamList.teams.forEach {
                    listTeamId.add(it.id)
                }
                withContext(Dispatchers.Main) {
                    realtime.subscribe("documents") {
                        val jsonData = JSONObject(it.payload.toJson())
                        if (it.events.contains("databases.*.collections.*.documents.*.create")) {
                            if (listTeamId.contains(jsonData.getString("team"))) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    teams.get(jsonData.getString("team")).apply {
                                        AppDatabase.getDatabase(this@NotiService)
                                            .NotiDao()
                                            .insertNotification(
                                                NotificationModel(
                                                    notiClass = this@apply.name,
                                                    notiDesc = jsonData.getString("desc"),
                                                    notiSeen = false,
                                                    notiTime = System.currentTimeMillis(),
                                                    uid = 0
                                                )
                                            )
                                        withContext(Dispatchers.Main) {
                                            FrogoNotification.Inject(this@NotiService)
                                                .setChannelId("id-1")
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setChannelName("eclass-1")
                                                .setContentTitle("Announcement in ${this@apply.name}")
                                                .setContentText(jsonData.getString("desc"))
                                                .setupAutoCancel()
                                                .build()
                                                .launch(jsonData.getInt("time"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return START_STICKY
    }
}