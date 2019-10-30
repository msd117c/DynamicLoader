package com.msd.dynamicloaders

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import com.msd.dynamicloader.DynamicLoaderCore
import com.msd.dynamicloader.ui.LoaderAppCompatActivity

class MainActivity : LoaderAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews(
            arrayOf(
                findViewById(R.id.text),
                findViewById(R.id.textView)
            )
        )

        findViewById<Button>(R.id.button).setOnClickListener {
            showAllLoading(Color.RED, Color.parseColor("#BCFF00"))
        }

        findViewById<Button>(R.id.button_stop).setOnClickListener {
            dismissAllLoading()
        }
    }


}
