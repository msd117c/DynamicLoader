package com.msd.dynamicloader.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.msd.dynamicloader.Loader

open class LoaderAppCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Loader.getInstance().init(this)
    }

    fun setViews(views: Array<View>) {
        Loader.getInstance().setViews(this, views)
    }

    fun showLoading(
        view: View,
        @ColorInt backgroundColor: Int? = null,
        @ColorInt progressColor: Int? = null
    ) {
        Loader.getInstance()
            .showLoading(this, view, backgroundColor, progressColor)
    }

    fun showLoadingFromResources(
        view: View,
        backgroundColor: Int,
        progressColor: Int
    ) {
        val backgroundColorInt = ContextCompat.getColor(this, backgroundColor)
        val progressColorInt = ContextCompat.getColor(this, progressColor)
        Loader.getInstance()
            .showLoading(this, view, backgroundColorInt, progressColorInt)
    }

    fun showAllLoadingFromResources(
        @ColorRes backgroundColor: Int,
        @ColorRes progressColor: Int
    ) {
        Loader.getInstance()
            .showAllLoading(
                this,
                ContextCompat.getColor(this, backgroundColor),
                ContextCompat.getColor(this, progressColor)
            )
    }

    fun showAllLoading(
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        val backgroundColorInt = try {
            ContextCompat.getColor(this, backgroundColor!!)
        } catch (e: Exception) {
            backgroundColor
        }
        val progressColorInt = try {
            ContextCompat.getColor(this, progressColor!!)
        } catch (e: Exception) {
            progressColor
        }
        Loader.getInstance()
            .showAllLoading(this, backgroundColorInt, progressColorInt)
    }

    fun dismissLoading(view: View) {
        Loader.getInstance().dismissLoading(this, view)
    }

    fun dismissAllLoading() {
        Loader.getInstance().dismissAllLoading(this)
    }

}