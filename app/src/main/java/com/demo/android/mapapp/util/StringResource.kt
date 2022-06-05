package com.demo.android.mapapp.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes

/**
 * string.xmlから文字列を取得するクラス
 * 参考：https://at-sushi.work/blog/23/
 */
data class StringResource(
    @StringRes private val resId: Int,
    private val params: List<Any> = emptyList()
) {
    companion object {
        fun create(@StringRes resId: Int, vararg params: Any): StringResource {
            return StringResource(resId, listOf(*params))
        }
    }

    fun getString(context: Context): String {
        if (params.isEmpty()) {
            return context.getString(resId)
        }

        return context.getString(resId, *params.toTypedArray())
    }

    @SuppressLint("ResourceType")
    fun getStringList(context: Context): List<String> {
        return context.resources.getStringArray(resId).toList()
    }
}
