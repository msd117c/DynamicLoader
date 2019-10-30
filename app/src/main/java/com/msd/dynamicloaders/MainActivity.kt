package com.msd.dynamicloaders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.msd.dynamicloader.DynamicLoaderCore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DynamicLoaderCore.getInstance().init(
            this, arrayOf(
                findViewById(R.id.text),
                findViewById(R.id.textView),
                findViewById(R.id.textView2)
            )
        )

        findViewById<Button>(R.id.button).setOnClickListener {
            DynamicLoaderCore.getInstance().showAllLoading(this)
        }

        findViewById<Button>(R.id.button_anim).setOnClickListener {
            DynamicLoaderCore.getInstance().showAllLoading(this, animationName = "loading.json")
        }

        findViewById<Button>(R.id.button_stop).setOnClickListener {
            DynamicLoaderCore.getInstance().dismissAllLoading(this)
        }

        findViewById<Button>(R.id.second).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }


}
