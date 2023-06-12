package com.c23ps105.prodify.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.c23ps105.prodify.R
import com.c23ps105.prodify.utils.isEmailValid

class ProdifyEditTextView : AppCompatEditText {
    private lateinit var customBackground: Drawable
    private var txtColor: Int = 0
    private var txtHintColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = customBackground
        setTextColor(txtColor)
        setHintTextColor(txtHintColor)
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        super.setError(error, icon)
    }

    private fun isEditTextPassword(textHint: String): Boolean {
        return textHint == context.getString(R.string.hint_password)
    }

    private fun isEditTextName(textHint: String): Boolean {
        return textHint == context.getString(R.string.hint_name)
    }

    private fun isEditTextEmail(textHint: String): Boolean {
        return textHint == context.getString(R.string.hint_email)
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.black500)
        txtHintColor = ContextCompat.getColor(context, R.color.black100)
        customBackground =
            ContextCompat.getDrawable(context, R.drawable.custom_edit_text) as Drawable

        addTextChangedListener(object : TextWatcher {
            var textHint = hint.toString()
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (isEditTextEmail(textHint)) {
                    when {
                        text.isEmpty() -> error = context.getString(R.string.empty_email)
                        !isEmailValid(text) -> error = context.getString(R.string.invalid_email)
                    }
                } else if (isEditTextPassword(textHint)) {
                    when {
                        text.isEmpty() -> error = context.getString(R.string.empty_password)
                        text.length < 6 -> error = context.getString(R.string.invalid_password)
                    }
                } else if (isEditTextName(textHint) && text.isEmpty()) {
                    error = context.getString(R.string.empty_name)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }
}