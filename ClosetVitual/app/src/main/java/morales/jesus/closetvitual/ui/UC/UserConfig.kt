package morales.jesus.closetvitual.ui.UC

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.registerForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.IniciarSesion
import morales.jesus.closetvitual.R

class UserConfig : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private var isEditable = false
    private var currentPassword = ""

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val reauthLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val password = result.data?.getStringExtra("password") ?: ""
            if (password.isNotEmpty()) {
                currentPassword = password
                actualizarDatosUsuario()
            }
        } else {
            Toast.makeText(requireContext(), "Reautenticación cancelada", Toast.LENGTH_SHORT).show()
            resetCampos()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_config, container, false)

        etName = root.findViewById(R.id.etName)
        etEmail = root.findViewById(R.id.etEmail)
        etPassword = root.findViewById(R.id.etPassword)
        btnEditProfile = root.findViewById(R.id.btnEditProfile)
        btnLogout = root.findViewById(R.id.btnLogout)
        val btnRegresar: Button = root.findViewById(R.id.btnRegresarHome)

        setEditable(false)
        cargarDatosUsuario()

        btnEditProfile.setOnClickListener {
            if (!isEditable) {
                habilitarEdicion()
            } else {
                validarCampos()
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), IniciarSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        btnRegresar.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        return root
    }

    private fun habilitarEdicion() {
        isEditable = true
        setEditable(true)
        btnEditProfile.text = "Guardar cambios"
        etPassword.text.clear()
        etPassword.hint = "Nueva contraseña"
    }

    private fun setEditable(enabled: Boolean) {
        etName.isEnabled = enabled
        etEmail.isEnabled = enabled
        etPassword.isEnabled = enabled
    }

    private fun cargarDatosUsuario() {
        val user = auth.currentUser ?: return

        db.collection("Usuarios").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nombre = document.getString("nombre") ?: ""
                    val email = user.email ?: ""

                    etName.setText(nombre)
                    etEmail.setText(email)
                    etPassword.setText("********")
                    etPassword.isEnabled = false
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validarCampos() {
        val nombre = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (nombre.isEmpty()) {
            etName.error = "Nombre requerido"
            return
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email inválido"
            return
        }

        // Si el campo de contraseña no está vacío y tiene menos de 6 caracteres
        if (password.isNotEmpty() && password.length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        // Verificar si se necesita reautenticación
        val user = auth.currentUser ?: return
        val necesitaReautenticar = email != user.email || password.isNotEmpty()

        if (necesitaReautenticar) {
            solicitarReautenticacion()
        } else {
            actualizarDatosUsuario()
        }
    }

    private fun solicitarReautenticacion() {
        val intent = Intent(requireContext(), IniciarSesion::class.java)
        intent.putExtra("reauth", true)
        reauthLauncher.launch(intent)
    }

    private fun actualizarDatosUsuario() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val nombre = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val nuevaPassword = etPassword.text.toString().trim()

        // Actualizar datos en Firestore
        val userUpdates = hashMapOf<String, Any>(
            "nombre" to nombre,
            "email" to email
        )

        db.collection("Usuarios").document(uid)
            .update(userUpdates)
            .addOnSuccessListener {
                // Actualizar email en Firebase Auth si cambió
                if (email != user.email) {
                    actualizarEmail(user, email)
                }

                // Actualizar contraseña si se proporcionó una nueva
                if (nuevaPassword.isNotEmpty() && nuevaPassword != "********") {
                    actualizarPassword(user, nuevaPassword)
                }

                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                deshabilitarEdicion()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarEmail(user: FirebaseUser, newEmail: String) {
        user.verifyBeforeUpdateEmail(newEmail)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Se envió un correo de verificación a $newEmail", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al actualizar email: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarPassword(user: FirebaseUser, newPassword: String) {
        user.updatePassword(newPassword)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al actualizar contraseña: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deshabilitarEdicion() {
        isEditable = false
        setEditable(false)
        btnEditProfile.text = "Editar perfil"
        etPassword.setText("********")
        etPassword.hint = "Contraseña"
    }

    private fun resetCampos() {
        cargarDatosUsuario()
        deshabilitarEdicion()
    }
}