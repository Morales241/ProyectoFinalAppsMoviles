package morales.jesus.closetvitual.ui.detalleprenda

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R

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
        val vistaColorPrenda1 = root.findViewById<View>(R.id.vistaColorPrenda)
        val vistaColorPrenda2 = root.findViewById<View>(R.id.vistaColorPrenda2)
        val nombre = root.findViewById<TextView>(R.id.txtNombrePrenda)

        val prenda = arguments?.getParcelable<Prenda>("prenda")

        val barraProgresoTotal = root.findViewById<ProgressBar>(R.id.barraDeProgreso)
        val txtProgresoTotal = root.findViewById<TextView>(R.id.txtProgreso)
        val barraProgresoMes = root.findViewById<ProgressBar>(R.id.barraDeProgresoMes)
        val txtProgresoMes = root.findViewById<TextView>(R.id.txtProgresoMes)

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        if (prenda != null) {
            shape.setColor(prenda.Color ?: Color.GRAY)
        }
        vistaColorPrenda1.background = shape
        vistaColorPrenda2.background = shape


        if (prenda != null) {
            detallePrendaViewModel.setPrenda(prenda)
        } else {
            Log.e("DetallePrenda", "No se recibió la prenda correctamente.")
        }

        detallePrendaViewModel.prenda.observe(viewLifecycleOwner) { prenda: Prenda ->
            listViewTags.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, prenda.tags)
            listViewCategorias.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listOf(prenda.tipo ?: "Sin categoría")
            )
            nombre.text = prenda.nombre

            Glide.with(this)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.imagenfondo)
                .into(root.findViewById(R.id.imagenPrenda))


            Log.d("DetallePrenda", "Tags: ${prenda.tags}")
            Log.d("DetallePrenda", "Categorias: ${prenda.tipo}")
        }

        btnRegresar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        detallePrendaViewModel.prenda.observe(viewLifecycleOwner) { prenda: Prenda ->
            listViewTags.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, prenda.tags)

            listViewCategorias.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listOf(prenda.tipo ?: "Sin categoría")
            )
            Log.d("COLOR_DEBUG", "entrando al metodo:")
            prenda.Color?.let { color ->
                Log.d("COLOR_DEBUG", "Aplicando color: $color")
                vistaColorPrenda1.backgroundTintList = ColorStateList.valueOf(color)
                vistaColorPrenda2.backgroundTintList = ColorStateList.valueOf(color)
            }


            contarUsosDePrenda(prenda.nombre ?: "") { usosTotales ->
                barraProgresoTotal.max = 100
                barraProgresoTotal.progress = usosTotales.coerceAtMost(100)
                txtProgresoTotal.text = usosTotales.toString()
            }

            contarUsosRecientesDePrenda(prenda.nombre ?: "") { usosMes ->
                barraProgresoMes.max = 30
                barraProgresoMes.progress = usosMes.coerceAtMost(30)
                txtProgresoMes.text = usosMes.toString()
            }
        }

        return root
    }

    private fun buscarPrendaPorUrlFoto(url: String, callback: (Prenda?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("Usuarios")
                .document(uid)
                .collection("prendas")
                .whereEqualTo("fotoUrl", url)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents[0]
                        val prenda = Prenda(
                            id = document.id,
                            nombre = document.getString("nombre"),
                            tipo = document.getString("tipoPrenda"),
                            tags = document.get("tags") as? List<String> ?: listOf(),
                            imagenUrl = document.getString("fotoUrl"),
                            Color = document.getString("color")?.toIntOrNull()
                        )
                        callback(prenda)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error al buscar la prenda", exception)
                    callback(null)
                }
        } else {
            Log.e("FirebaseAuth", "Usuario no autenticado")
            callback(null)
        }
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

    private fun contarUsosRecientesDePrenda(nombrePrenda: String, callback: (Int) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser ?: return callback(0)
        val db = FirebaseFirestore.getInstance()

        val treintaDiasAtras = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

        db.collection("Usuarios")
            .document(user.uid)
            .collection("Outfits")
            .whereGreaterThan("fecha", treintaDiasAtras)
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
