package morales.jesus.closetvitual.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import morales.jesus.closetvitual.Conjunto
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var homeViewModel: HomeViewModel
    private var adaptador: conjuntoAdapter? = null

    companion object{

        var Conjuntos = ArrayList<Conjunto>()

        var llave: Boolean = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root =
        inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        if (llave){
            cargarConjuntos()
            llave=false

        }

        adaptador = conjuntoAdapter(root.context, Conjuntos)

        val gridView: GridView = root.findViewById(R.id.gridview)

        gridView.adapter = adaptador

        return root
    }

    fun cargarConjuntos(){

        //ps aquí van a estar los conjuntos jsjs
    }

    private class conjuntoAdapter: BaseAdapter {

        var Conjuntos = ArrayList<Conjunto>()
        var contexto: Context? =null

        constructor(contextoP:Context, conjuntosP:ArrayList<Conjunto>){
            this.Conjuntos = conjuntosP
            this.contexto = contextoP

        }

        override fun getCount(): Int {
            return Conjuntos.size
        }

        override fun getItem(position: Int): Any {
            return Conjuntos.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var conjunto = Conjuntos[position]
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.conjunto, null)
            var imagenBytes = conjunto.imagen
            var bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.size)

            var imagen: ImageView = vista.findViewById(R.id.imgPrenda)
            var Nombre: TextView = vista.findViewById(R.id.txtNombrePrenda)
            var barraProgreso: ProgressBar = vista.findViewById(R.id.barraDeProgreso)
            var numeroUsos:TextView = vista.findViewById(R.id.txtNumeroDeUsosDePrendaxMes)
            var nUsos = conjunto.numeroUsos

            imagen.setImageBitmap(bitmap)
            Nombre.setText(conjunto.nombrePrenda)
            numeroUsos.setText(nUsos.toString())
            barraProgreso.progress = nUsos

            return vista
        }
    }
}