package morales.jesus.closetvitual.ui.UC

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import morales.jesus.closetvitual.R
import morales.jesus.closetvitual.ui.Ropero.Ropero.Companion.Outfits
import morales.jesus.closetvitual.ui.Ropero.Ropero.Companion.llave
import morales.jesus.closetvitual.ui.Ropero.Ropero.OutfitAdapter
import morales.jesus.closetvitual.ui.Ropero.RoperoViewModel

class UserConfig : Fragment() {

    private lateinit var userConfigViewModel: UserConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userConfigViewModel = ViewModelProvider(this).get(UserConfigViewModel::class.java)

        val root =
            inflater.inflate(R.layout.fragment_user_config, container, false)

        userConfigViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        val navController = findNavController()

        var btnRegresar:Button = root.findViewById(R.id.btnRegresarHome)


        btnRegresar.setOnClickListener {

            navController.navigate(R.id.navigation_home)
        }

        return root
    }
}



