package com.c23ps105.prodify.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.c23ps105.prodify.R


class ProdifyButton : AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 0

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
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
//        textSize = 16f
    }

    private fun init() {
        val text = text.toString()
        if (isRegister(text)) {
            enabledBackground =
                ContextCompat.getDrawable(context, R.drawable.btn_register) as Drawable
            txtColor = ContextCompat.getColor(context, R.color.black600)
        } else if (isLogin(text)) {
            enabledBackground =
                ContextCompat.getDrawable(context, R.drawable.btn_login) as Drawable
            txtColor = ContextCompat.getColor(context, R.color.offwhite)
        } else {
            enabledBackground =
                ContextCompat.getDrawable(context, R.drawable.custom_button_enabled) as Drawable
            txtColor = ContextCompat.getColor(context, R.color.offwhite)
        }

        disabledBackground =
            ContextCompat.getDrawable(context, R.drawable.custom_button_disabled) as Drawable

    }

    private fun isLogin(text: String): Boolean {
        return text == context.getString(R.string.login)
    }

    private fun isRegister(text: String): Boolean {
        return text == context.getString(R.string.register)
    }
}