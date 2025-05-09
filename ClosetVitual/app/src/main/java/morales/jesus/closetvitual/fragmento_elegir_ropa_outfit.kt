package morales.jesus.closetvitual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.ui.Ropero.RoperoViewModel
import java.util.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class fragmento_elegir_ropa_outfit(private val prendasSeleccionadas: List<String> = listOf()) : Fragment() {
    private lateinit var fermodel: ferModel
    private lateinit var dataList: ArrayList<Prenda>
    private lateinit var adapter: adaptadorPrendas
    private val db = FirebaseFirestore.getInstance()
    private val selectedPrendas = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fermodel = ViewModelProvider(this).get(ferModel::class.java)
        val root = inflater.inflate(R.layout.fragment_fragmento_elegir_ropa_outfit, container, false)

        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        val recyclerView: RecyclerView = root.findViewById(R.id.recycleView)
        recyclerView.layoutManager = gridLayoutManager

        dataList = ArrayList()
        adapter = adaptadorPrendas(requireContext(), dataList) { prendaId ->
            seleccionarPrendaYSalir(prendaId)
        }
        recyclerView.adapter = adapter

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            obtenerPrendasDeUsuario(userId)
        } else {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        }

        val botonRegresar: MaterialButton = root.findViewById(R.id.btnRegresarOutfit)
        botonRegresar.setOnClickListener {
            findNavController().navigate(R.id.action_elegirRopaOutfit_to_registrarOutfit)
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
                        tags = (document.get("tags") as? List<String> ?: listOf()),
                        imagenUrl = document.getString("fotoUrl")
                    )
                    dataList.add(prenda)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun seleccionarPrendaYSalir(prendaId: String) {
        val bundle = Bundle().apply {
            putString("prendaSeleccionadaId", prendaId)
        }
        setFragmentResult("resultadoSeleccionPrenda", bundle)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}