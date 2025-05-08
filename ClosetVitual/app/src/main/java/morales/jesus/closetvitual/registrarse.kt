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
import com.google.firebase.firestore.FirebaseFirestore

class registrarse : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            if (et_contrasena.text.toString() == et_confirmar_contrasena.text.toString()) {
                signUp(
                    et_correo.text.toString(),
                    et_contrasena.text.toString(),
                    et_nombre.text.toString()
                )
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

    fun signUp(email: String, password: String, nombre: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val usuario = hashMapOf(
                            "nombre" to nombre,
                            "email" to email
                        )

                        db.collection("Usuarios").document(it.uid)
                            .set(usuario)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Usuario registrado exitosamente")
                                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show()

                                // Ahora sí navegas
                                val intent = Intent(this, IniciarSesion::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al guardar usuario", e)
                                Toast.makeText(this, "Fallo al guardar en Firestore", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Log.e("Auth", "Fallo en registro: ${task.exception}")
                    Toast.makeText(this, "Registro falló: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}