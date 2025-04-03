package morales.jesus.closetvitual.ui.UC

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import morales.jesus.closetvitual.IniciarSesion
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.Usuario
import morales.jesus.closetvitual.ui.Ropero.Ropero.Companion.Outfits
import morales.jesus.closetvitual.ui.Ropero.Ropero.Companion.llave
import morales.jesus.closetvitual.ui.Ropero.Ropero.OutfitAdapter
import morales.jesus.closetvitual.ui.Ropero.RoperoViewModel

class UserConfig : Fragment() {

    private lateinit var userConfigViewModel: UserConfigViewModel
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button
    private var isEditable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        userConfigViewModel = ViewModelProvider(this).get(UserConfigViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user_config, container, false)

        // Mock usuario
        val usuarioMock = Usuario(R.drawable.user, "Beatriz Pinzón", "betty@gmail.com", "secretariaEcoModa", "*******")

        // Referencias
        etName = root.findViewById(R.id.etName)
        etUsername = root.findViewById(R.id.etUsername)
        etEmail = root.findViewById(R.id.etEmail)
        etPassword = root.findViewById(R.id.etPassword)
        btnEditProfile = root.findViewById(R.id.btnEditProfile)
        val btnRegresar: Button = root.findViewById(R.id.btnRegresarHome)
        btnLogout = root.findViewById(R.id.btnLogout)

        // Setear datos en los EditText
        etName.setText(usuarioMock.nombre)
        etUsername.setText(usuarioMock.useraName)
        etEmail.setText(usuarioMock.email)
        etPassword.setText(usuarioMock.pass)

        // Deshabilitar los campos al inicio
        setEditable(false)

        // Acción del botón Editar
        btnEditProfile.setOnClickListener {
            isEditable = !isEditable
            setEditable(isEditable)
            btnEditProfile.text = if (isEditable) "Guardar" else "Editar datos"
        }


        // Acción del botón Cerrar sesión
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Cierra sesión en Firebase
            val intent = Intent(requireActivity(), IniciarSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpia el back stack
            startActivity(intent)
            requireActivity().finish() // Cierra la actividad actual
        }

        // Botón de regreso a Home
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
}



