package morales.jesus.closetvitual.ui.Ropero

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.Outfit
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.adaptadorPrendas

class Ropero : Fragment() {

    private lateinit var roperoViewModel: RoperoViewModel
    private lateinit var dataList: ArrayList<Prenda>
    private lateinit var adapter: adaptadorPrendas
    private val db = FirebaseFirestore.getInstance()
    private var adaptador: OutfitAdapter? = null

    companion object {
        var Outfits = ArrayList<Outfit>()

        var llave: Boolean = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        roperoViewModel = ViewModelProvider(this).get(RoperoViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_ropero, container, false)
        roperoViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        cargarOutfits()

        val navController = findNavController()

        val btnNewOutfit: FloatingActionButton = root.findViewById(R.id.btnFlotante)
        btnNewOutfit.setOnClickListener {
            navController.navigate(R.id.registrar_Nuevo_Outfit)
        }

        adaptador = OutfitAdapter(Outfits, navController)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerOutfits)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptador

        return root
    }

    private fun cargarOutfits() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Usuarios")
            .document(usuarioId)
            .collection("outfitsRegistrados")
            .get()
            .addOnSuccessListener { documentos ->
                Outfits.clear()

                for (documento in documentos) {
                    val prendasMap = documento.get("prendas") as? Map<*, *>
                    if (prendasMap == null) continue

                    val listaPrendas = mutableListOf<Prenda>()
                    val totalPrendas = prendasMap.size
                    var prendasCargadas = 0

                    prendasMap.forEach { (_, prendaIdAny) ->
                        val prendaId = prendaIdAny as? String ?: return@forEach

                        db.collection("Usuarios")
                            .document(usuarioId)
                            .collection("prendas")
                            .document(prendaId)
                            .get()
                            .addOnSuccessListener { prendaDoc ->
                                val prenda = prendaDoc.toObject(Prenda::class.java)
                                prenda?.let { listaPrendas.add(it) }

                                prendasCargadas++
                                if (prendasCargadas == totalPrendas) {
                                    Outfits.add(Outfit(listaPrendas))
                                    adaptador?.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener {
                                prendasCargadas++
                                if (prendasCargadas == totalPrendas) {
                                    Outfits.add(Outfit(listaPrendas))
                                    adaptador?.notifyDataSetChanged()
                                }
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar outfits", Toast.LENGTH_SHORT).show()
            }
    }


    class PrendaAdapter(
        val context: Context,
        val prendas: List<Prenda>,
        val listener: (Prenda) -> Unit
    ) : RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_ropero, parent, false)

            return PrendaViewHolder(view)
        }

        override fun getItemCount(): Int = prendas.size;

        override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
            val prenda = prendas[position]
            //  holder.imgPrenda.setImageResource(prenda.imagen)

            holder.imgPrenda.setOnClickListener {
                listener(prenda)
            }

        }

        class PrendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
        }
    }

    class OutfitAdapter(
        val outfits: List<Outfit>,
        val navController: NavController
    ) : RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OutfitViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.outfit, parent, false)
            return OutfitViewHolder(view)
        }

        override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
            val outfit = outfits[position]

            val prendaAdapter = PrendaAdapter(holder.itemView.context, outfit.items) {
                navController.navigate(R.id.detallePrenda)
            }

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