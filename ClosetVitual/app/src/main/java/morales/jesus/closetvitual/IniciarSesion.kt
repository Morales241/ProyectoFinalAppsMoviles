package morales.jesus.closetvitual

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class IniciarSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewFlipper: ViewFlipper
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        viewFlipper = findViewById(R.id.viewFlipper)
        viewFlipper.setInAnimation(this, R.anim.in_from_top)
        viewFlipper.setOutAnimation(this, R.anim.out_to_bottom)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // Iniciar sesion
        val txtCorreo: EditText = findViewById(R.id.txtCorreo)
        val txtContra: EditText = findViewById(R.id.et_contrasena)

        //Registrarse
        val et_correo: EditText = findViewById(R.id.et_correo)
        val et_nombre: EditText = findViewById(R.id.et_nombre)
        val et_contrasena: EditText = findViewById(R.id.et_contrasenaRegister)
        val et_confirmar_contrasena: EditText = findViewById(R.id.et_confirmar_contrasena)


        // cuando el usuario se registra. redirige a IniciarSesion
        val btnRegistrarse = findViewById<Button>(R.id.btn_registrarse)
        btnRegistrarse.setOnClickListener {
            val correo = et_correo.text.toString()
            val nombre = et_nombre.text.toString()
            val contra = et_contrasena.text.toString()
            val confirmar = et_confirmar_contrasena.text.toString()

            if (correo.isBlank() || nombre.isBlank() || contra.isBlank() || confirmar.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (contra != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                signUp(correo, contra, nombre)
            }
        }


        val tvRegistrarse = findViewById<TextView>(R.id.tv_registrarse)
        tvRegistrarse.setOnClickListener {
            viewFlipper.setInAnimation(this, R.anim.in_from_top)
            viewFlipper.setOutAnimation(this, R.anim.out_to_bottom)
            viewFlipper.showNext()
        }

        val tvIrAInicioSesion = findViewById<TextView>(R.id.tv_ir_a_inicio_sesion)
        tvIrAInicioSesion.setOnClickListener {
            viewFlipper.setInAnimation(this, R.anim.in_from_bottom)
            viewFlipper.setOutAnimation(this, R.anim.out_to_top)
            viewFlipper.showPrevious()
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

                                login(email, password)
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


    private fun goToMain(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
