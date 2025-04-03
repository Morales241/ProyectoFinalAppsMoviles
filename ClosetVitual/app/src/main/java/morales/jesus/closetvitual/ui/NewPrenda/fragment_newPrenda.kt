package morales.jesus.closetvitual.ui.NewPrenda

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import morales.jesus.closetvitual.R

class fragment_newPrenda : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val CLOUD_NAME = "dzc2lttq5"
    private val UPLOAD_PRESET = "wearIT"
    private var imageUri: Uri? = null

    private lateinit var btnEditarColor: ImageButton
    private lateinit var btnEditarFoto: ImageButton
    private lateinit var editTextNombre: EditText
    private lateinit var spinnerTipoPrenda: Spinner
    private lateinit var radioSi: RadioButton

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                imageUri?.let { btnEditarFoto.setImageURI(it) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fragment_new_prenda, container, false)

        initCloudinary()

        btnEditarColor = view.findViewById(R.id.btn_editar_color)
        btnEditarFoto = view.findViewById(R.id.btn_editar_foto)
        val btnRegistrarPrenda: Button = view.findViewById(R.id.btnRegistarPrenda)

        editTextNombre = view.findViewById(R.id.editTextNombre)
        spinnerTipoPrenda = view.findViewById(R.id.spinner_tipo_prenda)
        radioSi = view.findViewById(R.id.radio_si)

        btnEditarColor.setOnClickListener { mostrarSelectorDeColor() }
        btnEditarFoto.setOnClickListener { abrirGaleria() }
        btnRegistrarPrenda.setOnClickListener {
            registrarPrenda()
            findNavController().navigate(R.id.navigation_home)
        }

        return view
    }

    private fun registrarPrenda() {
        guardarPrenda()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        seleccionarImagen.launch(intent)
    }

    fun guardarPrenda() {
        imageUri?.let { uri ->
            MediaManager.get().upload(uri)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val urlImagen = resultData["secure_url"] as? String ?: ""
                        Log.d("Cloudinary", "Upload Success: $urlImagen")
                        guardarPrendaEnFirestore(urlImagen)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("Cloudinary", "Upload Error: ${error.description}")
                        Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }

                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        } ?: run {
            Log.e("Cloudinary", "No image selected")
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarPrendaEnFirestore(urlImagen: String) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            val nombrePrenda = editTextNombre.text.toString().trim()
            val tipoPrenda = spinnerTipoPrenda.selectedItem.toString()
            val colorSeleccionado = btnEditarColor.tag?.toString() ?: "Sin color"
            val estampado = radioSi.isChecked

            if (nombrePrenda.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return
            }

            val prenda = hashMapOf(
                "nombre" to nombrePrenda,
                "tipoPrenda" to tipoPrenda,
                "tags" to listOf("moda", "casual"),
                "color" to colorSeleccionado,
                "estampado" to estampado,
                "fotoUrl" to urlImagen
            )

            db.collection("Usuarios").document(userId).collection("prendas")
                .add(prenda)
                .addOnSuccessListener {
                    Log.d("Firestore", "Prenda guardada exitosamente")
                    Toast.makeText(requireContext(), "Prenda guardada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al guardar la prenda", e)
                    Toast.makeText(requireContext(), "Error al guardar la prenda", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("FirebaseAuth", "Usuario no autenticado")
            Toast.makeText(requireContext(), "Debes iniciar sesión para guardar prendas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(requireContext(), config)
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

        val colores = listOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, Color.BLACK, Color.DKGRAY
        )

        colores.forEach { color ->
            val colorView = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(100, 100)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                setOnClickListener {
                    btnEditarColor.setColorFilter(color)
                    btnEditarColor.tag = color  // Se almacena el color seleccionado
                    alertDialog.dismiss()
                }
            }

            val params = ViewGroup.MarginLayoutParams(colorView.layoutParams).apply {
                setMargins(16, 16, 16, 16)
            }

            gridLayout.addView(colorView, params)
        }

        builder.setView(gridLayout)
        alertDialog.setView(gridLayout)
        alertDialog.show()
    }


}
