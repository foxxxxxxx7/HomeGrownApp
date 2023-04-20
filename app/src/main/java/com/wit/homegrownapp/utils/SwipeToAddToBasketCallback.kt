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

    private val addToBasketIcon =
        ContextCompat.getDrawable(context, R.drawable.baseline_shopping_basket_24)
    private val intrinsicWidth = addToBasketIcon?.intrinsicWidth
    private val intrinsicHeight = addToBasketIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#4CAF50")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
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
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.left + dX,
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the blue edit background
        background.color = backgroundColor
        background.setBounds(
            itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom
        )
        background.draw(c)

        // Calculate position of Edit icon
        val editIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
        val editIconMargin = (itemHeight - intrinsicHeight) / 2
        val editIconLeft = itemView.right - editIconMargin - intrinsicWidth!! - 810
        val editIconRight = itemView.right - editIconMargin - 810
        val editIconBottom = editIconTop + intrinsicHeight

        // Draw the edit icon
        addToBasketIcon?.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        addToBasketIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}