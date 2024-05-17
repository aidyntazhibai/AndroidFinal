package com.example.onlineshop


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshop.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val adminEmail = "admin@kbtu.kz"
    private val adminPassword = "adminn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()

                        val intent = if (email == adminEmail && password == adminPassword) {
                            Intent(this, AdminActivity::class.java)
                        } else {
                            Intent(this, MainActivity::class.java)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception?.message ?: "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

