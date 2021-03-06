package com.kotlin.downloadablefonts

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.design.widget.TextInputLayout
import android.support.v4.provider.FontRequest
import android.support.v4.provider.FontsContractCompat
import android.support.v4.util.ArraySet
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //private val TAG = MainActivity::class.simpleName

    companion object {
        private val TAG = "MainActivity"
    }

    private lateinit var mHandler: Handler

    lateinit private var mWidthSeekBar: SeekBar
    lateinit private var mWeightSeekBar: SeekBar
    lateinit private var mItalicSeekBar: SeekBar
    lateinit private var mDownloadableFontTextView: TextView
    lateinit private var mRequestDownloadButton: Button
    lateinit private var mBestEffort: CheckBox

    lateinit private var mFamilyNameSet: ArraySet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handlerThread = HandlerThread("fonts");
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        initializeSeekBars()

        mFamilyNameSet = ArraySet()
        mFamilyNameSet.addAll(Arrays.asList(*resources.getStringArray(R.array.family_names)))

        mDownloadableFontTextView = findViewById(R.id.textview)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.family_names))
        val familyNameInput = findViewById<TextInputLayout>(R.id.auto_complete_family_name_input)
        val autoCompleteFamilyName = findViewById<AutoCompleteTextView>(R.id.auto_complete_family_name)
        autoCompleteFamilyName.setAdapter<ArrayAdapter<String>>(adapter)
        autoCompleteFamilyName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (isValidFamilyName(p0.toString())) {
                    familyNameInput.isErrorEnabled = false
                    familyNameInput.error = ""
                } else {
                    familyNameInput.isErrorEnabled = true
                    familyNameInput.error = getString(R.string.invalid_family_name)
                }
            }
        })

        mRequestDownloadButton = findViewById(R.id.button_request)
        mRequestDownloadButton.setOnClickListener {
            val familyName = autoCompleteFamilyName.text.toString()
            if (!isValidFamilyName(familyName)) {
                familyNameInput.isErrorEnabled = true
                familyNameInput.error = getString(R.string.invalid_family_name)
                Toast.makeText(this@MainActivity, R.string.invalid_input, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requestDownload(familyName)
            mRequestDownloadButton.isEnabled = false
        }
        mBestEffort = findViewById(R.id.checkbox_best_effort)
    }

    private fun requestDownload(familyName: String) {
        val queryBuilder = QueryBuilder(familyName,
                progressToWidth(mWidthSeekBar.progress),
                progressToWeight(mWeightSeekBar.progress),
                progressToItalic(mItalicSeekBar.progress),
                mBestEffort.isChecked)
        val query = queryBuilder.build()

        val request = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                query,
                R.array.com_google_android_gms_fonts_certs)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val callback = object : FontsContractCompat.FontRequestCallback() {
            override fun onTypefaceRetrieved(typeface: Typeface) {
                mDownloadableFontTextView.typeface = typeface
                progressBar.visibility = View.GONE
                mRequestDownloadButton.isEnabled = true
            }

            override fun onTypefaceRequestFailed(reason: Int) {
                Toast.makeText(this@MainActivity,
                        getString(R.string.request_failed, reason), Toast.LENGTH_LONG)
                        .show()
                progressBar.visibility = View.GONE
                mRequestDownloadButton.isEnabled = true
            }
        }
        FontsContractCompat.requestFont(this, request, callback, mHandler)
    }

    private fun isValidFamilyName(familyName: String?) = (familyName != null && mFamilyNameSet.contains(familyName))

    private fun initializeSeekBars() {
        mWidthSeekBar = findViewById(R.id.seek_bar_width)
        val widthValue = (100 * WIDTH_DEFAULT.toFloat() / WIDTH_MAX.toFloat()).toInt()
        mWidthSeekBar.progress = widthValue
        val widthTextView = findViewById<TextView>(R.id.textview_width)
        widthTextView.text = widthValue.toString()
        mWidthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                widthTextView.text = progressToWidth(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mWeightSeekBar = findViewById(R.id.seek_bar_weight)
        val weightValue = WEIGHT_DEFAULT.toFloat() / WEIGHT_MAX.toFloat() * 100
        mWeightSeekBar.progress = weightValue.toInt()
        val weightTextView = findViewById<TextView>(R.id.textview_weight)
        weightTextView.text = WEIGHT_DEFAULT.toString()
        mWeightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                weightTextView.text = progressToWeight(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        mItalicSeekBar = findViewById(R.id.seek_bar_italic)
        mItalicSeekBar.progress = ITALIC_DEFAULT.toInt()
        val italicTextView = findViewById<TextView>(R.id.textview_italic)
        italicTextView.text = ITALIC_DEFAULT.toString()
        mItalicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                italicTextView.text = progressToItalic(progress).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun progressToItalic(progress: Int) = progress.toFloat() / 100f

    private fun progressToWeight(progress: Int) = when (progress) {
            0 -> 1 // The range of the weight is between (0, 1000) (exclusive)
            100 -> WEIGHT_MAX - 1 // The range of the weight is between (0, 1000) (exclusive)
            else -> WEIGHT_MAX * progress / 100
        }

    private fun progressToWidth(progress: Int) = (if (progress == 0) 1 else progress * WIDTH_MAX / 100).toFloat()
}
