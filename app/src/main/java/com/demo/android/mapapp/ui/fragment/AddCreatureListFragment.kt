package com.demo.android.mapapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.demo.android.mapapp.databinding.FragmentAddCreatureListBinding
import com.demo.android.mapapp.viewmodel.CreaturesViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * 生き物をリストに追加するフラグメント
 */
@AndroidEntryPoint
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

            // 入力データ画面で生き物追加

            // 生き物リストフラグメントに戻る
            NavHostFragment.findNavController(this@AddCreatureListFragment).navigateUp();
        }
    }

    /**
     * 入力した生き物情報を保存
     */
    private fun save(){

    }

    /**
     * フラグメント終了時にバインディングクラス破棄
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}