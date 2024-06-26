package com.example.foodapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodapp.databinding.ActivitySignBinding
import com.example.foodapp.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClint: GoogleSignInClient
    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        // Khởi tạo xac thuc Firebase
        auth = Firebase.auth
        // Khởi tạo CSDL Firebase
        database = Firebase.database.reference
        // Khởi tạo CSDL Firebase
        googleSignInClint = GoogleSignIn.getClient(this, googleSignInOptions)
        binding.createAccountButton.setOnClickListener {
            username = binding.userName.text.toString()
            email = binding.emailAddess.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isBlank() || username.isBlank()) {
                Toast.makeText(this, "Vui lòng điền tất cả các chi tiế", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }
        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClint.signInIntent
            launcher.launch(signIntent)

        }
    }

    //trình khởi chạy để đăng nhập bằng google
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            } else {
                Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Tài khoản được tạo thành công", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity((Intent(this, LoginActivity::class.java)))
                finish()
            } else {
                Toast.makeText(this, "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)

            }
        }
    }

    private fun saveUserData() {
        username = binding.userName.text.toString()
        password = binding.password.text.toString().trim()
        email = binding.emailAddess.text.toString().trim()

        val user = UserModel(username, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //  lưu dữ liệu vào cơ sở dữ liệu Firebase
        database.child("user").child(userId).setValue(user)
    }
}