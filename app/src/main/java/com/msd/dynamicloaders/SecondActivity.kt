package com.msd.dynamicloaders

import android.os.Bundle
import android.widget.Button
import com.msd.dynamicloader.ui.LoaderAppCompatActivity

class SecondActivity : LoaderAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)
        setViews(arrayOf(
            findViewById(R.id.text),
            findViewById(R.id.textView),
            findViewById(R.id.textView2)
        ))

        findViewById<Button>(R.id.button).setOnClickListener {
            showAllLoading()
        }

        findViewById<Button>(R.id.button_anim).setOnClickListener {
            showLoading(findViewById(R.id.text), animationName = "laoding.json")
            showLoading(findViewById(R.id.textView), animationName = "loading.json")
            showLoading(findViewById(R.id.textView2), animationName = "laoding.json")
        }

        findViewById<Button>(R.id.button_stop).setOnClickListener {
            dismissAllLoading()
        }
    }

}