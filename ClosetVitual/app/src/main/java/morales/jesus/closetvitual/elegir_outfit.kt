package morales.jesus.closetvitual

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.ui.Ropero.RoperoViewModel

class elegir_outfit : Fragment() {

    private var adaptador: OutfitAdapter? = null

    companion object {
        var Outfits = ArrayList<Outfit>()
        var PrendasUsuario = ArrayList<Prenda>()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_elegir_outfit, container, false)
        val navController = findNavController()

        adaptador = OutfitAdapter(Outfits, navController, parentFragmentManager)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerOutfits)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptador

        cargarPrendasYOutfits()

        val btnRegresar: MaterialButton = root.findViewById(R.id.btnRegresarOutfit)
        btnRegresar.setOnClickListener {
            navController.navigate(R.id.action_elegir_outfit_to_navigation_Outfits)
        }

        return root
    }

    private fun cargarPrendasYOutfits() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(user.uid).collection("prendas")
            .get()
            .addOnSuccessListener { prendaDocs ->
                PrendasUsuario.clear()

                prendaDocs.forEach { doc ->
                    val prenda = Prenda(
                        id = doc.id,
                        nombre = doc.getString("nombre"),
                        tipo = doc.getString("tipoPrenda"),
                        tags = doc.get("tags") as? List<String> ?: listOf(),
                        imagenUrl = doc.getString("fotoUrl"),
                        Color = when (val rawColor = doc.get("color")) {
                            is Long -> rawColor.toInt()
                            is String -> rawColor.toIntOrNull()
                            is Int -> rawColor
                            else -> null
                        }
                    )

                    PrendasUsuario.add(prenda)
                }

                cargarOutfits()
            }
            .addOnFailureListener {
                Log.e("Ropero", "Error al cargar prendas", it)
            }
    }


    private fun cargarOutfits() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val coleccion = "outfitsRegistrados"
        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(user.uid).collection(coleccion)
            .get()
            .addOnSuccessListener { documents ->
                Outfits.clear()

                documents.forEach { document ->
                    val data = document.data
                    val nombre = data["nombre"] as? String ?: "Sin nombre"
                    val id = document.id

                    val prendasMap = (data["prendas"] as? Map<String, List<String>>)
                        ?: (data["prendas"] as? Map<String, Any>)?.mapValues { entry ->
                            when (val value = entry.value) {
                                is List<*> -> value.filterIsInstance<String>()
                                else -> listOf()
                            }
                        } ?: emptyMap()

                    val outfit = Outfit(id, nombre, prendasMap)
                    Outfits.add(outfit)

                    val idsPrendas = prendasMap.values.flatten()
                    val prendasEnOutfit = PrendasUsuario.filter { idsPrendas.contains(it.id) }

                    adaptador?.setPrendasForOutfit(id, prendasEnOutfit)
                }

                adaptador?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Ropero", "Error al obtener outfits", exception)
            }
    }

    class PrendaAdapter(
        private val context: Context,
        private val prendas: List<Prenda>
    ) : RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.prenda, parent, false)
            return PrendaViewHolder(view)
        }

        override fun getItemCount(): Int = prendas.size

        override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
            val prenda = prendas[position]
            Log.d("PrendaAdapter", "Mostrando prenda: ${prenda}")

            Glide.with(context)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.imagenfondo)
                .into(holder.imgPrenda)

        }

        class PrendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
        }
    }

    class OutfitAdapter(
        private val outfits: List<Outfit>,
        private val navController: NavController,
        private val fragmentManager: FragmentManager
    ) : RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {


        private val outfitPrendasMap = mutableMapOf<String, List<Prenda>>()

        fun setPrendasForOutfit(outfitId: String, prendas: List<Prenda>) {
            outfitPrendasMap[outfitId] = prendas
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.outfit, parent, false)
            return OutfitViewHolder(view)
        }

        override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
            val outfit = outfits[position]
            val prendas = outfitPrendasMap[outfit.id] ?: emptyList()

            Log.d("OutfitAdapter", "Dibujando outfit: ${outfit.nombre} con ${prendas.size} prendas")

            holder.itemView.setOnClickListener {
                val result = Bundle().apply {
                    putParcelableArrayList("listaPrendas", ArrayList(prendas))
                }

                fragmentManager.setFragmentResult("resultadoSeleccionOutfit", result)
                navController.navigate(R.id.action_elegir_outfit_to_navigation_Outfits)

            }

            val prendaAdapter = PrendaAdapter(holder.itemView.context, prendas)
            holder.recyclerView.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerView.adapter = prendaAdapter


        }

        override fun getItemCount(): Int = outfits.size

        class OutfitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclePrendas)
        }
    }
}