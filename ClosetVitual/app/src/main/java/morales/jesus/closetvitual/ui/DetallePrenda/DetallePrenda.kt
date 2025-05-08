package morales.jesus.closetvitual.ui.detalleprenda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.ui.DetallePrenda.DetallePrendaViewModel
import morales.jesus.closetvitual.ui.DetallePrenda.itemDetallePrenda

class DetallePrenda : Fragment() {

    private lateinit var detallePrendaViewModel: DetallePrendaViewModel

    companion object {
        fun newInstance(): DetallePrenda = DetallePrenda()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_detalle_prenda, container, false)

        detallePrendaViewModel = ViewModelProvider(this)[DetallePrendaViewModel::class.java]

        val listViewTags = root.findViewById<ListView>(R.id.listViewTags)
        val listViewCategorias = root.findViewById<ListView>(R.id.listViewCategorias)
        val btnRegresar = root.findViewById<Button>(R.id.btnRegresarAropero)

        detallePrendaViewModel.prenda.observe(viewLifecycleOwner) { prendaAux ->
            listViewTags.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, prendaAux.tags)
            listViewCategorias.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, prendaAux.categorias)

            Log.d("DetallePrenda", "Tags: ${prendaAux.tags}")
            Log.d("DetallePrenda", "Categorias: ${prendaAux.categorias}")
        }

        btnRegresar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return root
    }

    class GridAdapter(
        private val context: android.content.Context,
        private val items: List<itemDetallePrenda>
    ) : android.widget.BaseAdapter() {

        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.categoria_tag, parent, false)
            val txtItem = view.findViewById<TextView>(R.id.txtTagOCategoria)
            txtItem.text = items[position].nombre
            return view
        }
    }
}
