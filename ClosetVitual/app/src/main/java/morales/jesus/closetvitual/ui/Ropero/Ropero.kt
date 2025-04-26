package morales.jesus.closetvitual.ui.Ropero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.adaptadorPrendas

class Ropero : Fragment() {

    private lateinit var roperoViewModel: RoperoViewModel
    private lateinit var dataList: ArrayList<Prenda>
    private lateinit var adapter: adaptadorPrendas
    private val db = FirebaseFirestore.getInstance()
    private var btnPrendaId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_fragmento_elegir_ropa_outfit, container, false)

        parentFragmentManager.setFragmentResultListener("solicitudSeleccionPrenda", this) { _, bundle ->
            btnPrendaId = bundle.getInt("btnPrendaId", -1)
        }

        val botonRegresar: MaterialButton = root.findViewById(R.id.btnRegresarOutfit)
        botonRegresar.setOnClickListener {
            findNavController().navigate(R.id.action_elegirRopaOutfit_to_registrarOutfit)
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.recycleView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        dataList = ArrayList()
        adapter = adaptadorPrendas(requireContext(), dataList) { prendaId ->
            seleccionarPrendaYSalir(prendaId)
        }
        recyclerView.adapter = adapter

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            obtenerPrendasDeUsuario(user.uid)
        } else {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private fun obtenerPrendasDeUsuario(userId: String) {
        db.collection("Usuarios").document(userId).collection("prendas")
            .get()
            .addOnSuccessListener { documents ->
                dataList.clear()
                for (document in documents) {
                    val prenda = Prenda(
                        id = document.id,
                        nombre = document.getString("nombre"),
                        tipo = document.getString("tipoPrenda"),
                        tag = (document.get("tags") as? List<String> ?: listOf()).toString(),
                        imagenUrl = document.getString("fotoUrl")
                    )
                    dataList.add(prenda)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun seleccionarPrendaYSalir(prendaId: String) {
        val prenda = dataList.find { it.id == prendaId } ?: return

        val bundle = Bundle().apply {
            putString("prendaSeleccionadaId", prenda.id)
            putString("nombrePrenda", prenda.nombre)
            putInt("btnPrendaId", btnPrendaId)
            putInt("imagenDrawableRes", R.drawable.logo)
        }

        setFragmentResult("resultadoSeleccionPrenda", bundle)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}
