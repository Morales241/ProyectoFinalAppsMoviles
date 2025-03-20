package morales.jesus.closetvitual.ui.NewPrenda

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import morales.jesus.closetvitual.R

class fragment_newPrenda : Fragment() {

    companion object {
        fun newInstance() = fragment_newPrenda()
    }

    private val viewModel: FragmentNewPrendaViewModel by viewModels()

    private lateinit var btnEditarColor: ImageButton

    private val colores = listOf(
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.CYAN, Color.MAGENTA, Color.BLACK, Color.DKGRAY
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fragment_new_prenda, container, false)

        btnEditarColor = view.findViewById(R.id.btn_editar_color)

        btnEditarColor.setOnClickListener {
            mostrarSelectorDeColor()
        }

        return view
    }

    private fun mostrarSelectorDeColor() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona un color")

        val gridLayout = GridLayout(requireContext()).apply {
            rowCount = 2
            columnCount = 4
            setPadding(32, 32, 32, 32)
        }

        val alertDialog = builder.create()

        colores.forEach { color ->
            val colorView = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(100, 100)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                setOnClickListener {
                    btnEditarColor.setColorFilter(color)
                    alertDialog.dismiss()
                }
            }

            val params = ViewGroup.MarginLayoutParams(colorView.layoutParams).apply {
                setMargins(16, 16, 16, 16)
            }

            gridLayout.addView(colorView, params)
        }

        alertDialog.setView(gridLayout)
        alertDialog.show()
    }
}
