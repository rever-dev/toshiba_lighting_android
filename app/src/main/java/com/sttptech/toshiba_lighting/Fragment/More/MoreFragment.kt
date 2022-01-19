package com.sttptech.toshiba_lighting.Fragment.More

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sttptech.toshiba_lighting.Activity.Main.MainActivity
import com.sttptech.toshiba_lighting.AppUtil.AppInfo
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {

    private lateinit var vm: MoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[MoreViewModel::class.java]
    }

    private var _vb: FragmentMoreBinding? = null
    private val vb: FragmentMoreBinding get() = _vb!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vb = FragmentMoreBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val version = getString(R.string.currentVersion) + vm.version.value
        val account = getString(R.string.signAccount) + vm.account.value
        vb.fragMoreTvVersion.text = version
        vb.fragMoreTvAccount.text = account
    }

}