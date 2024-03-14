package kotlinpl.fbchat.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.kotlinpl.fbchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

//ссылки на поля

class SignUpActivity : AppCompatActivity() {



    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    fun registerUser(userName: String, email: String, password: String) {
        Log.d("SignUpActivity", "Regg Started")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    // Create a reference to the user's data node
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val userDataMap: HashMap<String, String> = hashMapOf(
                        "userName" to userName,
                        "profileImage" to "" // Fix the key typo
                    )

                    databaseReference.setValue(userDataMap).addOnCompleteListener { dataTask ->
                        if (dataTask.isSuccessful) {
                            Log.d("SignUpActivity", "S")
                            //open home activity
                            val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Optional: Finish the current activity after successful registration
                        } else {
                            Log.e("SignUpActivity", "S")
                            // Handle database operation failure
                            Toast.makeText(
                                applicationContext,
                                "Failed to register user data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Handle authentication failure
                    Toast.makeText(
                        applicationContext,
                        "Failed to register user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnSignUp.setOnClickListener {

            val etName = findViewById<EditText>(R.id.etName)
            val etEmail = findViewById<EditText>(R.id.etEmail)
            val etPassword = findViewById<EditText>(R.id.etPassword)
            val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

            val userName = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)  || TextUtils.isEmpty(email))

            {
                Toast.makeText(
                    applicationContext,
                    "All fields are required",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(
                    applicationContext,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "All field are filled",
                    Toast.LENGTH_SHORT
                ).show()
                registerUser(userName, email, password)
            }

        }
        btnLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}