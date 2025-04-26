package morales.jesus.closetvitual
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResumenOutfitDialogFragment : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_resumen_outfit, null)
        val resumenTextView = view.findViewById<TextView>(R.id.resumenPrendas)
        val prendas = arguments?.getSerializable("prendasSeleccionadas") as? Map<Int, String> ?: emptyMap()

        resumenTextView.text = prendas.values.joinToString("\n") { "- $it" }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Resumen del Outfit")
            .setPositiveButton("Aceptar") { _, _ ->
                guardarOutfitEnFirestore(prendas)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun guardarOutfitEnFirestore(prendas: Map<Int, String>) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val outfitRef = db.collection("Usuarios").document(user.uid).collection("Outfits")
        val nuevoOutfit = hashMapOf(
            "fecha" to System.currentTimeMillis(),
            "prendas" to prendas.values.toList()
        )

        outfitRef.add(nuevoOutfit)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Outfit guardado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }
}