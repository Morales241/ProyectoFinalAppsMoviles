package morales.jesus.closetvitual.ui.detalleprenda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import morales.jesus.closetvitual.Prenda

class DetallePrendaViewModel : ViewModel() {

    private val _prenda = MutableLiveData<Prenda>()
    val prenda: LiveData<Prenda> = _prenda

    fun setPrenda(prenda: Prenda) {
        _prenda.value = prenda
    }
}
