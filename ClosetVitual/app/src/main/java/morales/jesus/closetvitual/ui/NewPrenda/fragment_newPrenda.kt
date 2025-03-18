package morales.jesus.closetvitual.ui.NewPrenda

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import morales.jesus.closetvitual.R

class fragment_newPrenda : Fragment() {

    companion object {
        fun newInstance() = fragment_newPrenda()
    }

    private val viewModel: FragmentNewPrendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_fragment_new_prenda, container, false)
    }
}