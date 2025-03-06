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

    private lateinit var homeViewModel: HomeViewModel
    private var adaptador: AdaptadorConjunto? = null

    companion object {
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

        if (llave) {
            cargarConjuntos()
            llave = false

        }

        adaptador = AdaptadorConjunto(root.context, Conjuntos)

        val gridView: GridView = root.findViewById(R.id.ContenedorConjuntos)

        gridView.adapter = adaptador

        return root
    }

    fun cargarConjuntos() {

        Conjuntos.add(Conjunto(R.drawable.camisa_roja, "Camisa Roja", 10))
        Conjuntos.add(Conjunto(R.drawable.pans_negro, "Pans Negro", 5))
        Conjuntos.add(Conjunto(R.drawable.zapatos_cafes, "Zapatos cafes", 30))
        Conjuntos.add(Conjunto(R.drawable.gorro_rosa, "Gorro rosa", 6))
    }

    class AdaptadorConjunto(
        private val contexto: Context,
        private val Conjuntos: ArrayList<Conjunto>
    ) : BaseAdapter() {

        override fun getCount(): Int {
            return Conjuntos.size
        }

        override fun getItem(position: Int): Any {
            return Conjuntos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val Conjunto = Conjuntos[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.conjunto, null)

            val imgPrenda: ImageView = vista.findViewById(R.id.imgPrenda)
            val txtNombrePrenda: TextView = vista.findViewById(R.id.txtNombrePrenda)
            val barraDeProgreso: ProgressBar = vista.findViewById(R.id.barraDeProgreso)
            val txtNumeroDeUsos: TextView = vista.findViewById(R.id.txtNumeroDeUsosDePrendaxMes)

            imgPrenda.setImageResource(Conjunto.img)
            txtNombrePrenda.text = Conjunto.nombrePrenda
            barraDeProgreso.progress = Conjunto.numeroUsos
            txtNumeroDeUsos.text = Conjunto.numeroUsos.toString()

            return vista
        }
    }
}