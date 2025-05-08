package morales.jesus.closetvitual.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var adaptador: AdaptadorConjunto? = null

    companion object {
        var Conjuntos = ArrayList<Prenda>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        cargarConjuntos()

        val navController = findNavController()

        adaptador = AdaptadorConjunto(root.context, Conjuntos)
        val gridView: GridView = root.findViewById(R.id.ContenedorConjuntos)
        gridView.adapter = adaptador

        root.findViewById<Button>(R.id.btnUsuario).setOnClickListener {
            navController.navigate(R.id.navigation_configUsuario)
        }

        root.findViewById<Button>(R.id.btnFiltro).setOnClickListener {
            showPopupMenu(it, root.context)
        }

        return root
    }

    private fun showPopupMenu(view: View, contexto: Context) {
        val popup = PopupMenu(contexto, view)
        popup.menuInflater.inflate(R.menu.menu_filtros, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.filtro_top,
                R.id.filtro_bottom,
                R.id.filtro_bodysuit,
                R.id.filtro_shoes,
                R.id.filtro_accessory -> true

                else -> false
            }
        }
        popup.show()
    }

    private fun cargarConjuntos() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val uid = user.uid

            db.collection("Usuarios")
                .document(uid)
                .collection("prendas")
                .get()
                .addOnSuccessListener { result ->
                    Conjuntos.clear()
                    for (document in result) {
                        val prenda = Prenda(
                            id = document.id,
                            nombre = document.getString("nombre"),
                            tipo = document.getString("tipo"),
                            tag = document.getString("tag"),
                            imagenUrl = document.getString("imagenUrl")
                        )
                        Conjuntos.add(prenda)
                    }
                    adaptador?.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    class AdaptadorConjunto(
        private val contexto: Context,
        private val prendas: ArrayList<Prenda>
    ) : BaseAdapter() {

        override fun getCount(): Int = prendas.size
        override fun getItem(position: Int): Any = prendas[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val prenda = prendas[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.conjunto, null)

            val imgPrenda: ImageView = vista.findViewById(R.id.imgPrenda)
            val txtNombrePrenda: TextView = vista.findViewById(R.id.txtNombrePrenda)
            val barraDeProgreso: ProgressBar = vista.findViewById(R.id.barraDeProgreso)
            val txtNumeroDeUsos: TextView = vista.findViewById(R.id.txtNumeroDeUsosDePrendaxMes)

            Glide.with(contexto)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.leftarrow)
                .into(imgPrenda)

            txtNombrePrenda.text = prenda.nombre ?: "Sin nombre"
            barraDeProgreso.progress = 0
            txtNumeroDeUsos.text = "..."

            contarUsosDePrenda(prenda.nombre ?: "") { usos ->
                barraDeProgreso.progress = usos.coerceAtMost(100)
                txtNumeroDeUsos.text = usos.toString()
            }

            imgPrenda.setOnClickListener {
                val navController = (contexto as AppCompatActivity)
                    .findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.detallePrenda)
            }

            return vista
        }

        private fun contarUsosDePrenda(nombrePrenda: String, callback: (Int) -> Unit) {
            val user = FirebaseAuth.getInstance().currentUser ?: return callback(0)
            val db = FirebaseFirestore.getInstance()

            db.collection("Usuarios")
                .document(user.uid)
                .collection("Outfits")
                .get()
                .addOnSuccessListener { result ->
                    var contador = 0
                    for (document in result) {
                        val prendas = document.get("prendas") as? List<*> ?: continue
                        if (prendas.any { it.toString().equals(nombrePrenda, ignoreCase = true) }) {
                            contador++
                        }
                    }
                    callback(contador)
                }
                .addOnFailureListener {
                    callback(0)
                }
        }
    }
}
