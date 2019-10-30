package com.msd.dynamicloader.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.msd.dynamicloader.DynamicLoaderCore

open class LoaderAppCompatActivity : AppCompatActivity(), LoaderAppCompatActivityInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicLoaderCore.getInstance().init(this)
    }

    /**
     * Call this method to set the desired views to make them load after
     * @param views Group of views which you want to be available for loading operations
     */
    override fun setViews(views: Array<View>) {
        DynamicLoaderCore.getInstance().setViews(this, views)
    }

    /**
     * This method creates and add a loading view over selected item. Background and progress color
     * are both optional. By default colorPrimaryDark and colorAccent are taken to draw the view. You
     * can specify one, both of none of them
     * @param view Desired view to show loading over it
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param progressColor If not specified, colorAccent
     */
    override fun showLoading(
        view: View,
        @ColorInt backgroundColor: Int?,
        @ColorInt progressColor: Int?
    ) {
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColor, progressColor)
    }

    /**
     * This method creates and add a loading view over selected item. Background color
     * is optional. By default colorPrimaryDark ais taken to draw the view. You
     * must specify the animation file which you want to use and must be present on assets folder
     * @param view Desired view to show loading over it
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param animationName Animation json file name
     */
    override fun showLoading(view: View, backgroundColor: Int?, animationName: String) {
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColor, animationName)
    }

    /**
     * This method adds loading view for specified item. Background and progress color
     * are mandatory and must exist on colors.xml file. You
     * must specify both of them
     * @param view Desired view to show loading over it
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param progressColor It must be specified and must exist on colors.xml
     */
    override fun showLoadingFromResources(
        view: View,
        backgroundColor: Int,
        progressColor: Int
    ) {
        val backgroundColorInt = ContextCompat.getColor(this, backgroundColor)
        val progressColorInt = ContextCompat.getColor(this, progressColor)
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColorInt, progressColorInt)
    }

    /**
     * This method creates and add a loading view over selected item. You
     * must specify the animation file which you want to use and must be present on assets folder.
     * Background color must be present and must be on colors.xml
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param animationName Animation json file name
     */
    override fun showLoadingFromResources(
        view: View, @ColorRes backgroundColor: Int,
        animationName: String
    ) {
        val backgroundColorInt = ContextCompat.getColor(this, backgroundColor)
        DynamicLoaderCore.getInstance()
            .showLoading(this, view, backgroundColorInt, animationName)
    }

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. Background and progress color
     * are both optional. By default colorPrimaryDark and colorAccent are taken to draw the view. You
     * can specify one, both of none of them
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param progressColor If not specified, colorAccent
     */
    override fun showAllLoading(
        backgroundColor: Int?,
        progressColor: Int?
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

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. You
     * must specify the animation file which you want to use and must be present on assets folder
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param animationName Animation json file name
     */
    override fun showAllLoading(
        backgroundColor: Int?,
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

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. Background and progress color
     * are mandatory and must exist on colors.xml file. You
     * must specify both of them
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param progressColor It must be specified and must exist on colors.xml
     */
    override fun showAllLoadingFromResources(
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

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. You
     * must specify the animation file which you want to use and must be present on assets folder. Background color
     * is mandatory and must exist on colors.xml file. You
     * must specify it
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param animationName Animation json file name
     */
    override fun showAllLoadingFromResources(
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

    /**
     * This method remove loading view over specified item if exists.
     * @param view Desired view to remove it's loading view
     */
    override fun dismissLoading(view: View) {
        DynamicLoaderCore.getInstance().dismissLoading(this, view)
    }

    /**
     * This method is like dismissLoading but it will affect every view added from init or setView methods.
     * Views added making use of showLoading are also included.
     */
    override fun dismissAllLoading() {
        DynamicLoaderCore.getInstance().dismissAllLoading(this)
    }

}

interface LoaderAppCompatActivityInterface {
    fun setViews(views: Array<View>)
    fun showLoading(
        view: View,
        @ColorInt backgroundColor: Int? = null,
        @ColorInt progressColor: Int? = null
    )

    fun showLoading(view: View, backgroundColor: Int? = null, animationName: String)
    fun showLoadingFromResources(
        view: View,
        backgroundColor: Int,
        progressColor: Int
    )

    fun showLoadingFromResources(view: View, @ColorRes backgroundColor: Int, animationName: String)
    fun showAllLoading(
        backgroundColor: Int? = null,
        progressColor: Int? = null
    )

    fun showAllLoading(
        backgroundColor: Int? = null,
        animationName: String
    )

    fun showAllLoadingFromResources(
        @ColorRes backgroundColor: Int,
        @ColorRes progressColor: Int
    )

    fun showAllLoadingFromResources(
        @ColorRes backgroundColor: Int,
        animationName: String
    )

    fun dismissLoading(view: View)
    fun dismissAllLoading()
}