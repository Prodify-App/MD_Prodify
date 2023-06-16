package com.c23ps105.prodify.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.c23ps105.prodify.R
import com.c23ps105.prodify.utils.cat
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

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.black500)
        txtHintColor = ContextCompat.getColor(context, R.color.black100)
        customBackground =
            ContextCompat.getDrawable(context, R.drawable.custom_edit_text) as Drawable

        when (id) {
            R.id.email -> {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        when {
                            s.isEmpty() -> error = context.getString(R.string.empty_email)
                            !isEmailValid(s) -> error = context.getString(R.string.invalid_email)
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                    }
                })
            }

            R.id.passwords -> {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        when {
                            s.isEmpty() -> error = context.getString(R.string.empty_password)
                            s.length < 8 -> error = context.getString(R.string.invalid_password)
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {

                    }

                })
            }

            R.id.username -> {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        if (s.isEmpty()) error = context.getString(R.string.empty_name)
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                        if (s.isEmpty()) error = context.getString(R.string.empty_name)
                    }
                })
            }
        }
    }
}