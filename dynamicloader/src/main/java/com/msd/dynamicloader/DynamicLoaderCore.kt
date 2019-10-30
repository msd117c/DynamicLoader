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
import com.msd.dynamicloader.utils.ThemeUtils

class DynamicLoaderCore {

    companion object {
        private var instance: DynamicLoaderCore? = null
        /**
         * This method return the running instance of DynamicLoaderCore
         * @return DynamicLoaderCore instance
         */
        fun getInstance(): DynamicLoaderCore {
            if (instance == null) {
                instance = DynamicLoaderCore()
            }
            return instance!!
        }
    }

    /**
     * This object will hold all activities and it's views references
     */
    private var activitiesMap = HashMap<String, HashMap<View, RelativeLayout>>()

    private var loadingShowing = HashMap<Pair<String, Int>, RelativeLayout>()

    /**
     * This attribute will be used to handle lifecycle changes
     */
    private lateinit var lifecycleObserver: LifecycleObserver

    /**
     * This object will retrieve any resource from current activity's context
     */
    private val themeUtils = ThemeUtils()

    /**
     * This method initialize the dynamic loader for passed activity
     * It must be called in onCreate method. You can set here the views you want to set loading or
     * do it later.
     * @param activity The current created activity
     * @param views Array of views you want to set loading during activity's lifecycle. It could be not setted here
     */
    @Throws(LoaderAlreadyInit::class)
    fun init(activity: AppCompatActivity, views: Array<View>? = null) {
        /*if (activitiesMap.containsKey(activity::class.java.name)) {
            throw LoaderAlreadyInit(
                if (activity is LoaderAppCompatActivity) {
                    "This activity is extending LoaderAppCompatActivity. Calling init(...) is not needed"
                } else {
                    "This activity was already initialized. You have to call init(...) only once"
                }
            )
        }*/
        if (!activitiesMap.containsKey(activity::class.java.name)) {
            activitiesMap[activity::class.java.name] = HashMap()
        }
        activitiesMap[activity::class.java.name] = HashMap()
        lifecycleObserver = LoaderLifecycle(activitiesMap, activity)
        activity.lifecycle.addObserver(lifecycleObserver)
        setViews(activity, views)
    }

    /**
     * Call this method if you want to overwrite current activity's views to show loading views over them.
     * This method is useful if you have already initialized your activity and you want to set views after
     * something happened, for instance, an asynchronous work
     * @param activity Current activity
     * @param views Array of views to show loading view over them
     */
    fun setViews(activity: AppCompatActivity, views: Array<View>? = null) {
        views?.let { nonNullViews ->
            val hashMap = activitiesMap[activity::class.java.name] ?: HashMap()
            var relativeLayout: RelativeLayout? = null
            for (view: View in nonNullViews) {
                if (loadingShowing.containsKey(Pair(activity::class.java.name, view.id))) {
                    relativeLayout = loadingShowing[Pair(activity::class.java.name, view.id)]
                    (view.parent as ViewGroup).addView(relativeLayout)
                }
                if (!hashMap.containsKey(view)) {
                    hashMap[view] = relativeLayout ?: RelativeLayout(activity)
                }
            }
            activitiesMap[activity::class.java.name] = hashMap
        }
    }

    /**
     * This method creates and add a loading view over selected item. Background and progress color
     * are both optional. By default colorPrimaryDark and colorAccent are taken to draw the view. You
     * can specify one, both of none of them
     * @param activity Current activity
     * @param view Desired view to show loading over it
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param progressColor If not specified, colorAccent
     */
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

    /**
     * This method creates and add a loading view over selected item. Background color
     * is optional. By default colorPrimaryDark ais taken to draw the view. You
     * must specify the animation file which you want to use and must be present on assets folder
     * @param activity Current activity
     * @param view Desired view to show loading over it
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param animationName Animation json file name
     */
    fun showLoading(
        activity: AppCompatActivity,
        view: View,
        backgroundColor: Int? = null,
        animationName: String
    ) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val selectedBackgroundColor =
                backgroundColor ?: themeUtils.resolveBackgroundColor(activity)
            toggleLoading(activity, view, selectedBackgroundColor, animationName)
        }
    }

    /**
     * This method adds loading view for specified item. Background and progress color
     * are mandatory and must exist on colors.xml file. You
     * must specify both of them
     * @param activity Current activity
     * @param view Desired view to show loading over it
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param progressColor It must be specified and must exist on colors.xml
     */
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

    /**
     * This method creates and add a loading view over selected item. You
     * must specify the animation file which you want to use and must be present on assets folder.
     * Background color must be present and must be on colors.xml
     * @param activity Current activity
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param animationName Animation json file name
     */
    fun showLoadingFromResources(
        activity: AppCompatActivity,
        view: View, @ColorRes backgroundColor: Int,
        animationName: String
    ) {
        val backgroundColorInt = ContextCompat.getColor(activity, backgroundColor)
        showLoading(activity, view, backgroundColorInt, animationName)
    }

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. Background and progress color
     * are both optional. By default colorPrimaryDark and colorAccent are taken to draw the view. You
     * can specify one, both of none of them
     * @param activity Current activity
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param progressColor If not specified, colorAccent
     */
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

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. You
     * must specify the animation file which you want to use and must be present on assets folder
     * @param activity Current activity
     * @param backgroundColor If not specified, colorPrimaryDark
     * @param animationName Animation json file name
     */
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

    /**
     * This method adds loading views for each component set on init or setViews methods. Also
     * independent elements added with showLoading are affected by this method. Background and progress color
     * are mandatory and must exist on colors.xml file. You
     * must specify both of them
     * @param activity Current activity
     * @param backgroundColor It must be specified and must exist on colors.xml
     * @param progressColor It must be specified and must exist on colors.xml
     */
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

    /**
     * This method remove loading view over specified item if exists.
     * @param activity Current activity
     * @param view Desired view to remove it's loading view
     */
    fun dismissLoading(activity: AppCompatActivity, view: View) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            toggleLoading(activity, view, true)
        }
    }

    /**
     * This method is like dismissLoading but it will affect every view added from init or setView methods.
     * Views added making use of showLoading are also included.
     * @param activity Current activity
     */
    fun dismissAllLoading(activity: AppCompatActivity) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            toggleAllLoading(activity, true)
        }
    }

    /**
     * This method destroy all data stored on DynamicLoaderCore
     */
    fun destroy() {
        activitiesMap.clear()
        loadingShowing.clear()
        instance = null
    }

    // Private functions //

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
                activity::class.java.name,
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
        activity: AppCompatActivity, view: View,
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
                activity::class.java.name,
                view,
                loadingMap[view],
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
                    activity::class.java.name,
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
                    activity::class.java.name,
                    view,
                    relativeLayout,
                    backgroundColor,
                    animationName
                )
            }
        }
    }

    private fun processView(
        activityName: String,
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
                    loadingShowing.remove(Pair(activityName, view.id))
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
                relativeLayout?.let { nonNullLayout ->
                    loadingShowing[Pair(activityName, view.id)] = nonNullLayout
                }
            }
        }
    }

    private fun processView(
        activityName: String,
        view: View,
        relativeLayout: RelativeLayout?,
        backgroundColor: Int?,
        animationName: String
    ) {
        var loading = false
        for (v: View in (view.parent as ViewGroup).children) {
            if (v == relativeLayout) {
                loading = true
            }
        }
        if (!loading && backgroundColor != null && animationName.isNotEmpty()) {
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
                relativeLayout?.let { nonNullLayout ->
                    loadingShowing[Pair(activityName, view.id)] = nonNullLayout
                }
            }
        }
    }

    internal class LoaderLifecycle(
        private var activitiesMap: HashMap<String, HashMap<View, RelativeLayout>>,
        private var activity: AppCompatActivity
    ) : LifecycleObserver {


        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            val activityName = activity::class.java.name
            if (activitiesMap.containsKey(activityName)) {
                activitiesMap[activityName]?.let { nonNullMap ->
                    for (entry: Map.Entry<View, RelativeLayout> in nonNullMap) {
                        if ((entry.key.parent as ViewGroup).children.contains(entry.value)) {
                            (entry.key.parent as ViewGroup).removeView(entry.value)
                        }
                    }
                }
                if (activity.isFinishing) {
                    activitiesMap[activityName]?.clear()
                    activitiesMap.remove(activityName)

                }
            }
        }

    }
}