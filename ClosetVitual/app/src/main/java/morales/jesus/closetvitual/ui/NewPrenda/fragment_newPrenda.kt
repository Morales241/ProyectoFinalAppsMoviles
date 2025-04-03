package morales.jesus.closetvitual.ui.NewPrenda

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import morales.jesus.closetvitual.R
import android.app.AlertDialog
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback


class fragment_newPrenda : Fragment() {

    val CLOUD_NAME="dzc2lttq5"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "wearIT"
    var imageUri: Uri? = null



    companion object {
        fun newInstance() = fragment_newPrenda()
    }

    private val viewModel: FragmentNewPrendaViewModel by viewModels()

    private lateinit var btnEditarColor: ImageButton
    private lateinit var btnEditarFoto: ImageButton


    private val colores = listOf(
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.CYAN, Color.MAGENTA, Color.BLACK, Color.DKGRAY
    )

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data // <--- Guarda la URI para subirla luego
                imageUri?.let {
                    btnEditarFoto.setImageURI(it)
                }
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

        btnEditarColor.setOnClickListener { mostrarSelectorDeColor() }
        btnEditarFoto.setOnClickListener { abrirGaleria() }
        btnRegistrarPrenda.setOnClickListener { registrarPrenda()

            findNavController().navigate(R.id.navigation_home)
        
        }


        return view
    }

    private fun registrarPrenda() {
        guardarPrenda()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        seleccionarImagen.launch(intent)
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

    fun guardarPrenda(): String {
        var url = ""

        imageUri?.let { uri ->  // Solo sube si imageUri no es null
            MediaManager.get().upload(uri)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d("Cloudinary", "Upload start")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        Log.d("Cloudinary", "Upload progress")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        url = resultData["secure_url"] as? String ?: ""
                        Log.d("Cloudinary", "Upload Success: $url")
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("Cloudinary", "Upload Error: ${error.description}")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.d("Cloudinary", "Upload Rescheduled")
                    }
                }).dispatch()
        } ?: Log.e("Cloudinary", "No image selected")

        return url
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(requireContext(), config);
    }

}
