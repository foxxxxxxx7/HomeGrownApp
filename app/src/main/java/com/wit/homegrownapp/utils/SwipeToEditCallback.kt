package com.wit.homegrownapp.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.R
import com.wit.homegrownapp.adapters.ProductAdapter

/* This is a class that extends the ItemTouchHelper.SimpleCallback class. It is used to implement the
swipe to edit functionality. */
abstract class SwipeToEditCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit)
    private val intrinsicWidth = editIcon?.intrinsicWidth
    private val intrinsicHeight = editIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#2196F3")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    /**
     * If the viewHolder is a readOnlyRow, return 0
     *
     * @param recyclerView The RecyclerView to which the ItemTouchHelper is attached to.
     * @param viewHolder The ViewHolder that is being dragged by the user.
     * @return The return value is a bit mask telling the ItemTouchHelper which movement directions are
     * supported.
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        if ((viewHolder as ProductAdapter.MainHolder).readOnlyRow) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    /**
     * Return true if the item was moved to the new adapter position.
     *
     * @param recyclerView The RecyclerView to which the adapter is attached.
     * @param viewHolder The view holder that is being dragged.
     * @param target The target view holder you are switching the original one with.
     * @return Boolean
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * If the user is not swiping, clear the canvas. If the user is swiping, draw the background and
     * the edit icon
     *
     * @param c Canvas - The canvas on which we will draw the background
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder The ViewHolder that is being swiped.
     * @param dX The amount of horizontal displacement caused by user's action
     * @param dY The vertical distance the user has moved the ViewHolder.
     * @param actionState The current state of the item. Is it swiped? Is it dragged? Is it idle?
     * @param isCurrentlyActive Boolean - true if user is still acting on the View.
     * @return The return type is a Boolean.
     */
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
            itemView.left + dX.toInt(),
            itemView.top,
            itemView.left,
            itemView.bottom
        )
        background.draw(c)

        // Calculate position of Edit icon
    /**
     * Clear the canvas by drawing a rectangle with the clearPaint.
     *
     * @param c Canvas? - The canvas to draw on.
     * @param left The left coordinate of the rectangle to clear.
     * @param top The top of the rectangle to be cleared.
     * @param right The right side of the rectangle to clear.
     * @param bottom The bottom position of the rectangle to be cleared.
     */
        val editIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
        val editIconMargin = (itemHeight - intrinsicHeight) / 2
        val editIconLeft = itemView.right - editIconMargin - intrinsicWidth!! - 810
        val editIconRight = itemView.right - editIconMargin - 810
        val editIconBottom = editIconTop + intrinsicHeight

        // Draw the edit icon
        editIcon?.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        editIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}