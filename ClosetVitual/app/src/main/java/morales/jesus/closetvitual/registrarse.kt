package morales.jesus.closetvitual

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class registrarse : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrarse)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val et_correo: EditText = findViewById(R.id.et_correo)
        val et_nombre: EditText = findViewById(R.id.et_nombre)
        val et_contrasena: EditText = findViewById(R.id.et_contrasena)
        val et_confirmar_contrasena: EditText = findViewById(R.id.et_confirmar_contrasena)


        // cuando el usuario se registra. redirige a IniciarSesion
        val btnRegistrarse = findViewById<Button>(R.id.btn_registrarse)
        btnRegistrarse.setOnClickListener {
            //falta validar
            if (et_contrasena.text.toString() == et_confirmar_contrasena.text.toString()) {
                signUp(et_correo.text.toString(), et_contrasena.text.toString())
                val intent: Intent = Intent(this, IniciarSesion::class.java)
                startActivity(intent)
            }
        }

        // redirige a IniciarSesion
        val tvIrAInicioSesion = findViewById<TextView>(R.id.tv_ir_a_inicio_sesion)
        tvIrAInicioSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
            finish()
        }

    }

    //funcion que registra a un usuario con el email y la contraseña
    fun signUp(email: String, password: String) {
        Log.d("INFO", "email: ${email}, password: ${password}")
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("INFO", "Se registro de manera satisfactoria")
                val user = auth.currentUser
            } else {
                Log.w("ERROR", "No se registro el usuario", task.exception)
                Toast.makeText(
                    baseContext,
                    "El registro falló",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}