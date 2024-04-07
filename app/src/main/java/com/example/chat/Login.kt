package com.example.chat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_up)

        mAuth = FirebaseAuth.getInstance()

        emailEdit = findViewById(R.id.emailText)
        passEdit = findViewById(R.id.passText)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener{
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener{
            val email = emailEdit.text.toString()
            val password = passEdit.text.toString()
            login(email,password)
        }
    }

    private fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this@Login, "Введите данные", Toast.LENGTH_SHORT).show()
        }
        else{
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){   task->
                    if(task.isSuccessful){
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this@Login, "Неправильно указан логин и/или пароль", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}