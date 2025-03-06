package morales.jesus.closetvitual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import morales.jesus.closetvitual.databinding.FragmentConfigUsuarioBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigUsuario.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigUsuario : Fragment() {

    private var _binding: FragmentConfigUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Datos de prueba (Mock)
        val usuarioMock = Usuario(
            img = R.drawable.user,
            nombre = "Jesús Morales",
            email = "jesus.morales@gmail.com",
            useraName = "jesus_m",
            pass = "123456"
        )

        // Asignar valores a la vista
        binding.ivProfilePic.setImageResource(usuarioMock.img)
        binding.etName.setText(usuarioMock.nombre)
        binding.etEmail.setText(usuarioMock.email)
        binding.etUsername.setText(usuarioMock.useraName)
        binding.etPassword.setText(usuarioMock.pass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}}