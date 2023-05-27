package com.qdot.eclass

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qdot.eclass.databinding.ActivityNotificationBinding
import com.qdot.eclass.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@NotificationActivity).NotiDao()
                .getAll().apply {
                    if (this.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            binding.noNotiLay.visibility = View.VISIBLE
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.noNotiLay.visibility = View.GONE
                            binding.notiRv.layoutManager =
                                LinearLayoutManager(this@NotificationActivity)
                            binding.notiRv.adapter = NotificationAdapter(this@apply)
                        }
                    }
                }
        }

    }
}