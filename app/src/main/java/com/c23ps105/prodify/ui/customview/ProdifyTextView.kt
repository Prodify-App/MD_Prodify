package com.c23ps105.prodify.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.c23ps105.prodify.R


class ProdifyTextView : AppCompatTextView {
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setTextColor(txtColor)
    }

    private fun init() {
        val idName = resources.getResourceEntryName(id)
        txtColor = if (idName.endsWith("_white")) ContextCompat.getColor(
            context,
            R.color.white_pure
        ) else if (idName.endsWith("_middle")) {
            ContextCompat.getColor(
                context,
                R.color.black200
            )
        } else {
            ContextCompat.getColor(context, R.color.black500)
        }
    }
}