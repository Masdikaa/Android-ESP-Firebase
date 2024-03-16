package com.masdika.espandroidfirebase.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
class HistoryDrawableUtils {

    companion object {
        fun getDrawableWithColor(context: Context, color: Int, cornerRadius: Float = 20f): Drawable {
            val outerRadii = FloatArray(8) { cornerRadius }
            val roundRectShape = RoundRectShape(outerRadii, null, null)
            val shapeDrawable = ShapeDrawable(roundRectShape)
            shapeDrawable.paint.color = color

            return shapeDrawable
        }
    }

}