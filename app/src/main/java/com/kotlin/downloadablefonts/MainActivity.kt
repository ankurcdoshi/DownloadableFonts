package com.kotlin.downloadablefonts

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private lateinit var mHandler: Handler

    lateinit private var mWidthSeekBar: SeekBar
    lateinit private var mWeightSeekBar: SeekBar
    lateinit private var mItalicSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handlerThread = HandlerThread("fonts");
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        initializeSeekBars()
    }

    private fun initializeSeekBars() {
        mWidthSeekBar = findViewById<SeekBar>(R.id.seek_bar_width)
        val widthValue = (100 * WIDTH_DEFAULT.toFloat() / WIDTH_MAX.toFloat()).toInt()
        mWidthSeekBar.progress = widthValue

        val widthTextView = findViewById<TextView>(R.id.textview_width)
        widthTextView.text = widthValue.toString()

        mWidthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                widthTextView.text = progressToWidth(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun progressToWidth(progress: Int) {

    }
}
