package morales.jesus.closetvitual.ui.DetallePrenda

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import morales.jesus.closetvitual.Conjunto
import morales.jesus.closetvitual.ItemGridDetallePrenda
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.ui.home.HomeFragment
import morales.jesus.closetvitual.ui.home.HomeFragment.AdaptadorConjunto
import morales.jesus.closetvitual.ui.home.HomeFragment.Companion
import morales.jesus.closetvitual.ui.home.HomeFragment.Companion.Conjuntos
import morales.jesus.closetvitual.ui.home.HomeViewModel

class DetallePrenda : Fragment() {

    private lateinit var detallePrendaViewModel: DetallePrendaViewModel
    private var adaptadorCateforias: GridAdapter? = null
    private var adaptadorTags: GridAdapter? = null

    companion object {
        var categorias = ArrayList<ItemGridDetallePrenda>()
        var tags = ArrayList<ItemGridDetallePrenda>()

        var llave: Boolean = true

        fun newInstance(): DetallePrenda {
            val fragment = DetallePrenda()
            val args = Bundle()
            //args.putInt("imagen", prenda.imagen)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detallePrendaViewModel = ViewModelProvider(this).get(DetallePrendaViewModel::class.java)

        val root =
            inflater.inflate(R.layout.fragment_detalle_prenda, container, false)
        detallePrendaViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        if (HomeFragment.llave) {
            cargarInfo()
            HomeFragment.llave = false

        }

        adaptadorCateforias = GridAdapter(root.context, categorias)

        adaptadorTags = GridAdapter(root.context, tags)

        val gridView: GridView = root.findViewById(R.id.gridCategorias)

        val gridView2: GridView = root.findViewById(R.id.gridTags)

        gridView.adapter = adaptadorCateforias

        gridView2.adapter = adaptadorTags

        val btnRegresar:Button = root.findViewById(R.id.btnRegresarAropero)

        btnRegresar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return root
    }

    fun cargarInfo() {
        categorias.addAll(
            listOf(
                ItemGridDetallePrenda("Casual"),
                ItemGridDetallePrenda("Deportivo"),
                ItemGridDetallePrenda("Formal")
            )
        )

        tags.addAll(
            listOf(
                ItemGridDetallePrenda("chido"),
                ItemGridDetallePrenda("Pa fiesta"),
                ItemGridDetallePrenda("Navidad")
            )
        )

    }


    class GridAdapter(val context: Context, val items: List<ItemGridDetallePrenda>) :
        BaseAdapter() {

        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view =
                convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.categoria_tag, parent, false)
            val txtItem = view.findViewById<TextView>(R.id.txtTagOCategoria)

            txtItem.text = items[position].nombre

            return view
        }
    }
}