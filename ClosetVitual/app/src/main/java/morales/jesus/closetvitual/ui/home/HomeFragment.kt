package morales.jesus.closetvitual.ui.home

import android.content.Context
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
        var modoBusqueda = "nombre"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        cargarConjuntos()

        mostrarNombreUsuario(root)

        parentFragmentManager.setFragmentResultListener(
            "prendaGuardada",
            viewLifecycleOwner
        ) { _, _ ->
            cargarConjuntos()
        }

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

        val editText = root.findViewById<EditText>(R.id.txtBuscarPrenda)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val textoIngresado = editText.text.toString()
                if (modoBusqueda == "nombre") {
                    mostrarConisidenciasPorNombre(textoIngresado)
                } else if (modoBusqueda == "tag") {
                    mostrarPorTags(textoIngresado)
                }
                true
            } else {
                false
            }
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

    fun mostrarConisidenciasPorNombre(nombrePrenda: String) {

        if (!nombrePrenda.isEmpty()) {
            cargarConjuntos()
            val resultado =
                Conjuntos.filter { it.nombre?.contains(nombrePrenda, ignoreCase = true) == true }
            adaptador?.let {
                it.prendas.clear()
                it.prendas.addAll(resultado)
                it.notifyDataSetChanged()
            }
        } else {
            cargarConjuntos()
        }
    }

    fun mostrarPorOpcionDeFiltro() {

    }

    fun mostrarPorTags(tagDeRopa: String) {
        val resultado = Conjuntos.filter { it.tag?.contains(tagDeRopa, ignoreCase = true) == true }
        adaptador?.let {
            it.prendas.clear()
            it.prendas.addAll(resultado)
            it.notifyDataSetChanged()
        }
    }

    fun mostrarNombreUsuario(root: View) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val uid = user.uid

            db.collection("Usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { result ->
                    val nombreUsuario: TextView = root.findViewById(R.id.txtNombreUsuario)

                    val nombre = result.getString("nombre") ?: "Usuario desconocido"

                    nombreUsuario.text = nombre

                }
                .addOnFailureListener {
                    // Manejo de error
                    Log.e("Firestore", "Error al obtener nombre de usuario", it)
                }
        }
    }


    class AdaptadorConjunto(
        private val contexto: Context,
        internal val prendas: ArrayList<Prenda>
    ) : BaseAdapter() {

        override fun getCount(): Int = prendas.size
        override fun getItem(position: Int): Any = prendas[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val prenda = prendas[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.conjunto, null)

            val contendor: ConstraintLayout = vista.findViewById(R.id.ContenedorConjuntos)
            val txtNombrePrenda: TextView = vista.findViewById(R.id.txtNombrePrenda)
            val barraDeProgreso: ProgressBar = vista.findViewById(R.id.barraDeProgreso)
            val txtNumeroDeUsos: TextView = vista.findViewById(R.id.txtNumeroDeUsosDePrendaxMes)

            Glide.with(contexto)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.imagenfondo)
                .into(vista.findViewById(R.id.imgPrenda))

            txtNombrePrenda.text = prenda.nombre ?: "Sin nombre"
            barraDeProgreso.progress = 0
            txtNumeroDeUsos.text = "..."

            contarUsosDePrenda(prenda.nombre ?: "") { usos ->
                barraDeProgreso.progress = usos.coerceAtMost(100)
                txtNumeroDeUsos.text = usos.toString()
            }

            contendor.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("prenda", prenda)

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
