package com.masdika.espandroidfirebase.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
class HistoryDrawableUtils {

    companion object {
        fun getDrawableWithColor(context: Context, color: Int): Drawable {
            val shape = ShapeDrawable()
            shape.paint.color = color

            return shape
        }
    }

}