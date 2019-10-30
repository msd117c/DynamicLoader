package com.msd.dynamicloader.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.msd.dynamicloader.DynamicLoaderCore

open class LoaderAppCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicLoaderCore.getInstance().init(this)
    }

    fun setViews(views: Array<View>) {
        DynamicLoaderCore.getInstance().setViews(this, views)
    }

    fun showLoading(
        view: View,
        @ColorInt backgroundColor: Int? = null,
        @ColorInt progressColor: Int? = null
    ) {
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColor, progressColor)
    }

    fun showLoadingFromResources(
        view: View,
        backgroundColor: Int,
        progressColor: Int
    ) {
        val backgroundColorInt = ContextCompat.getColor(this, backgroundColor)
        val progressColorInt = ContextCompat.getColor(this, progressColor)
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColorInt, progressColorInt)
    }

    fun showLoading(view: View, backgroundColor: Int? = null, animationName: String) {
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColor, animationName)
    }

    fun showLoadingFromResources(view: View, @ColorRes backgroundColor: Int, animationName: String) {
        val backgroundColorInt = ContextCompat.getColor(this, backgroundColor)
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColorInt, animationName)
    }

    fun showAllLoadingFromResources(
        @ColorRes backgroundColor: Int,
        @ColorRes progressColor: Int
    ) {
        DynamicLoaderCore.getInstance()
            .showAllLoading(
                this,
                ContextCompat.getColor(this, backgroundColor),
                ContextCompat.getColor(this, progressColor)
            )
    }

    fun showAllLoadingFromResources(
        @ColorRes backgroundColor: Int,
       animationName: String
    ) {
        DynamicLoaderCore.getInstance()
            .showAllLoading(
                this,
                ContextCompat.getColor(this, backgroundColor),
                animationName
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
        DynamicLoaderCore.getInstance()
            .showAllLoading(this, backgroundColorInt, progressColorInt)
    }

    fun showAllLoading(
        backgroundColor: Int? = null,
        animationName: String
    ) {
        val backgroundColorInt = try {
            ContextCompat.getColor(this, backgroundColor!!)
        } catch (e: Exception) {
            backgroundColor
        }
        DynamicLoaderCore.getInstance()
            .showAllLoading(this, backgroundColorInt, animationName)
    }

    fun dismissLoading(view: View) {
        DynamicLoaderCore.getInstance().dismissLoading(this, view)
    }

    fun dismissAllLoading() {
        DynamicLoaderCore.getInstance().dismissAllLoading(this)
    }

}