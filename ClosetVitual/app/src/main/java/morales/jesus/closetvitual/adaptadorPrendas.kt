package morales.jesus.closetvitual

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class adaptadorPrendas(
    private val context: Context,
    private var dataList: List<Prenda>,
    private val onPrendaSelected: (String) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerviewitem, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val prenda = dataList[position]
        Glide.with(context).load(prenda.imagenUrl).into(holder.recImage)
        holder.recTitle.text = prenda.nombre
        holder.recDesc.text = prenda.tipo
        holder.recPriority.text = prenda.tag

        holder.recCard.setOnClickListener {
            onPrendaSelected(prenda.id.toString())
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun searchDataList(searchList: List<Prenda>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recImage: ImageView = itemView.findViewById(R.id.recImage)
    val recTitle: TextView = itemView.findViewById(R.id.recTitle)
    val recDesc: TextView = itemView.findViewById(R.id.recDesc)
    val recPriority: TextView = itemView.findViewById(R.id.recPriority)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
}
