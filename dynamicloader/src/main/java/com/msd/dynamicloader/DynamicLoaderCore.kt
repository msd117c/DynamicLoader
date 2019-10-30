package com.msd.dynamicloader

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.msd.dynamicloader.exceptions.LoaderAlreadyInit
import com.msd.dynamicloader.exceptions.LoaderNotInitialized
import com.msd.dynamicloader.ui.LoaderAppCompatActivity
import com.msd.dynamicloader.utils.ThemeUtils

class DynamicLoaderCore {

    companion object {
        private var instance: DynamicLoaderCore? = null
        fun getInstance(): DynamicLoaderCore {
            if (instance == null) {
                instance = DynamicLoaderCore()
            }
            return instance!!
        }
    }

    var activitiesMap = HashMap<String, HashMap<View, RelativeLayout>>()
    lateinit var lifecycleObserver: LifecycleObserver
    private val themeUtils = ThemeUtils()

    @Throws(LoaderNotInitialized::class)
    private fun toggleLoading(
        activity: AppCompatActivity, view: View, disableLoading: Boolean,
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderNotInitialized()
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

    @Throws(LoaderNotInitialized::class)
    private fun toggleLoading(
        activity: AppCompatActivity, view: View, disableLoading: Boolean,
        backgroundColor: Int? = null,
        animationName: String
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderNotInitialized()
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
                animationName
            )
        }
    }

    @Throws(LoaderNotInitialized::class)
    private fun toggleAllLoading(
        activity: AppCompatActivity, disableLoading: Boolean,
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderNotInitialized()
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

    @Throws(LoaderNotInitialized::class)
    private fun toggleAllLoading(
        activity: AppCompatActivity,
        backgroundColor: Int? = null,
        animationName: String
    ) {
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderNotInitialized()
        }
        val loadingMap = activitiesMap[activity::class.java.name]
        if (loadingMap != null) {
            for (entry: Map.Entry<View, RelativeLayout> in loadingMap) {
                val view = entry.key
                val relativeLayout = entry.value
                processView(
                    view,
                    relativeLayout,
                    false,
                    backgroundColor,
                    animationName
                )
            }
        }
    }

    private fun processView(
        view: View,
        relativeLayout: RelativeLayout?,
        disableLoading: Boolean,
        backgroundColor: Int?,
        progressColor: Int?
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
        if (!loading && !disableLoading && backgroundColor != null && progressColor != null) {
            view.post {
                val drawable: Drawable? = view.background?.constantState?.newDrawable()
                relativeLayout?.removeAllViews()
                relativeLayout?.layoutParams = view.layoutParams
                relativeLayout?.layoutParams?.width = view.width
                relativeLayout?.layoutParams?.height = view.height
                if (drawable == null) {
                    relativeLayout?.setBackgroundColor(
                        backgroundColor
                    )
                } else {
                    relativeLayout?.background = drawable
                    if (relativeLayout?.background != null && backgroundColor != Color.TRANSPARENT) {
                        relativeLayout.background.setColorFilter(
                            backgroundColor,
                            PorterDuff.Mode.SRC_IN
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

    private fun processView(
        view: View,
        relativeLayout: RelativeLayout?,
        disableLoading: Boolean,
        backgroundColor: Int?,
        animationName: String
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
        if (!loading && !disableLoading && backgroundColor != null && animationName.isNotEmpty()) {
            view.post {
                val drawable: Drawable? = view.background?.constantState?.newDrawable()
                relativeLayout?.removeAllViews()
                relativeLayout?.layoutParams = view.layoutParams
                relativeLayout?.layoutParams?.width = view.width
                relativeLayout?.layoutParams?.height = view.height
                if (drawable == null) {
                    relativeLayout?.setBackgroundColor(
                        backgroundColor
                    )
                } else {
                    relativeLayout?.background = drawable
                    if (relativeLayout?.background != null && backgroundColor != Color.TRANSPARENT) {
                        relativeLayout.background.setColorFilter(
                            backgroundColor,
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                }
                val animation = LottieAnimationView(view.context)
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                animation.layoutParams = params
                animation.setAnimation(animationName)
                animation.repeatCount = LottieDrawable.INFINITE
                animation.playAnimation()
                relativeLayout?.addView(animation)
                (view.parent as ViewGroup).addView(relativeLayout)
                relativeLayout?.bringToFront()
            }
        }
    }

    @Throws(LoaderAlreadyInit::class)
    fun init(activity: AppCompatActivity, views: Array<View>? = null) {
        if (activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderAlreadyInit(
                if (activity is LoaderAppCompatActivity) {
                    "This activity is extending LoaderAppCompatActivity. Calling init(...) is not needed"
                } else {
                    "This activity was already initialized. You have to call init(...) only once"
                }
            )
        }
        activitiesMap[activity::class.java.name] = HashMap()
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

    fun showLoading(
        activity: AppCompatActivity,
        view: View,
        backgroundColor: Int? = null,
        animationName: String
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val selectedBackgroundColor =
                backgroundColor ?: themeUtils.resolveBackgroundColor(activity)
            toggleLoading(activity, view, false, selectedBackgroundColor, animationName)
        }
    }

    fun showAllLoading(
        activity: AppCompatActivity,
        backgroundColor: Int? = null,
        progressColor: Int? = null
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val backgroundColorInt = try {
                ContextCompat.getColor(activity, backgroundColor!!)
            } catch (e: Exception) {
                backgroundColor
            }
            val progressColorInt = try {
                ContextCompat.getColor(activity, progressColor!!)
            } catch (e: Exception) {
                progressColor
            }
            val selectedBackgroundColor =
                backgroundColorInt ?: themeUtils.resolveBackgroundColor(activity)
            val selectedProgressColor = progressColorInt ?: themeUtils.resolveAccentColor(activity)
            toggleAllLoading(activity, false, selectedBackgroundColor, selectedProgressColor)
        }
    }

    fun showAllLoading(
        activity: AppCompatActivity,
        backgroundColor: Int? = null,
        animationName: String
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val backgroundColorInt = try {
                ContextCompat.getColor(activity, backgroundColor!!)
            } catch (e: Exception) {
                backgroundColor
            }
            val selectedBackgroundColor =
                backgroundColorInt ?: themeUtils.resolveBackgroundColor(activity)
            toggleAllLoading(activity, selectedBackgroundColor, animationName)
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

    fun showLoadingFromResources(
        activity: AppCompatActivity,
        view: View,
        backgroundColor: Int,
        progressColor: Int
    ) {
        val backgroundColorInt = ContextCompat.getColor(activity, backgroundColor)
        val progressColorInt = ContextCompat.getColor(activity, progressColor)
        showLoading(activity, view, backgroundColorInt, progressColorInt)
    }

    fun showLoadingFromResources(
        activity: AppCompatActivity,
        view: View, @ColorRes backgroundColor: Int,
        animationName: String
    ) {
        val backgroundColorInt = ContextCompat.getColor(activity, backgroundColor)
        showLoading(activity, view, backgroundColorInt, animationName)
    }

    fun showAllLoadingFromResources(
        activity: AppCompatActivity,
        @ColorRes backgroundColor: Int,
        @ColorRes progressColor: Int
    ) {
        showAllLoading(
            activity,
            ContextCompat.getColor(activity, backgroundColor),
            ContextCompat.getColor(activity, progressColor)
        )
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