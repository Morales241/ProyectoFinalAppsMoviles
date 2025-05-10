package morales.jesus.closetvitual.CalendarioVisuall

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.Prenda
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.databinding.FragmentVisualCalendaryBinding

class calendario_visuall : Fragment() {

    private lateinit var binding: FragmentVisualCalendaryBinding
    private lateinit var adapter: PrendaAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "CalendarioVisuall"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisualCalendaryBinding.inflate(inflater, container, false)

        adapter = PrendaAdapter()
        binding.rvUsedClothes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsedClothes.adapter = adapter

        auth.currentUser?.metadata?.creationTimestamp?.let { timestamp ->
            binding.calendarView.minDate = timestamp
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = convertirFecha(year, month, dayOfMonth)
            Log.d(TAG, "Fecha seleccionada: $fechaSeleccionada")
            cargarOutfitsPorFecha(fechaSeleccionada)
        }

        val hoy = java.util.Calendar.getInstance()
        val year = hoy.get(java.util.Calendar.YEAR)
        val month = hoy.get(java.util.Calendar.MONTH)
        val day = hoy.get(java.util.Calendar.DAY_OF_MONTH)

        val fecha = convertirFecha(year, month, day)

        cargarOutfitsPorFecha(fecha)

        return binding.root
    }

    private fun convertirFecha(year: Int, month: Int, day: Int): Long {
        return (year * 10000 + (month + 1) * 100 + day).toLong()
    }

    private fun cargarOutfitsPorFecha(fecha: Long) {
        val userId = auth.currentUser?.uid ?: return
        val año = (fecha / 10000).toInt()
        val mes = ((fecha % 10000) / 100).toInt()
        val dia = (fecha % 100).toInt()

        Log.d(TAG, "Buscando outfits para: $dia/$mes/$año")

        db.collection("Usuarios")
            .document(userId)
            .collection("outfitsUsados")
            .get()
            .addOnSuccessListener { result ->
                val outfitsEnFecha = result.documents.filter { doc ->
                    val timestamp = doc.getTimestamp("fecha")
                    val fechaDoc = timestamp?.toDate()

                    val coincide = fechaDoc?.let {
                        val calDoc = java.util.Calendar.getInstance().apply { time = it }
                        val calSel = java.util.Calendar.getInstance().apply {
                            set(java.util.Calendar.YEAR, año)
                            set(java.util.Calendar.MONTH, mes - 1)
                            set(java.util.Calendar.DAY_OF_MONTH, dia)
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }

                        calDoc.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        calDoc.set(java.util.Calendar.MINUTE, 0)
                        calDoc.set(java.util.Calendar.SECOND, 0)
                        calDoc.set(java.util.Calendar.MILLISECOND, 0)

                        val ok = calDoc.time == calSel.time
                        Log.d(TAG, "Comparando ${calDoc.time} con ${calSel.time}: $ok")
                        ok
                    } ?: false

                    coincide
                }

                Log.d(TAG, "Outfits encontrados en esa fecha: ${outfitsEnFecha.size}")

                if (outfitsEnFecha.isEmpty()) {
                    adapter.actualizarLista(emptyList())
                    Log.d(TAG, "No hay outfits en esa fecha.")
                    return@addOnSuccessListener
                }

                val prendasIds = outfitsEnFecha.flatMap { doc ->
                    val mapaPrendas = doc.get("prendas") as? Map<*, *>
                    mapaPrendas?.values?.flatMap { lista ->
                        lista as? List<*> ?: emptyList<Any>()
                    }?.mapNotNull { it as? String } ?: emptyList()
                }.distinct()

                Log.d(TAG, "Prendas únicas encontradas: ${prendasIds.size}")

                if (prendasIds.isEmpty()) {
                    adapter.actualizarLista(emptyList())
                    Log.d(TAG, "Outfits sin prendas.")
                    return@addOnSuccessListener
                }

                db.collection("Usuarios")
                    .document(userId)
                    .collection("prendas")
                    .whereIn(FieldPath.documentId(), prendasIds)
                    .get()
                    .addOnSuccessListener { prendasSnapshot ->
                        val listaPrendas = mutableListOf<Prenda>()
                        for (doc in prendasSnapshot.documents) {
                            val nombre = doc.getString("nombre")
                            val tipo = doc.getString("tipoPrenda")
                            val tags = doc.get("tags") as? List<String> ?: emptyList()
                            val imagenUrl = doc.getString("fotoUrl")
                            val color = (doc.get("color") as? Number)?.toInt() ?: Color.GRAY

                            Log.d(TAG, "Prenda cargada: $nombre, URL: $imagenUrl")

                            listaPrendas.add(
                                Prenda(
                                    id = doc.id,
                                    nombre = nombre,
                                    tipo = tipo,
                                    tags = tags,
                                    imagenUrl = imagenUrl,
                                    Color = color
                                )
                            )
                        }
                        adapter.actualizarLista(listaPrendas)
                        Log.d(TAG, "Prendas mostradas: ${listaPrendas.size}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al cargar prendas: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al buscar outfits: ${e.message}")
            }
    }


    inner class PrendaAdapter(private var listaPrendas: List<Prenda> = emptyList()) :
        RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

        inner class PrendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
            val txtNombrePrenda: TextView = view.findViewById(R.id.txtNombrePrenda)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prenda_cv, parent, false)
            return PrendaViewHolder(view)
        }

        override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
            val prenda = listaPrendas[position]
            holder.txtNombrePrenda.text = prenda.nombre ?: "Sin nombre"

            val contendor: View = holder.itemView.findViewById(R.id.contenedorPrendaCalendar)

            Glide.with(holder.itemView.context)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.imagenfondo)
                .into(holder.imgPrenda)

            prenda.Color?.let {
                holder.imgPrenda.setBackgroundColor(it)
            }

            contendor.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("prenda", prenda)

                val navController = (context as AppCompatActivity)
                    .findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.detallePrenda, bundle)
            }

        }

        override fun getItemCount(): Int = listaPrendas.size

        fun actualizarLista(nuevaLista: List<Prenda>) {
            listaPrendas = nuevaLista
            notifyDataSetChanged()
        }
    }
}
