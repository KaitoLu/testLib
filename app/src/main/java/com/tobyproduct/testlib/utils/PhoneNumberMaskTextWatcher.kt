package com.tobyproduct.testlib.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.tobyproduct.testlib.R


class PhoneNumberMaskTextWatcher(private val editText: EditText, private val button: View, private val clearOnFocus: Boolean = false) :
    TextWatcher {
    /***
     * 使用方式 宣告完 帶入你的input、開關、是否清除內容的boolean
     * boolean是判斷點擊這個input的時候是否要自動清空裡面的內容
     * Java
     *             {
     *                 watcher = new PhoneNumberMaskTextWatcher(phoneEditText, accountVisibilitySwitch,false);
     *                 accountVisibilitySwitch.setOnClickListener(new View.OnClickListener() {
     *                     @Override
     *                     public void onClick(View v) {
     *                         watcher.toggleVisibility();
     *                     }
     *                 });
     *                 //不能用 input.getText.toString() 會得到遮蔽的號碼，要直接呼叫 watcher.originalPhone
     *                 String phone = watcher.originalPhone
     *             }
     * Kotlin
     *              watcher = PhoneNumberMaskTextWatcher(phoneEditText, accountVisibilitySwitch, false)
     *              accountVisibilitySwitch.setOnClickListener {
     *                     watcher.toggleVisibility()
     *              }
     *              //不能用 input.getText.toString() 會得到遮蔽的號碼，要直接呼叫 watcher.originalPhone
     *              val phone = watcher.originalPhone
     */

    var originalPhone = "" //輸入的內容由這邊獲取   watcher.originalPhone
    private var maskPhone = ""
    private var isAccountVisible = false // 預設關閉遮蔽

    init {
        editText.addTextChangedListener(this)
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            isAccountVisible = hasFocus // 獲得焦點時打開遮蔽，失去焦點時關閉
            if (hasFocus && clearOnFocus) { // 只有在 clearOnFocus 為 true 時才清空內容
                editText.setText("")
            }
            updateEditTextAndButton()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        originalPhone = s.toString()
        updateMaskPhone()
        if (!isAccountVisible) { // 只有在遮蔽狀態下才更新 EditText
            updateEditText()
        }
    }

    fun toggleVisibility() {
        if (!editText.hasFocus()) { // 只有在 EditText沒有焦點時才允許切換
            isAccountVisible = !isAccountVisible
            updateEditTextAndButton()
        }
    }

    private fun updateMaskPhone() {
        maskPhone = if (originalPhone.length == 10) {
            originalPhone.substring(0, 3) + "***" + originalPhone.substring(6)
        } else {
            originalPhone
        }
    }

    private fun updateEditText() {
        editText.removeTextChangedListener(this) // 避免無限迴圈
        editText.setText(if (isAccountVisible) originalPhone else maskPhone)
        editText.setSelection(editText.text.length)
        editText.addTextChangedListener(this)
    }

    private fun updateEditTextAndButton() {
        updateMaskPhone()
        updateEditText()
        updateButtonState()
    }

    private fun updateButtonState() {
        // 切換眼睛開關的icon 也可以在activity寫這段
        button.setBackgroundResource(if (isAccountVisible) R.drawable.ic_password_visibility_24 else R.drawable.ic_password_visibility_off_24)
    }
}