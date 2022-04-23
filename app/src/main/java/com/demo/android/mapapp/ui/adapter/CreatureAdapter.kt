package com.demo.android.mapapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.android.mapapp.R
import com.demo.android.mapapp.model.data.Creature

/**
 * 生き物のリスト表示用のAdapter
 */
class CreatureAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Creature, CreatureAdapter.ViewHolder>(callbacks) {

    /**
     * 生き物のリストのビューホルダーを生成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val root = inflater.inflate(R.layout.row_creature, parent, false)
        return ViewHolder(root)
    }

    /**
     * ビューホルダーに値を割り当てる
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ListAdapterではgetItem()メソッドでリストのDataオブジェクトを取得可能
        val creature = getItem(position)
        holder.creatureName.text = creature.creatureName
        holder.itemView.setOnClickListener {
            onClickListener.onClick(creature)
        }
    }

    /**
     * 生き物のリストのビューホルダー
     */
    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val creatureName: TextView = root.findViewById(R.id.creature_name)
    }

    /**
     * リストをタップしたときのリスナー（フラグメントで詳細記述）
     * https://www.section.io/engineering-education/handling-recyclerview-clicks-the-right-way/
     */
    class OnClickListener(val clickListener: (creature: Creature) -> Unit) {
        fun onClick(creature: Creature) = clickListener(creature)
    }

    /**
     * ListAdapterのプリマリコンストラクタに必須なcallback
     * ※companion object：クラス生成時に一緒に生成されるシングルトンオブジェクト
     * ※object: objectのメソッドが呼ばれた際にはじめて生成されるシングルトンオブジェクト
     */
    companion object {
        // ListAdapterのコンストラクタに必要なDiffUtil.ItemCallback型である必要あり
        private val callbacks = object : DiffUtil.ItemCallback<Creature>() {

            // IDが同じかどうか
            override fun areItemsTheSame(oldItem: Creature, newItem: Creature): Boolean {
                return oldItem.creatureId == newItem.creatureId
            }

            // itemが同じオブジェクトかどうか
            override fun areContentsTheSame(oldItem: Creature, newItem: Creature): Boolean {
                return oldItem == newItem
            }
        }
    }
}

