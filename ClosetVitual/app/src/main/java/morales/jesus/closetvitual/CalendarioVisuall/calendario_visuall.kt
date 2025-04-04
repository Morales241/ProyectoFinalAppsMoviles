package morales.jesus.closetvitual.CalendarioVisuall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import morales.jesus.closetvitual.CalendarioVisuall.CalendarioVisuallViewModel
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.databinding.FragmentVisualCalendaryBinding
import morales.jesus.closetvitual.Prenda


class calendario_visuall : Fragment() {

    private lateinit var binding: FragmentVisualCalendaryBinding
    private lateinit var adapter: PrendaAdapter
    private val prendasMap: MutableMap<Long, List<Prenda>> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisualCalendaryBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        adapter = PrendaAdapter()
        binding.rvUsedClothes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsedClothes.adapter = adapter

        // Mock de datos
        mockPrendas()

        // Escuchar cambios en la selección del calendario
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = convertirFecha(year, month, dayOfMonth)
            val prendas = prendasMap[fechaSeleccionada] ?: emptyList()
            adapter.actualizarLista(prendas)
        }

        return binding.root
    }

    private fun mockPrendas() {
      //  val prendasDia1 = listOf(
           // Prenda(R.drawable.camisa_roja),
            //Prenda(R.drawable.pans_negro)
       // )

        //val prendasDia2 = listOf(
          //  Prenda(R.drawable.zapatos_cafes),
           // Prenda(R.drawable.gorro_rosa)
       // )

        // Simular prendas por fecha (en formato timestamp)
       // prendasMap[convertirFecha(2025, 2, 6)] = prendasDia1 // 6 de marzo de 2025
        //prendasMap[convertirFecha(2025, 2, 7)] = prendasDia2 // 7 de marzo de 2025
    }

    private fun convertirFecha(year: Int, month: Int, day: Int): Long {
        return (year * 10000 + (month + 1) * 100 + day).toLong() // Formato YYYYMMDD
    }

    // Adapter dentro del fragmento
    inner class PrendaAdapter(private var listaPrendas: List<Prenda> = emptyList()) :
        RecyclerView.Adapter<PrendaAdapter.PrendaViewHolder>() {

        private val nombresPrendas = mapOf(
            R.drawable.camisa_roja to "Camisa Roja",
            R.drawable.pans_negro to "Pans Negro",
            R.drawable.zapatos_cafes to "Zapatos Cafés",
            R.drawable.gorro_rosa to "Gorro Rosa"
        )

        inner class PrendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPrenda: ImageView = view.findViewById(R.id.imgPrenda)
            val txtNombrePrenda: TextView = view.findViewById(R.id.txtNombrePrenda)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_prenda_cv, parent, false)
            return PrendaViewHolder(view)
        }

        override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
            val prenda = listaPrendas[position]
          //  holder.imgPrenda.setImageResource(prenda.imagen)
        //    holder.txtNombrePrenda.text = nombresPrendas[prenda.imagen] ?: "Prenda Desconocida"
        }

        override fun getItemCount(): Int = listaPrendas.size

        fun actualizarLista(nuevaLista: List<Prenda>) {
            listaPrendas = nuevaLista
            notifyDataSetChanged()
        }
    } // Fin de la clase PrendaAdapter
} // Fin de la clase CalendarioVisuallFragment