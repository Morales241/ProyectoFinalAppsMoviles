package morales.jesus.closetvitual.ui.UC

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserConfigViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is ropero Fragment"
    }
    val text: LiveData<String> = _text
}