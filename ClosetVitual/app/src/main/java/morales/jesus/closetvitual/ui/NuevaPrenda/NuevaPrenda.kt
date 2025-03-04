package morales.jesus.closetvitual.ui.NuevaPrenda

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import morales.jesus.closetvitual.R

class NuevaPrenda : Fragment() {

    companion object {
        fun newInstance() = NuevaPrenda()
    }

    private val viewModel: NuevaPrendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_nueva_prenda, container, false)
    }
}