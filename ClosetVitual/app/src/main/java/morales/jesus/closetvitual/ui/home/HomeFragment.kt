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
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var adaptador: AdaptadorConjunto? = null

    companion object {
        var Conjuntos = ArrayList<Prenda>()
        var ConjuntosFiltrados = ArrayList<Prenda>()
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
                R.id.filtro_Sombreros -> {
                    mostrarPorOpcionDeFiltro("Sombreros")

                    true
                }

                R.id.filtro_Accesorios -> {
                    mostrarPorOpcionDeFiltro("Accesorios")

                    true
                }

                R.id.filtro_Camisetassimilares -> {
                    mostrarPorOpcionDeFiltro("Camisetas y similares")

                    true
                }

                R.id.filtro_Abrigoschaquetas -> {
                    mostrarPorOpcionDeFiltro("Abrigos y chaquetas")

                    true
                }

                R.id.filtro_pantalonesshorts -> {
                    mostrarPorOpcionDeFiltro("Pantalones y shorts")


                    true
                }

                R.id.filtro_zapatos -> {
                    mostrarPorOpcionDeFiltro("Zapatos")

                    true
                }

                R.id.filtro_bodysuits -> {
                    mostrarPorOpcionDeFiltro("Bodysuits")
                    true
                }

                R.id.filtro_tagPersonalizado -> {

                    if (!modoBusqueda.equals("tag")) {
                        modoBusqueda = "tag"

                        Toast.makeText(
                            contexto,
                            "Modo búsqueda por tag activado",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        modoBusqueda = "nombre"
                        Toast.makeText(
                            contexto,
                            "Modo búsqueda por tag descativado",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    true
                }

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
                    Log.d("Ropero", "Documentos de outfits encontrados: ${result.size()} en la coleccion: prendas")
                    Conjuntos.clear()
                    for (document in result) {
                        val prenda = Prenda(
                            id = document.id,
                            nombre = document.getString("nombre"),
                            tipo = document.getString("tipoPrenda"),
                            tags = document.get("tags") as? List<String> ?: listOf(),
                            imagenUrl = document.getString("fotoUrl"),
                            Color = when (val rawColor = document.get("color")) {
                                is Long -> rawColor.toInt()
                                is String -> rawColor.toIntOrNull()
                                is Int -> rawColor
                                else -> null
                            }
                        )
                        Log.d("prendadesdelabd", prenda.toString())
                        Conjuntos.add(prenda)
                    }

                    ConjuntosFiltrados = ArrayList(Conjuntos)

                    adaptador?.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }

    }

    fun mostrarPorOpcionDeFiltro(tipo: String) {
        Conjuntos = ArrayList(ConjuntosFiltrados)

        val resultado = Conjuntos.filter { prenda ->
            prenda.tipo?.equals(tipo, ignoreCase = true) == true
        }

        adaptador?.let {
            it.prendas.clear()
            it.prendas.addAll(resultado)
            it.notifyDataSetChanged()
        }
    }

    fun mostrarConisidenciasPorNombre(nombrePrenda: String) {
        Conjuntos = ArrayList(ConjuntosFiltrados)

        val resultado = if (nombrePrenda.isBlank()) {
            ConjuntosFiltrados
        } else {
            Conjuntos.filter {
                it.nombre?.contains(nombrePrenda, ignoreCase = true) == true
            }
        }

        adaptador?.let {
            it.prendas.clear()
            it.prendas.addAll(resultado)
            it.notifyDataSetChanged()
        }
    }

    fun mostrarPorTags(tagDeRopa: String) {
        Conjuntos = ArrayList(ConjuntosFiltrados)

        val resultado = if (tagDeRopa.isBlank()) {
            ConjuntosFiltrados
        } else {
            Conjuntos.filter { prenda ->
                prenda.tags.any { tag ->
                    tag.contains(tagDeRopa, ignoreCase = true)
                }
            }
        }

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
            Log.d("DetallePrenda", "tipo: ${prenda.tipo}")
            val inflador = LayoutInflater.from(contexto)
            val vista = inflador.inflate(R.layout.conjunto, null)

            val contendor: ConstraintLayout = vista.findViewById(R.id.ContenedorConjuntos)
            val txtNombrePrenda: TextView = vista.findViewById(R.id.txtNombrePrenda)
            val barraDeProgreso: ProgressBar = vista.findViewById(R.id.barraDeProgreso)

            val txtNumeroDeUsos: TextView = vista.findViewById(R.id.txtNumeroDeUsosDePrendaxMes)

            Log.d("DetallePrenda", "Imagen URL: ${prenda.imagenUrl}")

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
                navController.navigate(R.id.detallePrenda, bundle)
            }

            return vista
        }

        private fun contarUsosDePrenda(idPrenda: String, callback: (Int) -> Unit) {
            val user = FirebaseAuth.getInstance().currentUser ?: return callback(0)
            val db = FirebaseFirestore.getInstance()

            db.collection("Usuarios")
                .document(user.uid)
                .collection("outfitsUsados")
                .get()
                .addOnSuccessListener { result ->
                    var contador = 0
                    for (document in result) {
                        val prendas = document.get("prendas") as? Map<*, *> ?: continue
                        for ((_, valor) in prendas) {
                            if (valor == idPrenda) {
                                contador++
                            }
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
