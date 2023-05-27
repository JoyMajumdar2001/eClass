package com.qdot.eclass

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qdot.eclass.databinding.ActivityLoginBinding
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = Client(this)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("645e7beae63b22ab0c74")
            .setSelfSigned(true)
        val account = Account(client)

        binding.passwordResetBtn.setOnClickListener {
            startActivity(Intent(this,PasswordActivity::class.java))
        }
        binding.createAccbtn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            if (binding.emailTv.text.isNullOrBlank() || binding.passwordTv.text.isNullOrBlank()){
                Toast.makeText(this,"Empty credentials",Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        account.createEmailSession(
                            binding.emailTv.text.toString().trim(),
                            binding.passwordTv.text.toString().trim()
                        ).apply {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }catch (e:AppwriteException){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@LoginActivity,
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}