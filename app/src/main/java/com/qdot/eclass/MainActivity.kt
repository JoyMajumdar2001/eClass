package com.qdot.eclass

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputEditText
import com.qdot.eclass.databinding.ActivityMainBinding
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account
import io.appwrite.services.Avatars
import io.appwrite.services.Realtime
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var teams: Teams
    private lateinit var account: Account
    private lateinit var avatars: Avatars
    private lateinit var realtime: Realtime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        account = Account(client)
        teams = Teams(client)
        avatars = Avatars(client)
        realtime = Realtime(client)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accountInfo = account.get()
                withContext(Dispatchers.Main) {
                    buildUi(accountInfo)
                    Intent(this@MainActivity,NotiService::class.java).also {
                        startService(it)
                    }
                }
            }catch (e:Exception){
                startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun buildUi(accountInfo: io.appwrite.models.Account<Any>) {
        binding.loadingCard.visibility = View.GONE
        binding.toolbar.setNavigationOnClickListener { binding.drawerLay.open() }
        val headerView  = binding.drawerNavView.getHeaderView(0)
        val imageView = headerView.findViewById<ImageView>(R.id.profileImage)
        val usernameView = headerView.findViewById<TextView>(R.id.profileName)
        val emailView = headerView.findViewById<TextView>(R.id.profileEmail)
        usernameView.text = accountInfo.name
        emailView.text = accountInfo.email
        CoroutineScope(Dispatchers.IO).launch {
            val img = avatars.getInitials(accountInfo.name)
            withContext(Dispatchers.Main) {
                imageView.load(img) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.baseline_account_circle_24)
                }
            }
        }
        reloadClassList()
        binding.addClassFab.setOnClickListener {
            InputSheet().show(this){
                title("Create a class")
                with(InputEditText{
                    required(true)
                    label("Class name")
                })
                onNegative {  }
                onPositive { result ->
                    val className = result.getString("0")
                    CoroutineScope(Dispatchers.IO).launch {
                       runCatching {
                           teams.create(teamId = ID.unique(), name = className.toString()).apply {
                               withContext(Dispatchers.Main) {
                                   Toast.makeText(
                                       this@MainActivity,
                                       "Class created",
                                       Toast.LENGTH_SHORT
                                   ).show()
                                   reloadClassList()
                               }
                           }
                       }
                    }
                }
            }
        }
        binding.drawerNavView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile_item -> {
                    startActivity(Intent(this,ProfileActivity::class.java))
                return@setNavigationItemSelectedListener false
                }
                R.id.notification_item -> {
                    startActivity(Intent(this,NotificationActivity::class.java))
                    return@setNavigationItemSelectedListener false
                }
                else -> {return@setNavigationItemSelectedListener false}
            }
        }
    }

    private fun reloadClassList() {
        CoroutineScope(Dispatchers.IO).launch {
            val teamList = teams.list()
            val adapter = ClassAdapter(teamList)
            val layManager = LinearLayoutManager(this@MainActivity)
            withContext(Dispatchers.Main){
                binding.classRecyclerView.adapter = adapter
                binding.classRecyclerView.layoutManager = layManager
            }
        }
    }
}