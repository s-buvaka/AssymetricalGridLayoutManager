package com.example.assymetricalgridlayoutmanager.layoutmanager

/**
 * @author s.buvaka
 */
class SpanInfo(
    val column: Int,
    val row: Int,
    val forceFill: Boolean = false
) {

    companion object {

        fun createSingle() = SpanInfo(1, 1)

        fun createSquare(size: Int) = SpanInfo(size, size)
    }
}
