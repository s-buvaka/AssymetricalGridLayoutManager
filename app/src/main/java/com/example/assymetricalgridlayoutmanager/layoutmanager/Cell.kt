package com.example.assymetricalgridlayoutmanager.layoutmanager

/**
 * @author s.buvaka
 */
class Cell(
    val column: Int,
    val row: Int
) {

    companion object {

        fun createSingle() = Cell(1, 1)

        fun createSquare(size: Int) = Cell(size, size)
    }
}
