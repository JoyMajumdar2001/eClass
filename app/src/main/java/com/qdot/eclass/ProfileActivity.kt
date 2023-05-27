package com.qdot.eclass

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.carousel.CarouselLayoutManager
import com.qdot.eclass.databinding.ActivityProfileBinding
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Avatars
import io.appwrite.services.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.trimSubstring

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var account: Account
    private lateinit var avatars: Avatars
    private lateinit var teams: Teams
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        account = Account(client)
        avatars = Avatars(client)
        teams = Teams(client)
        CoroutineScope(Dispatchers.IO).launch {
            val profile = account.get()
            val img = avatars.getInitials(profile.name)
            teams.list().apply {
                withContext(Dispatchers.Main){
                    binding.teamRv.layoutManager = CarouselLayoutManager()
                    binding.teamRv.adapter = CarouselAdapter(this@apply.teams)
                }
            }
            withContext(Dispatchers.Main) {
                binding.userNameTextView.text = profile.name
                binding.emailTextView.text = profile.email
                binding.memberTimeChip.text = "Member since : ${profile.createdAt.trimSubstring(0,10)}"
                binding.profileImageView.load(img) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.baseline_account_circle_24)
                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
        binding.logoutFab.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                account.deleteSessions().apply {
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                }
            }
        }
    }
}