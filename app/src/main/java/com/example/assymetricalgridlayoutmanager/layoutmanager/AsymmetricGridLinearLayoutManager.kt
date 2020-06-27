package com.example.assymetricalgridlayoutmanager.layoutmanager

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AsymmetricGridLinearLayoutManager : RecyclerView.LayoutManager() {

    private var spanCount: Int = DEFAULT_SPAN_COUNT

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
        var column = 0
        var stroke = 0

        for (position in 0 until state.itemCount) {
            val view: View = recycler.getViewForPosition(position)
            val sector = width / spanCount
            measureChildWithMargins(view, 0, 0)

            val top = paddingTop + getDecoratedMeasuredHeight(view) * stroke
            val bottom = top + getDecoratedMeasuredHeight(view)
            val left = paddingLeft + sector * column
            val rights = left + sector

            addView(view, position)

            layoutDecorated(view, left, top, rights, bottom)
            column++

            if ((position + 1) % spanCount == 0) {
                stroke++
                column = 0
            }

        }
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
            else -> (getDecoratedBottom(lastView) - height).coerceAtMost(dy)
        }
    }

    private fun calculateWhenMoveDown(dy: Int): Int {
        val firstView = getChildAt(0)

        return when {
            firstView == null -> throw NullPointerException("First view in recycler is null")
            getPosition(firstView) > 0 -> dy
            else -> getDecoratedTop(firstView).coerceAtLeast(dy)
        }
    }

    companion object {

        private const val DEFAULT_SPAN_COUNT = 3
    }
}
