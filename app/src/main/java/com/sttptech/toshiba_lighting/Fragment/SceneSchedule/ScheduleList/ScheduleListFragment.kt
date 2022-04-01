package com.sttptech.toshiba_lighting.Fragment.SceneSchedule.ScheduleList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentScheduleListBinding

class ScheduleListFragment : Fragment(), View.OnClickListener {
    
    companion object {
        fun newInstance() = ScheduleListFragment()
    }
    
    private lateinit var vm: ScheduleListViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[ScheduleListViewModel::class.java]
        vm.init()
    }
    
    private lateinit var vb: FragmentScheduleListBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentScheduleListBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListener()
        observerVM()
    }
    
    private fun observerVM() {
    
    }
    
    private fun setListener() {
    
    }
    
    override fun onClick(view: View?) {
    
    }
}