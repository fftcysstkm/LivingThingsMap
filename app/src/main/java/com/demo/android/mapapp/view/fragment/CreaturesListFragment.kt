package com.demo.android.mapapp.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.android.mapapp.R
import com.demo.android.mapapp.data.Creature
import com.demo.android.mapapp.data.DataSource
import com.demo.android.mapapp.databinding.FragmentCreaturesListBinding
import com.demo.android.mapapp.view.adapter.CreatureAdapter
import java.time.LocalDateTime
import java.util.*


/**
 * 生き物のリストを表示するフラグメント
 */
class CreaturesListFragment : Fragment() {

    private var _binding: FragmentCreaturesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private lateinit var creatureList: List<Creature>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // テスト用の生き物リストをセット
        this.creatureList = DataSource().createCreaturesList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreaturesListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.creatureList
        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                layoutManager.orientation
            )
        )
        // アダプターにデータ、タップで画面遷移リスナーを設定
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val adapter = CreatureAdapter(CreatureAdapter.OnClickListener { creature ->
            val action = CreaturesListFragmentDirections.actionCreaturesListFragmentToMapFragment()
            navController.navigate(action)
        })
        recyclerView.adapter = adapter
        adapter.submitList(this.creatureList)

        // fabでリスト追加するリスナーを登録
        val random = Random()
        val now = LocalDateTime.now()
        val fab = _binding?.addFab?.setOnClickListener {
            val nextCreatures = this.creatureList.toMutableList()
            val nextId = nextCreatures.size + 1
            val position = random.nextInt(creatureList.size)
            nextCreatures.add(position, Creature(id = nextId, type = "魚", "お魚 $nextId", now))
            this.creatureList = nextCreatures
            adapter.submitList(this.creatureList)

        }
    }

    /**
     * フラグメント終了時
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}