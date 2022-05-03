package com.demo.android.mapapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.demo.android.mapapp.databinding.FragmentAddCreatureListBinding
import com.demo.android.mapapp.viewmodel.add.AddCreatureViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * 生き物をリストに追加するフラグメント
 */
@AndroidEntryPoint
class AddCreatureListFragment : Fragment() {

    // 生き物追加用情報を管理するViewModel
    private val viewModel: AddCreatureViewModel by activityViewModels()

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
//        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
//            if (msg.isEmpty()) return@observe
//
//            Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
//            viewModel.errorMessage.value = ""
//        }
        binding.addButton.setOnClickListener {
            // 入力データで生き物をDB保存
            save()
        }

        // viewModelの保存完了フラグを監視、trueなら生き物リストフラグメントに戻る
//        viewModel.done.observe(viewLifecycleOwner) {
//            findNavController().popBackStack();
//        }
    }

    /**
     * 入力した生き物情報を保存
     */
    private fun save() {
        val creatureType = binding.creatureTypeText.text.toString()
        val creatureName = binding.creatureNameText.text.toString()
        viewModel.save(creatureType, creatureName)
    }

    /**
     * フラグメント終了時にバインディングクラス破棄
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}