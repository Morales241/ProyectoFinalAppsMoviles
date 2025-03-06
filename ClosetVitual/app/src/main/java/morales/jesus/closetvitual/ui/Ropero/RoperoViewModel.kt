package morales.jesus.closetvitual.ui.Ropero

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RoperoViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is ropero Fragment"
    }
    val text: LiveData<String> = _text
}