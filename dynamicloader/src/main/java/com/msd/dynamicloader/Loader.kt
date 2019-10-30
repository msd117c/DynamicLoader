package com.msd.dynamicloader

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.msd.dynamicloader.utils.ThemeUtils

class Loader {

    companion object {
        private var instance: Loader? = null
        fun getInstance(): Loader {
            if (instance == null) {
                instance = Loader()
            }
            return instance!!
        }
    }

    var activitiesMap = HashMap<String, HashMap<View, RelativeLayout>>()
    lateinit var lifecycleObserver: LifecycleObserver
    private val themeUtils = ThemeUtils()

    private fun toggleLoading(
        activity: AppCompatActivity, view: View, disableLoading: Boolean,
        backgroundColor: Int = -1,
        progressColor: Int = -1
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            activitiesMap[activity::class.java.name] = HashMap()
        }
        val loadingMap = activitiesMap[activity::class.java.name]
        if (loadingMap != null) {
            if (!loadingMap.containsKey(view) || loadingMap[view] == null) {
                loadingMap[view] = RelativeLayout(view.context)
            }
            processView(
                view,
                loadingMap[view],
                disableLoading,
                backgroundColor,
                progressColor
            )
        }
    }

    private fun toggleAllLoading(
        activity: AppCompatActivity, disableLoading: Boolean,
        backgroundColor: Int = -1,
        progressColor: Int = -1
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            activitiesMap[activity::class.java.name] = HashMap()
        }
        val loadingMap = activitiesMap[activity::class.java.name]
        if (loadingMap != null) {
            for (entry: Map.Entry<View, RelativeLayout> in loadingMap) {
                val view = entry.key
                val relativeLayout = entry.value
                processView(
                    view,
                    relativeLayout,
                    disableLoading,
                    backgroundColor,
                    progressColor
                )
            }
        }
    }

    private fun processView(
        view: View,
        relativeLayout: RelativeLayout?,
        disableLoading: Boolean,
        backgroundColor: Int = -1,
        progressColor: Int = -1
    ) {
        var loading = false
        for (v: View in (view.parent as ViewGroup).children) {
            if (v == relativeLayout) {
                if (disableLoading) {
                    (view.parent as ViewGroup).removeView(v)
                }
                loading = true
            }
        }
        if (!loading && !disableLoading) {
            view.post {
                val drawable: Drawable? = view.background
                relativeLayout?.layoutParams = view.layoutParams
                relativeLayout?.layoutParams?.width = view.width
                relativeLayout?.layoutParams?.height = view.height
                if (drawable == null) {
                    relativeLayout?.setBackgroundColor(
                        if (backgroundColor != -1) {
                            backgroundColor
                        } else {
                            Color.BLACK
                        }
                    )
                } else {
                    relativeLayout?.background = drawable
                    if (backgroundColor != -1 && relativeLayout?.background != null) {
                        DrawableCompat.setTint(
                            relativeLayout.background,
                            backgroundColor
                        )
                    }
                }
                val progressBar = ProgressBar(view.context)
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                progressBar.layoutParams = params
                progressBar.indeterminateDrawable.setColorFilter(
                    progressColor,
                    PorterDuff.Mode.SRC_IN
                )
                relativeLayout?.addView(progressBar)
                (view.parent as ViewGroup).addView(relativeLayout)
                relativeLayout?.bringToFront()
            }
        }
    }

    fun init(activity: AppCompatActivity, views: Array<View>? = null) {
        lifecycleObserver = LoaderLifecycle(activitiesMap, activity::class.java.name)
        activity.lifecycle.addObserver(lifecycleObserver)
        setViews(activity, views)
    }

    fun setViews(activity: AppCompatActivity, views: Array<View>? = null) {
        views?.let { nonNullViews ->
            val hashMap = HashMap<View, RelativeLayout>()
            for (view: View in nonNullViews) {
                if (!hashMap.containsKey(view)) {
                    hashMap[view] = RelativeLayout(activity)
                }
            }
            activitiesMap[activity::class.java.name] = hashMap
        }
    }

    fun showLoading(
        activity: AppCompatActivity,
        view: View,
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val selectedBackgroundColor =
                backgroundColor ?: themeUtils.resolveBackgroundColor(activity)
            val selectedProgressColor = progressColor ?: themeUtils.resolveAccentColor(activity)
            toggleLoading(activity, view, false, selectedBackgroundColor, selectedProgressColor)
        }
    }

    fun showAllLoading(
        activity: AppCompatActivity,
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val selectedBackgroundColor =
                backgroundColor ?: themeUtils.resolveBackgroundColor(activity)
            val selectedProgressColor = progressColor ?: themeUtils.resolveAccentColor(activity)
            toggleAllLoading(activity, false, selectedBackgroundColor, selectedProgressColor)
        }
    }

    fun dismissLoading(activity: AppCompatActivity, view: View) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            toggleLoading(activity, view, true)
        }
    }

    fun dismissAllLoading(activity: AppCompatActivity) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            toggleAllLoading(activity, true)
        }
    }

    fun destroy() {
        activitiesMap.clear()
        instance = null
    }

    internal class LoaderLifecycle(
        private var activitiesMap: HashMap<String, HashMap<View, RelativeLayout>>,
        private var activityName: String
    ) : LifecycleObserver {


        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            if (activitiesMap.containsKey(activityName)) {
                activitiesMap[activityName]?.clear()
                activitiesMap.remove(activityName)
            }
        }

    }
}