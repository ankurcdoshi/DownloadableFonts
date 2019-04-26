package com.kotlin.downloadablefonts

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread

class MainActivity : AppCompatActivity() {

    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handlerThread = HandlerThread("fonts");
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        initializeSeekBars()
    }

    private fun initializeSeekBars() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
