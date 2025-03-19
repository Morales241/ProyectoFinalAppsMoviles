package morales.jesus.closetvitual.CalendarioVisuall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import morales.jesus.closetvitual.CalendarioVisuall.CalendarioVisuallViewModel
import morales.jesus.closetvitual.R

class calendario_visuall : Fragment() {

    companion object {
        fun newInstance() = calendario_visuall()
    }

    private val viewModel: CalendarioVisuallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendario_visuall, container, false)
    }
}