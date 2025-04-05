package morales.jesus.closetvitual

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip

class adaptadorPrendas(
    private val context: Context,
    private var dataList: List<Prenda>,
    private val onPrendaSelected: (String) -> Unit
) : RecyclerView.Adapter<adaptadorPrendas.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recImage: ImageView = itemView.findViewById(R.id.recImage)
        val recTitle: TextView = itemView.findViewById(R.id.recTitle)
        val recPriority: TextView = itemView.findViewById(R.id.recPriority)
        val recCard: CardView = itemView.findViewById(R.id.recCard)
        val tagsContainer: LinearLayout = itemView.findViewById(R.id.tagsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerviewitem, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prenda = dataList[position]

        // Cargar imagen
        Glide.with(context)
            .load(prenda.imagenUrl)
            .placeholder(R.color.grisClaro)
            .into(holder.recImage)

        // Configurar nombre y tipo de prenda
        holder.recTitle.text = prenda.nombre ?: "Sin nombre"
        holder.recPriority.text = prenda.tipo ?: "Sin tipo"

        // Limpiar tags anteriores
        holder.tagsContainer.removeAllViews()

        // Procesar tags (asumiendo que vienen como lista o string separado por comas)
        val tags = when {
            prenda.tag.isNullOrEmpty() -> emptyList()
            prenda.tag!!.startsWith("[") && prenda.tag.endsWith("]") -> {
                // Si es una lista en formato string (ej: "[tag1, tag2]")
                prenda.tag.substring(1, prenda.tag.length - 1)
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }
            else -> {
                // Si es un string separado por comas
                prenda.tag.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }
        }

        // Agregar tags como Chips
        tags.forEach { tagText ->
            val chip = Chip(context).apply {
                text = tagText
                isClickable = false
                isCheckable = false
                setTextAppearance(R.style.ChipTextStyle)
                setChipBackgroundColorResource(R.color.chip_background_color)
                setTextColor(ContextCompat.getColor(context, R.color.white))
                chipStrokeWidth = 1f
                setRippleColorResource(R.color.transparent)
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.chip_horizontal_padding),
                    resources.getDimensionPixelSize(R.dimen.chip_vertical_padding),
                    resources.getDimensionPixelSize(R.dimen.chip_horizontal_padding),
                    resources.getDimensionPixelSize(R.dimen.chip_vertical_padding)
                )
            }
            val marginEnd = context.resources.getDimensionPixelSize(R.dimen.chip_margin_end)
            (chip.layoutParams as? LinearLayout.LayoutParams)?.let {
                it.marginEnd = marginEnd
                chip.layoutParams = it
            } ?: run {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = marginEnd
                chip.layoutParams = params
            }
            holder.tagsContainer.addView(chip)
        }

        // Configurar click listener
        holder.recCard.setOnClickListener {
            prenda.id?.let { id -> onPrendaSelected(id) }
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newDataList: List<Prenda>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}