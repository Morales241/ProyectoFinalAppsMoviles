package morales.jesus.closetvitual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class IniciarSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val txtCorreo: EditText = findViewById(R.id.txtCorreo)
        val txtContra: EditText = findViewById(R.id.et_contrasena)

        val tvRegistrarse = findViewById<TextView>(R.id.tv_registrarse)
        tvRegistrarse.setOnClickListener {
            val intent = Intent(this, registrarse::class.java)
            startActivity(intent)
        }

        val btnIniciarSesion = findViewById<Button>(R.id.btn_iniciar_sesion)
        btnIniciarSesion.setOnClickListener {
            login(txtCorreo.text.toString(), txtContra.text.toString())
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isReauth = intent.getBooleanExtra("reauth", false)

                    if (isReauth) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        val user = auth.currentUser
                        goToMain(user!!)
                    }
                } else {
                    Toast.makeText(this, "Email o Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMain(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
