package com.example.assymetricalgridlayoutmanager.layoutmanager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// todo add constructors for XML
class AsymmetricGridLinearLayoutManager(private val spanCount: Int = DEFAULT_SPAN_COUNT, private val spanProvider: SpanProvider) :
    RecyclerView.LayoutManager() {

    private var isSquareCell = true
    private var matrix: MutableList<Array<Boolean>> = mutableListOf()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        fillRecycler(state, recycler)
    }

    private fun fillRecycler(
        state: RecyclerView.State,
        recycler: RecyclerView.Recycler
    ) {
        matrix.add(Array(spanCount) { false })
        var column = 0
        var row = 0

        for (position in 0 until state.itemCount) {
            val spanInfo = spanProvider.getSpanOnPosition(position)

            val view: View = recycler.getViewForPosition(position)

            val cellWidth = (width - paddingStart - paddingEnd) / spanCount
            val cellHeight = if (isSquareCell) cellWidth else getDecoratedMeasuredHeight(view)

            val top = paddingTop + cellHeight * row * spanInfo.row
            val bottom = top + cellHeight * spanInfo.row
            val left = paddingLeft + cellWidth * column * spanInfo.column
            val rights = left + cellWidth * spanInfo.column

            measureChildWithDecorationsAndMargin(view, cellWidth * spanInfo.column, cellHeight * spanInfo.row)

            addView(view, position)
            layoutDecorated(view, left, top, rights, bottom)

            column += spanInfo.column
            if ((position + 1) % spanCount == 0) {
                row ++
                column = 0
            }
        }
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

        return when {
            lastView == null -> throw NullPointerException("Last view in recycler is null")
            getPosition(lastView) < itemCount - 1 -> dy
            else -> (getDecoratedBottom(lastView) - height + paddingBottom).coerceAtMost(dy)
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

        private const val DEFAULT_SPAN_COUNT = 4
    }
}
