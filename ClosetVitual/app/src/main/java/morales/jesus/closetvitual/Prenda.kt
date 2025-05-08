package morales.jesus.closetvitual

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prenda(
    val id: String? = null,
    val nombre: String? = null,
    val tipo: String? = null,
    val tag: String? = null,
    val imagenUrl: String? = null
):Parcelable
