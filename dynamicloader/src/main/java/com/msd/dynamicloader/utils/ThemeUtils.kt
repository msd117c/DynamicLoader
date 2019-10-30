package com.msd.dynamicloader.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.TypedValue
import com.msd.dynamicloader.R


class ThemeUtils {

    @TargetApi(LOLLIPOP)
    fun resolveBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()

        val resourcesArray = context.obtainStyledAttributes(
            typedValue.data,
            intArrayOf(R.attr.colorPrimaryDark)
        )
        val backgroundColor = resourcesArray.getColor(0, 0)

        resourcesArray.recycle()

        return backgroundColor
    }

    @TargetApi(LOLLIPOP)
    fun resolveAccentColor(context: Context): Int {
        val typedValue = TypedValue()

        val resourcesArray = context.obtainStyledAttributes(
            typedValue.data,
            intArrayOf(R.attr.colorAccent)
        )
        val colorAccent = resourcesArray.getColor(0, 0)

        resourcesArray.recycle()

        return colorAccent
    }

}