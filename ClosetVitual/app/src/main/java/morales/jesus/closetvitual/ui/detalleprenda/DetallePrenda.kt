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
import java.util.Calendar

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

    private fun contarUsosRecientesDePrenda(idPrenda: String, callback: (Int) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser ?: return callback(0)
        val db = FirebaseFirestore.getInstance()

        val treintaDiasAtras = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
        }.time

        db.collection("Usuarios")
            .document(user.uid)
            .collection("outfitsUsados")
            .whereGreaterThan("fecha", treintaDiasAtras)
            .get()
            .addOnSuccessListener { result ->
                var contador = 0
                for (document in result) {
                    val prendas = document.get("prendas") as? Map<*, *> ?: continue
                    for ((_, valor) in prendas) {
                        when (valor) {
                            is String -> {
                                if (valor == idPrenda) contador++
                            }

                            is List<*> -> {
                                valor.forEach {
                                    if (it == idPrenda) contador++
                                }
                            }
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
