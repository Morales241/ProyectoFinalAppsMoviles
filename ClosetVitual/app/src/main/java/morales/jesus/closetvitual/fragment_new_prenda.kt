package morales.jesus.closetvitual

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import morales.jesus.closetvitual.databinding.FragmentNewPrendaBinding

class FragmentNewPrenda : Fragment() {

    private var _binding: FragmentNewPrendaBinding? = null
    private val binding get() = _binding!!

    private var selectedColor: Int = Color.BLACK // Color por defecto
    private val prenda = NuevaPrenda() // Objeto para almacenar los datos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPrendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupColorPicker()
        setupRegisterButton()
    }

    private fun setupSpinner() {
        val opciones = resources.getStringArray(R.array.tipo_de_prenda_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoPrenda.adapter = adapter

        binding.spinnerTipoPrenda.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prenda.tipoPrenda = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupColorPicker() {
        binding.btnEditarColor.setOnClickListener {
            val coloresNombres = arrayOf("Rojo", "Verde", "Azul", "Amarillo", "Negro")
            val coloresHex = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK)

            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona un color")
                .setItems(coloresNombres) { _, which ->
                    selectedColor = coloresHex[which] // Asignar el color seleccionado
                    prenda.color = selectedColor
                    Toast.makeText(requireContext(), "Color seleccionado: ${coloresNombres[which]}", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
        }
    }

    private fun setupRegisterButton() {
        binding.btnRegistarPrenda.setOnClickListener {
            prenda.nombre = binding.editTextNombre.text.toString()

            val estampadoSeleccionado = binding.radioGroupEstampado.checkedRadioButtonId
            val radioButton = view?.findViewById<RadioButton>(estampadoSeleccionado)
            prenda.estampada = radioButton?.text.toString() == getString(R.string.s)

            Toast.makeText(requireContext(), "Prenda registrada: $prenda", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
