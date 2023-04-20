package com.wit.homegrownapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.R
import com.wit.homegrownapp.adapters.ProductAdapter

abstract class SwipeToAddToBasketCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val addToBasketIcon = ContextCompat.getDrawable(context, R.drawable.baseline_shopping_basket_24)
    private val intrinsicWidth = addToBasketIcon?.intrinsicWidth
    private val intrinsicHeight = addToBasketIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#4CAF50")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (!(viewHolder as ProductAdapter.MainHolder).readOnlyRow) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the green add to basket background
        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        // Calculate position of add to basket icon
        val addToBasketIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
        val addToBasketIconMargin = (itemHeight - intrinsicHeight) / 2
        val addToBasketIconLeft = itemView.right - addToBasketIconMargin - intrinsicWidth!!
        val addToBasketIconRight = itemView.right - addToBasketIconMargin
        val addToBasketIconBottom = addToBasketIconTop + intrinsicHeight

        // Draw the add to basket icon
        addToBasketIcon?.setBounds(addToBasketIconLeft, addToBasketIconTop, addToBasketIconRight, addToBasketIconBottom)
        addToBasketIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
