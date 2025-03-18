package morales.jesus.closetvitual.ui.Ropero

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import morales.jesus.closetvitual.Outfit
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R

class Ropero : Fragment() {

    private lateinit var roperoViewModel: RoperoViewModel
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

        if (llave) {
            cargarOutfits()
            llave = false
        }

        val navController = findNavController()

        adaptador = OutfitAdapter(Outfits, navController)

        val recyclerView: RecyclerView = root.findViewById(R.id.GridView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adaptador

        return root
    }

    private fun cargarOutfits() {
        Outfits.add(
            Outfit(
                listOf(
                    Prenda(R.drawable.camisa_roja),
                    Prenda(R.drawable.pans_negro),
                    Prenda(R.drawable.zapatos_cafes)
                )
            )
        )

        Outfits.add(
            Outfit(
                listOf(
                    Prenda(R.drawable.gorro_rosa),
                    Prenda(R.drawable.camisa_roja),
                    Prenda(R.drawable.pans_negro)
                )
            )
        )
    }

    class PrendaAdapter(
        val context: Context,
        val prendas: List<Prenda>,
        val listener: (Prenda) -> Unit
    ) : RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.prenda, parent, false)
            return PrendaViewHolder(view)
        }

        override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
            val prenda = prendas[position]
            holder.imgPrenda.setImageResource(prenda.imagen)

            holder.imgPrenda.setOnClickListener {
                listener(prenda)
            }
        }

        override fun getItemCount(): Int = prendas.size

        class PrendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
        }
    }

    class OutfitAdapter(
        val outfits: List<Outfit>,
        val navController: NavController
    ) : RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.outfit, parent, false)
            return OutfitViewHolder(view)
        }

        override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
            val outfit = outfits[position]

            val prendaAdapter = PrendaAdapter(holder.itemView.context, outfit.items) {
                navController.navigate(R.id.detallePrenda)
            }

            holder.recyclerView.adapter = prendaAdapter
        }

        override fun getItemCount(): Int = outfits.size

        class OutfitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val recyclerView: RecyclerView = view.findViewById(R.id.gridView)
        }
    }
}
