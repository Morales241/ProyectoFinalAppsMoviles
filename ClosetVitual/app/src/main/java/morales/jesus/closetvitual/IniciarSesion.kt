package morales.jesus.closetvitual

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IniciarSesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // redirige a la pantalla de registro
        val tvRegistrarse = findViewById<TextView>(R.id.tv_registrarse)
        tvRegistrarse.setOnClickListener {
            val intent = Intent(this, registrarse::class.java)
            startActivity(intent)
        }

        // redirige a la pagina de inicio
        val btnIniciarSesion = findViewById<Button>(R.id.btn_iniciar_sesion)
        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}