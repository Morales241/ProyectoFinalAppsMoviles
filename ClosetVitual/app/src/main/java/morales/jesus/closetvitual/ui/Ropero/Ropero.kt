package morales.jesus.closetvitual.ui.Ropero

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import morales.jesus.closetvitual.R

class Ropero : Fragment() {

    companion object {
        fun newInstance() = Ropero()
    }

    private val viewModel: RoperoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_ropero, container, false)
    }
}