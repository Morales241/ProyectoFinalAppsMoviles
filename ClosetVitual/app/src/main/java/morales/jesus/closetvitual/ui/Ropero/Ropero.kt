package morales.jesus.closetvitual.ui.Ropero

import android.annotation.SuppressLint
import android.content.Context
import android.database.Observable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.DocumentsContract.Root
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import morales.jesus.closetvitual.Conjunto
import morales.jesus.closetvitual.Outfit
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.ui.DetallePrenda.DetallePrenda

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


        val root =
            inflater.inflate(R.layout.fragment_ropero, container, false)



        roperoViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        if (llave) {
            cargarOutfits()
            llave = false

        }


        val navController = findNavController()


        adaptador = OutfitAdapter(root.context, Outfits, navController)

        val gridPrincipal: GridView = root.findViewById(R.id.GridView)

        gridPrincipal.adapter = adaptador

        return root
    }


    fun cargarOutfits() {

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
    ) :
        BaseAdapter() {

        override fun getCount(): Int = prendas.size

        override fun getItem(position: Int): Any = prendas[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var prenda = prendas[position]
            var inflater = LayoutInflater.from(context)
            var view = convertView ?: inflater.inflate(R.layout.prenda, parent, false)

            var imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
            imgPrenda.setImageResource(prenda.imagen)

            imgPrenda.setOnClickListener {
                listener(prenda)
            }

            return view
        }
    }

    class OutfitAdapter(val context: Context, val outfits: List<Outfit>, val navController: NavController) : BaseAdapter() {

        override fun getCount(): Int {
            return outfits.size
        }

        override fun getItem(position: Int): Any {
            return outfits[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var outfit = outfits[position]
            var inflater = LayoutInflater.from(context)
            var view = convertView ?: inflater.inflate(R.layout.outfit, parent, false)

            val gridItemOutfits: GridView = view.findViewById(R.id.gridView)

            var prendaAdapter = PrendaAdapter(context, outfit.items) {

                navController.navigate(R.id.detallePrenda)

            }

            gridItemOutfits.adapter = prendaAdapter

            return view
        }
    }

}