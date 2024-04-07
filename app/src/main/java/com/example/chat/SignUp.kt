package com.example.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.chat.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class SignUp : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var imageEdit: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var passEdit: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBase: FirebaseDatabase
    //private lateinit var mDbRef: DatabaseReference
    private var selectedImage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        mDataBase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()

        nameEdit = findViewById(R.id.nameText)
        emailEdit = findViewById(R.id.emailText)
        imageEdit = findViewById(R.id.imageBord)
        passEdit = findViewById(R.id.passText)
        btnSignUp = findViewById(R.id.btnSignUp)

        imageEdit.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,45)
        }
        btnSignUp.setOnClickListener{
            val name = nameEdit.text.toString()
            val email = emailEdit.text.toString()
            val password = passEdit.text.toString()
            if(email.isEmpty() || password.isEmpty() || name.isEmpty()){
                Toast.makeText(this@SignUp, "Введите данные", Toast.LENGTH_SHORT).show()
            }
            else{
                if(selectedImage!=null){
                    mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this@SignUp){ task ->
                            if(task.isSuccessful){
                                val reference = mStorage.reference.child("Profile").child(mAuth.uid!!)
                                reference.putFile(selectedImage!!).addOnCompleteListener(this@SignUp){ task1 ->
                                    if(task1.isSuccessful){
                                        reference.downloadUrl.addOnCompleteListener(this@SignUp){uri->
                                            val imageUrl = uri.toString()
                                            val uid = mAuth.currentUser?.uid!!
                                            val user = User(name,email,uid,imageUrl)
                                            mDataBase.reference.child("user").child(uid).setValue(user)
                                            val intent = Intent(this@SignUp, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                }
                else{
                    mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this@SignUp){ task ->
                            if(task.isSuccessful){
                                val uid = mAuth.currentUser?.uid!!
                                val user = User(name,email,uid,"No Image")
                                mDataBase.reference.child("user").child(uid).setValue(user)
                                val intent = Intent(this@SignUp, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
            }
            /*else{
               signUp(name,email,password)
            }*/
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null){
            if(data.data != null){
                val uri = data.data
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference.child("Profile").child(time.toString()+"")
                /*reference.putFile(uri!!).addOnCompleteListener(this@SignUp) { task ->
                    if(task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener{ uri->
                            val filePath = uri.toString()
                            val obj = HashMap<String,Any>()
                            //obj["profileImage"] = filePath
                            /*mDataBase.reference.child("user").child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnCompleteListener{}*/
                        }
                    }
                }*/
                Glide.with(this@SignUp).load(data.data).apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade()).into(imageEdit)
                selectedImage = data.data
            }
        }
    }

    /*private fun signUp(name: String, email: String, password: String) {
            if(password.length < 6){
                Toast.makeText(this@SignUp,"Пароль должен быть не меньше 6 символов", Toast.LENGTH_SHORT).show()
            }
            else{
                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){
                            addUserToDatBase(name,email,mAuth.currentUser?.uid!!)
                            val intent = Intent(this@SignUp, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this@SignUp,"Произошла ошибка", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun addUserToDatBase(name: String, email: String, uid: String?) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid!!).setValue(User(name,email,uid))
    }*/
}