package com.demo.android.mapapp.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.android.mapapp.R
import com.demo.android.mapapp.databinding.FragmentCreaturesListBinding
import com.demo.android.mapapp.view.adapter.CreatureAdapter
import com.demo.android.mapapp.viewmodel.CreaturesViewModel


/**
 * 生き物のリストを表示するフラグメント
 */
class CreaturesListFragment : Fragment() {

    // 生き物の情報を管理するViewModel
    private val viewModel: CreaturesViewModel by activityViewModels()

    // バインディングクラス
    private var _binding: FragmentCreaturesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * フラグメント生成時、viewbindingを設定
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreaturesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View生成後、リストにアダプターとリスナーを設定
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // リサイクラービュー設定
        recyclerView = binding.creatureList
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // 罫線不要
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(
//                context,
//                layoutManager.orientation
//            )
//        )

        // 生き物リストにオブザーバ設定、リストタップで画面遷移するリスナーを設定
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val adapter = CreatureAdapter(CreatureAdapter.OnClickListener { creature ->
            val action = CreaturesListFragmentDirections.actionCreaturesListFragmentToMapFragment()
            navController.navigate(action)
        })
        recyclerView.adapter = adapter
        viewModel.creatures.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // fabにリストを追加するリスナーを登録
        val fab = _binding?.addFab?.setOnClickListener {
            viewModel.addTestCreature()
        }
    }

    /**
     * フラグメント終了時にバインディングクラス破棄
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}