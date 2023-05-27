package com.qdot.eclass

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qdot.eclass.databinding.ActivitySignupBinding
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)

        binding.signupBtn.setOnClickListener {
            if (binding.emailTv.text.isNullOrBlank()||
                    binding.passwordTv.text.isNullOrBlank()||
                    binding.userNameTv.text.isNullOrBlank())
            {
                Toast.makeText(this,"Empty credentials", Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    account.create(ID.unique(),
                        binding.emailTv.text.toString(),
                        binding.passwordTv.text.toString(),
                        binding.userNameTv.text.toString()
                    ).apply {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@SignupActivity,"Account created, login now!",
                                Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    }
                }
            }
        }
    }
}