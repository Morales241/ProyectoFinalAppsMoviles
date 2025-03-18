package morales.jesus.closetvitual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

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

        auth = Firebase.auth

        val et_correo: EditText = findViewById(R.id.et_correo)
        val et_contrasena: EditText = findViewById(R.id.et_contrasena)

        // redirige a la pantalla de registro
        val tvRegistrarse = findViewById<TextView>(R.id.tv_registrarse)
        tvRegistrarse.setOnClickListener {
            val intent = Intent(this, registrarse::class.java)
            startActivity(intent)
        }

        //Cuando se va a iniciar sesion. redirige a la pagina de inicio
        val btnIniciarSesion = findViewById<Button>(R.id.btn_iniciar_sesion)
        btnIniciarSesion.setOnClickListener {
            //falta validar
            login(et_email.text.toString(), et_password.text.toString())
        }

    }

    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    goToMain(user!!)
                } else {
                    Toast.makeText(this, "Email o Contraseña no coincide", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun goToMain(user: FirebaseUser){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}