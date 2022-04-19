package com.demo.android.mapapp.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.demo.android.mapapp.data.Creature
import com.demo.android.mapapp.databinding.FragmentAddCreatureListBinding
import com.demo.android.mapapp.viewmodel.CreaturesViewModel
import java.time.LocalDateTime


/**
 * 生き物をリストに追加するフラグメント
 */
class AddCreatureListFragment : Fragment() {

    // 生き物の情報を管理するViewModel
    private val viewModel: CreaturesViewModel by activityViewModels()

    // バインディングクラス
    private var _binding: FragmentAddCreatureListBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAddCreatureListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View生成後、リスナーを設定
     */
    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addButton.setOnClickListener {

            // 入力データから生き物クラス作成 todo バリデーション
            val name = binding.creatureNameText.text.toString()
            val type = binding.creatureTypeText.text.toString()

            val now = LocalDateTime.now()
            val nextId = viewModel.creatures.value?.size?.plus(1)!!

            // 生き物を追加
            viewModel.addTestInputCreature(
                Creature(
                    id = nextId,
                    type = type,
                    name = name,
                    createdAt = now
                )
            )

            // 生き物リストフラグメントに戻る
            NavHostFragment.findNavController(this@AddCreatureListFragment).navigateUp();
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