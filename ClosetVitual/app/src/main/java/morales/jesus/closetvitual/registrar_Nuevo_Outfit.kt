package morales.jesus.closetvitual

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class registrar_Nuevo_Outfit : Fragment() {

    private val viewModel: RegistrarNuevoOutfitViewModel by activityViewModels()

    private lateinit var nombreEditText: EditText
    private var categoriaActual: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_registrar__nuevo__outfit, container, false)

        nombreEditText = view.findViewById(R.id.txtnombreOutfit)

        val botonesCategorias = mapOf(
            R.id.btnsombreros to "Sombreros",
            R.id.btnaccesorios to "Accesorios",
            R.id.btnCamisetassimilares to "Camisetas y similares",
            R.id.btnAbrigoschaquetas to "Abrigos y chaquetas",
            R.id.btnpantalonesshorts to "Pantalones y shorts",
            R.id.btnzapatos to "Zapatos",
            R.id.btnbodysuits to "Bodysuits"
        )

        for ((id, categoria) in botonesCategorias) {
            view.findViewById<ImageButton>(id).setOnClickListener {
                categoriaActual = categoria
                val bundle = Bundle().apply {
                    putString("filtroCategoria", categoria)
                    putString("origen", "registrarOutfitNuevo")
                }
                findNavController().navigate(R.id.action_RNO_to_fragmento_elegir_ropa_outfit, bundle)

            }
        }

        setFragmentResultListener("resultadoSeleccionPrenda_registrarOutfitNuevo") { _, result ->
            val prendaId = result.getString("prendaSeleccionadaId")
            val tipo = result.getString("tipoPrenda")
            if (prendaId != null && tipo != null) {
                viewModel.agregarPrenda(tipo, prendaId)
                Log.d("OUTFIT_DEBUG", "prenda agregada: ${prendaId}")
                Toast.makeText(requireContext(), "Prenda agregada ${prendaId}", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnRegistrarOutfit).setOnClickListener {
            registrarOutfit()
        }

        return view
    }

    private fun registrarOutfit() {
        val nombre = nombreEditText.text.toString()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (nombre.isBlank()) {
            Toast.makeText(requireContext(), "Nombre del outfit requerido", Toast.LENGTH_SHORT).show()
            return
        }

        val prendas = viewModel.obtenerPrendas()
        if (prendas.isEmpty() || prendas.size < 4) {
            Toast.makeText(requireContext(), "Debes agregar al menos cuatro prendas", Toast.LENGTH_SHORT).show()
            return
        }

        val datosOutfit = hashMapOf(
            "nombre" to nombre,
            "prendas" to prendas
        )

        db.collection("Usuarios")
            .document(usuarioId)
            .collection("outfitsRegistrados")
            .add(datosOutfit)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Outfit registrado", Toast.LENGTH_SHORT).show()
                viewModel.limpiar()
                findNavController().navigate(R.id.navigation_Ropero)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al registrar outfit", Toast.LENGTH_SHORT).show()
            }
    }
}
