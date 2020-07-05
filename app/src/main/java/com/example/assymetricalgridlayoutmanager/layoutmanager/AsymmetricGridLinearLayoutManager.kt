package com.example.assymetricalgridlayoutmanager.layoutmanager

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("MemberVisibilityCanBePrivate")
class AsymmetricGridLinearLayoutManager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RecyclerView.LayoutManager() {

    constructor(context: Context, spanCount: Int, spanProvider: SpanProvider) : this(context) {
        this.spanCount = spanCount
        this.spanProvider = spanProvider
    }

    var isSquareCell = true
    var spanProvider: SpanProvider? = null
    var spanCount: Int = DEFAULT_SPAN_COUNT
        set(value) {
            if (value == spanCount) return

            require(spanCount >= 1) { "Span count should be at least 1. Provided $spanCount" }
            field = value
            matrix = Matrix(value)
            requestLayout()
        }

    private var matrix = Matrix(spanCount)
    private var lowestView: ViewWrapper? = null

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        fillRecycler(state, recycler)
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrollDistance = calculateScrollDistance(dy)
        offsetChildrenVertical(-scrollDistance)
        return scrollDistance
    }

    private fun fillRecycler(
        state: RecyclerView.State,
        recycler: RecyclerView.Recycler
    ) {
        for (position in 0 until state.itemCount) {
            val spanInfo = spanProvider?.getSpanOnPosition(position)
                ?: throw IllegalStateException("SpanProvider is not exist. Set span provider for correct working LayoutManager")
            val coordinates = matrix.add(Matrix.Point(spanInfo.column, spanInfo.row))

            val view: View = recycler.getViewForPosition(position)
            measureChildWithMargins(view, 0, 0)

            val cellWidth = (width - paddingStart - paddingEnd) / spanCount
            val cellHeight = calculateCellHeight(cellWidth, view)

            val top = paddingTop + cellHeight * coordinates.top
            val bottom = paddingBottom + cellHeight * coordinates.bottom
            val left = paddingLeft + cellWidth * coordinates.left
            val right = paddingRight + cellWidth * coordinates.right

            measureChildWithDecorationsAndMargin(view, right - left, bottom - top)

            addView(view, position)
            layoutDecorated(view, left, top, right, bottom)
            checkLowestView(view, bottom, right)
        }
    }

    private fun calculateCellHeight(cellWidth: Int, view: View): Int =
        if (isSquareCell) {
            cellWidth
        } else {
            val percent: Float = getDecoratedMeasuredHeight(view).toFloat() / getDecoratedMeasuredWidth(view).toFloat()
            (percent * cellWidth).toInt()
        }

    private fun measureChildWithDecorationsAndMargin(
        child: View,
        cellWidth: Int,
        cellHeight: Int
    ) {
        var widthSpec = View.MeasureSpec.makeMeasureSpec(cellWidth, View.MeasureSpec.EXACTLY)
        var heightSpec = View.MeasureSpec.makeMeasureSpec(cellHeight, View.MeasureSpec.EXACTLY)

        val decorRect = Rect()
        calculateItemDecorationsForChild(child, decorRect)
        val params = child.layoutParams as RecyclerView.LayoutParams
        widthSpec = updateSpecWithExtra(
            widthSpec, params.leftMargin + decorRect.left,
            params.rightMargin + decorRect.right
        )
        heightSpec = updateSpecWithExtra(
            heightSpec, params.topMargin + decorRect.top,
            params.bottomMargin + decorRect.bottom
        )
        child.measure(widthSpec, heightSpec)
    }

    private fun updateSpecWithExtra(spec: Int, startInset: Int, endInset: Int): Int {
        if (startInset == 0 && endInset == 0) return spec

        val mode = View.MeasureSpec.getMode(spec)
        return if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(spec) - startInset - endInset, mode)
        } else spec
    }

    private fun checkLowestView(view: View, bottom: Int, right: Int) {
        lowestView?.let {
            if (it.bottom < bottom) {
                lowestView = ViewWrapper(view, bottom, right)
            }
        } ?: run { lowestView = ViewWrapper(view, bottom, right) }
    }


    private fun calculateScrollDistance(dy: Int): Int {
        val moveUp = dy > 0
        val moveDown = dy < 0
        return when {
            childCount == 0 -> 0
            allViewFitOnScreen() -> 0
            moveUp -> calculateWhenMoveUp(dy)
            moveDown -> calculateWhenMoveDown(dy)
            else -> 0
        }
    }

    /**
     * Check if all views fit on screen. We don't need scroll recycler when all view fit on screen.
     */
    private fun allViewFitOnScreen(): Boolean {
        val firstView = getChildAt(0)
        val lastView = getChildAt(childCount - 1)

        return when {
            firstView == null -> throw NullPointerException("First view in recycler is null")
            lastView == null -> throw NullPointerException("Last view in recycler is null")
            else -> getDecoratedBottom(lastView) - getDecoratedTop(firstView) <= height
        }
    }

    private fun calculateWhenMoveUp(dy: Int): Int {
        val lastView = getChildAt(childCount - 1)
        val lowest = lowestView?.view

        return when {
            lastView == null || lowest == null -> throw NullPointerException("Last view in recycler is null")
            getPosition(lastView) < itemCount - 1 -> dy
            else -> (getDecoratedBottom(lowest) - height + paddingBottom).coerceAtMost(dy)
        }
    }

    private fun calculateWhenMoveDown(dy: Int): Int {
        val firstView = getChildAt(0)

        return when {
            firstView == null -> throw NullPointerException("First view in recycler is null")
            getPosition(firstView) > 0 -> dy
            else -> (getDecoratedTop(firstView) - paddingTop).coerceAtLeast(dy)
        }
    }

    companion object {

        private const val DEFAULT_SPAN_COUNT = 2
    }
}
