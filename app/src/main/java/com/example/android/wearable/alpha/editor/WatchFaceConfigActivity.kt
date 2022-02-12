package com.example.android.wearable.alpha.editor

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.wearable.alpha.data.watchface.ColorStyleIdAndResourceIds
import com.example.android.wearable.alpha.databinding.ActivityWatchFaceConfigBinding
import com.example.android.wearable.alpha.utils.LONG_BOTTOM_COMPLICATION_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Allows user to edit certain parts of the watch face (color style, ticks displayed, minute arm
 * length) by using the [WatchFaceConfigStateHolder]. (All widgets are disabled until data is loaded.)
 */
class WatchFaceConfigActivity : ComponentActivity() {
    private val stateHolder: WatchFaceConfigStateHolder by lazy {
        WatchFaceConfigStateHolder(
            lifecycleScope,
            this@WatchFaceConfigActivity,
        )
    }

    private lateinit var binding: ActivityWatchFaceConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityWatchFaceConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disable widgets until data loads and values are set.
        binding.colorStylePickerButton.isEnabled = false

        binding.preview.bottomComplication.setOnClickListener { onClickLongBottomComplicationButton(it) }

        lifecycleScope.launch(Dispatchers.Main.immediate) {
            stateHolder.uiState.collect { uiState: WatchFaceConfigStateHolder.EditWatchFaceUiState ->
                when (uiState) {
                    is WatchFaceConfigStateHolder.EditWatchFaceUiState.Loading -> {
                        Log.d(TAG, "StateFlow Loading: ${uiState.message}")
                    }
                    is WatchFaceConfigStateHolder.EditWatchFaceUiState.Success -> {
                        Log.d(TAG, "StateFlow Success.")
                        updateWatchFacePreview(uiState.userStylesAndPreview)
                    }
                    is WatchFaceConfigStateHolder.EditWatchFaceUiState.Error -> {
                        Log.e(TAG, "Flow error: ${uiState.exception}")
                    }
                }
            }
        }
    }

    private fun updateWatchFacePreview(
        userStylesAndPreview: WatchFaceConfigStateHolder.UserStylesAndPreview
    ) {
        Log.d(TAG, "updateWatchFacePreview: $userStylesAndPreview")
        val colorStyleId: String = userStylesAndPreview.colorStyleId
        Log.d(TAG, "\tselected color style: $colorStyleId")
        binding.preview.watchFaceBackground.setImageBitmap(userStylesAndPreview.previewImage)
        enabledWidgets()
    }

    private fun enabledWidgets() {
        binding.colorStylePickerButton.isEnabled = true
    }

    fun onClickColorStylePickerButton(view: View) {
        Log.d(TAG, "onClickColorStylePickerButton() $view")
        // Go to the next color style from the list of color styles
        // TODO: cycling is not accurate... why?
        val currentColorStyle = stateHolder.getColorStyle()
        val colorStyleIdAndResourceIdsList = enumValues<ColorStyleIdAndResourceIds>()
        val currentColorIndex = colorStyleIdAndResourceIdsList.indexOf(currentColorStyle)
        val newColorStyle: ColorStyleIdAndResourceIds = colorStyleIdAndResourceIdsList[(currentColorIndex + 1) % colorStyleIdAndResourceIdsList.size]
        stateHolder.setColorStyle(newColorStyle.id)
    }

    private fun onClickLongBottomComplicationButton(view: View) {
        Log.d(TAG, "onClickBottomComplicationButton() $view")
        stateHolder.setComplication(LONG_BOTTOM_COMPLICATION_ID)
    }

    companion object {
        const val TAG = "WatchFaceConfigActivity"
    }
}
