package morales.jesus.closetvitual

import android.util.Log
import androidx.lifecycle.ViewModel

class RegistrarNuevoOutfitViewModel : ViewModel() {

    private val prendasSeleccionadas = HashMap<String, MutableList<String>>()

    fun agregarPrenda(tipo: String, id: String): Boolean {
        val tipoNormalizado = tipo.trim().lowercase().replaceFirstChar { it.uppercase() }

        val lista = prendasSeleccionadas[tipoNormalizado]

        return when {
            tipoNormalizado == "Accesorios" -> {
                if (lista == null) {
                    prendasSeleccionadas.put(tipoNormalizado, mutableListOf(id))
                } else if (lista.size < 2) {
                    lista.add(id)
                } else {
                    lista[1] = id
                }
                true
            }

            else -> {

                if (lista == null) {
                    prendasSeleccionadas.put(tipoNormalizado, mutableListOf(id))
                } else {

                    lista[0] = id
                }
                true
            }
        }.also {
            Log.d(
                "OUTFIT_DEBUG",
                "Tipo: $tipoNormalizado, Lista: ${prendasSeleccionadas[tipoNormalizado]}"
            )
            Log.d("OUTFIT_DEBUG", "Prendas actuales: ${obtenerPrendas().size}")
        }
    }

    fun obtenerPrendas(): Map<String, List<String>> {
        return prendasSeleccionadas
    }

    fun limpiar() {
        prendasSeleccionadas.clear()
    }
}
