package morales.jesus.closetvitual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.setFragmentResultListener

class fragment_registrar_outfit : Fragment() {

    private val selectedPrendas = mutableMapOf<Int, String>()
    private var currentBtnId: Int = -1
    private lateinit var buttonIds: List<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_outfit, container, false)

        buttonIds = listOf(
            R.id.btnprenda1, R.id.btnprenda2, R.id.btnprenda3,
            R.id.btnprenda4, R.id.btnprenda5, R.id.btnprenda6, R.id.btnprenda7
        )

        buttonIds.forEach { id ->
            val button = view.findViewById<ImageButton>(id)
            button.setOnClickListener {
                currentBtnId = id
                val bundle = Bundle().apply {
                    putInt("btnPrendaId", id)
                }
                parentFragmentManager.setFragmentResult("solicitudSeleccionPrenda", bundle)
                findNavController().navigate(R.id.action_registrarOutfit_to_elegirRopaOutfit)
            }
        }

        parentFragmentManager.setFragmentResultListener("resultadoSeleccionPrenda", this) { _, bundle ->
            val nombre = bundle.getString("nombrePrenda")
            val imageResId = bundle.getInt("imagenDrawableRes")

            if (nombre != null) {
                selectedPrendas[currentBtnId] = nombre
            }

            buttonIds.forEach { id ->
                val button = view.findViewById<ImageButton>(id)
                if (selectedPrendas.containsKey(id)) {
                    button.setImageResource(R.drawable.logoseleccionado)
                }
            }
        }

        val btnRegistrarUso = view.findViewById<Button>(R.id.btnRegistrarUso)
        btnRegistrarUso.setOnClickListener {
            if (selectedPrendas.isNotEmpty()) {
                val resumenDialog = ResumenOutfitDialogFragment()
                val bundle = Bundle().apply {
                    putSerializable("prendasSeleccionadas", HashMap(selectedPrendas))
                }
                resumenDialog.arguments = bundle
                resumenDialog.show(parentFragmentManager, "ResumenOutfit")
            }
        }

        return view
    }
}
