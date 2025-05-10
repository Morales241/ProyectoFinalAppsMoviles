package morales.jesus.closetvitual.CalendarioVisuall

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
    private val prendasMap: MutableMap<Long, List<Prenda>> = mutableMapOf()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisualCalendaryBinding.inflate(inflater, container, false)

        adapter = PrendaAdapter()
        binding.rvUsedClothes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsedClothes.adapter = adapter

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = convertirFecha(year, month, dayOfMonth)
            cargarOutfitsPorFecha(fechaSeleccionada)
        }

        return binding.root
    }

    private fun convertirFecha(year: Int, month: Int, day: Int): Long {
        return (year * 10000 + (month + 1) * 100 + day).toLong() // Formato YYYYMMDD
    }

    private fun cargarOutfitsPorFecha(fecha: Long) {
        val userId = auth.currentUser?.uid ?: return
        val año = (fecha / 10000).toInt()
        val mes = ((fecha % 10000) / 100).toInt()
        val dia = (fecha % 100).toInt()

        db.collection("Usuarios")
            .document(userId)
            .collection("outfitsUsados")
            .get()
            .addOnSuccessListener { result ->
                val prendasTotales = mutableListOf<Prenda>()

                val outfitsEnFecha = result.documents.filter { doc ->
                    val timestamp = doc.getTimestamp("fecha")
                    val fechaDoc = timestamp?.toDate()
                    fechaDoc?.let {
                        val cal = java.util.Calendar.getInstance().apply { time = it }
                        cal.get(java.util.Calendar.YEAR) == año &&
                                cal.get(java.util.Calendar.MONTH) + 1 == mes &&
                                cal.get(java.util.Calendar.DAY_OF_MONTH) == dia
                    } ?: false
                }

                if (outfitsEnFecha.isEmpty()) {
                    adapter.actualizarLista(emptyList())
                    return@addOnSuccessListener
                }

                val prendasIds = outfitsEnFecha.flatMap { it.get("prendas") as? List<String> ?: emptyList() }.distinct()

                if (prendasIds.isEmpty()) {
                    adapter.actualizarLista(emptyList())
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
                            val tags = if (doc.get("tags") != null) doc.get("tags") as List<String> else emptyList<String>()
                            val imagenUrl = doc.getString("fotoUrl")
                            val color = if (doc.get("color") != null) (doc.get("color") as Number).toInt() else Color.GRAY

                            listaPrendas.add(Prenda(
                                id = doc.id,
                                nombre = nombre,
                                tipo = tipo,
                                tags = tags,
                                imagenUrl = imagenUrl,
                                Color = color
                            ))
                        }
                        adapter.actualizarLista(listaPrendas)
                    }
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

            Glide.with(holder.itemView.context)
                .load(prenda.imagenUrl)
                .placeholder(R.drawable.placeholder_imagen) // Asegúrate de tener esta imagen
                .into(holder.imgPrenda)

            prenda.Color?.let {
                holder.imgPrenda.setBackgroundColor(it)
            }
        }

        override fun getItemCount(): Int = listaPrendas.size

        fun actualizarLista(nuevaLista: List<Prenda>) {
            listaPrendas = nuevaLista
            notifyDataSetChanged()
        }
    }
}
