package morales.jesus.closetvitual.ui.UC

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.IniciarSesion
import morales.jesus.closetvitual.R

class UserConfig : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private var isEditable = false

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_user_config, container, false)

        // Referencias a vistas
        etName = root.findViewById(R.id.etName)
        etUsername = root.findViewById(R.id.etUsername)
        etEmail = root.findViewById(R.id.etEmail)
        etPassword = root.findViewById(R.id.etPassword)
        btnEditProfile = root.findViewById(R.id.btnEditProfile)
        btnLogout = root.findViewById(R.id.btnLogout)
        val btnRegresar: Button = root.findViewById(R.id.btnRegresarHome)

        // Deshabilitar los campos inicialmente
        setEditable(false)

        // Cargar datos del usuario actual
        cargarDatosUsuario()

        // Botón Editar/Guardar
        btnEditProfile.setOnClickListener {
            if (!isEditable) {
                // Cambiar a modo editable
                isEditable = true
                setEditable(true)
                btnEditProfile.text = "Guardar datos"
            } else {
                // Guardar cambios
                actualizarDatosUsuario()
            }
        }

        // Botón Cerrar sesión
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), IniciarSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // Botón regresar a Home
        btnRegresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        return root
    }

    private fun setEditable(enabled: Boolean) {
        etName.isEnabled = enabled
        etUsername.isEnabled = enabled
        etEmail.isEnabled = enabled
        etPassword.isEnabled = enabled
    }

    private fun cargarDatosUsuario() {
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid

            // Consultar Firestore
            db.collection("Usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: ""
                        val email = document.getString("email") ?: ""
                        val username = email.substringBefore("@") // Aquí hacemos username automático del correo

                        etName.setText(nombre)
                        etEmail.setText(email)
                        etUsername.setText(username)
                        etPassword.setText("********") // La contraseña no se obtiene directamente
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun actualizarDatosUsuario() {
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid
            val nuevoNombre = etName.text.toString().trim()
            val nuevoEmail = etEmail.text.toString().trim()

            val actualizaciones = hashMapOf<String, Any>(
                "nombre" to nuevoNombre,
                "email" to nuevoEmail
            )

            db.collection("Usuarios").document(uid)
                .update(actualizaciones)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                    setEditable(false)
                    btnEditProfile.text = "Editar datos"

                    // Actualizar el email en Firebase Authentication si se cambió
                    if (nuevoEmail != user.email) {
                        user.updateEmail(nuevoEmail)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Email actualizado en Firebase Auth", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error actualizando email en Auth", Toast.LENGTH_SHORT).show()
                            }
                    }

                    isEditable = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
