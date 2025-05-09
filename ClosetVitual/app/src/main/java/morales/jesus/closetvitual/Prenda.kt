package morales.jesus.closetvitual

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prenda(
    val id: String? = null,
    val nombre: String? = null,
    val tipo: String? = null,
    val tags: List<String> = emptyList(),
    val imagenUrl: String? = null,
    val Color: Int? = null
) : Parcelable

