package morales.jesus.closetvitual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import java.time.LocalDate

class fragment_registrar_outfit : Fragment() {

    private val viewModel: RegistrarNuevoOutfitViewModel by activityViewModels()
    private var categoriaActual: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_outfit, container, false)

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
                    putString("origen", "registrarOutfit")
                }
                findNavController().navigate(R.id.action_registrarOutfit_to_elegirRopaOutfit, bundle)
            }
        }

        setFragmentResultListener("resultadoSeleccionPrenda_registrarOutfit") { _, result ->
            val prendaId = result.getString("prendaSeleccionadaId")
            val tipo = result.getString("tipoPrenda")
            if (prendaId != null && tipo != null) {
                viewModel.agregarPrenda(tipo, prendaId)
                Toast.makeText(requireContext(), "Prenda agregada: $prendaId", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnRegistrarUso).setOnClickListener {
            registrarOutfit()
        }

        return view
    }

    private fun registrarOutfit() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return


        val prendas = viewModel.obtenerPrendas()
        if (prendas.isEmpty() || prendas.size < 4) {
            Toast.makeText(requireContext(), "Debes agregar al menos cuatro prendas", Toast.LENGTH_SHORT).show()
            return
        }

        val datosOutfit = hashMapOf(
            "prendas" to prendas,
            "fecha" to Timestamp.now()
        )

        db.collection("Usuarios")
            .document(usuarioId)
            .collection("outfitsUsados")
            .add(datosOutfit)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Outfit registrado", Toast.LENGTH_SHORT).show()
                viewModel.limpiar()
                findNavController().navigate(R.id.navigation_Ropero)
            }
            //prueba para ver si si va a funcionar
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al registrar outfit", Toast.LENGTH_SHORT).show()
            }
    }
}
