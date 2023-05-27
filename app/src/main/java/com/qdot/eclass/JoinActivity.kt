package com.qdot.eclass

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.qdot.eclass.databinding.ActivityJoinBinding
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)
        val teams = Teams(client)

        val urlData = intent.data
        val userId = urlData?.getQueryParameter("userId")
        val secret = urlData?.getQueryParameter("secret")
        val teamId = urlData?.getQueryParameter("teamId")
        val membershipId = urlData?.getQueryParameter("membershipId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val acc = account.get()
                withContext(Dispatchers.Main) {
                    if (userId == acc.id) {
                        binding.dataTv.text = "You have joined the class"
                    } else {
                        binding.dataTv.text = "You can not join with this account"
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main) {
                    binding.dataTv.text = "Creating your session...."
                }
                teams.updateMembershipStatus(
                    userId= userId.toString(),
                    secret = secret.toString(),
                    teamId = teamId.toString(),
                    membershipId = membershipId.toString()
                ).apply {
                    startActivity(Intent(this@JoinActivity,MainActivity::class.java))
                    finish()
                }
            }
        }

    }
}