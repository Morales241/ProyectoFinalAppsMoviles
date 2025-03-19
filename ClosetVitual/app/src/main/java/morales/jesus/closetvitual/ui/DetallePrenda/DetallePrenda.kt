package morales.jesus.closetvitual.ui.DetallePrenda

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.ui.home.HomeFragment


class DetallePrenda : Fragment() {

    private lateinit var detallePrendaViewModel: DetallePrendaViewModel

    companion object {
        var categorias = ArrayList<String>()
        var tags = ArrayList<String>()

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
        if (llave) {
            cargarInfo()

            llave = false
        }
        val listVtags: ListView = root.findViewById(R.id.listViewTags)
        val adapterTags = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tags)

        val listVcategorias: ListView = root.findViewById(R.id.listViewCategorias)
        val adapterCategorias = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categorias)

        listVtags.adapter = adapterTags
        listVcategorias.adapter = adapterCategorias

        Log.d("DetallePrenda", "Tags: $tags")
        Log.d("DetallePrenda", "Categorias: $categorias")
        Log.d("DetallePrenda", "ListView Tags Adapter Count: ${listVtags.adapter.count}")
        Log.d(
            "DetallePrenda",
            "ListView Categorias Adapter Count: ${listVcategorias.adapter.count}"
        )


        val btnRegresar: Button = root.findViewById(R.id.btnRegresarAropero)

        btnRegresar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return root
    }

    fun cargarInfo() {
        categorias.addAll(
            listOf("Casual", "Deportivo", "Formal")
        )

        tags.addAll(
            listOf("chido", "Pa fiesta", "Navidad")
        )

    }


    class GridAdapter(val context: Context, val items: List<itemDetallePrenda>) :
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