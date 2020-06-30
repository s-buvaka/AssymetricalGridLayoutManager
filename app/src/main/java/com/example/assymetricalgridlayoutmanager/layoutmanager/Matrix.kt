package com.example.assymetricalgridlayoutmanager.layoutmanager

/**
 * @author s.buvaka
 */
class Matrix(private val width: Int) {

    private val matrix: MutableList<Array<Boolean>> = mutableListOf(Array(width) { false })
    private val cursor: Point = Point(0, 0)

    fun add(desiredSize: Point): Coordinates {
        return if (desiredSize.column + cursor.column <= width && desiredSize.column <= calculateFreeColumn()) {
            addDesiredCell(desiredSize)
        } else if (desiredSize.column == desiredSize.row && calculateFreeColumn() > 1) {
            addDesiredCell(Point(calculateFreeColumn(), calculateFreeColumn()))
        } else {
            addDesiredCell(Point(1, 1))
        }
    }

    private fun addDesiredCell(desiredSize: Point): Coordinates {
        val coordinates = Coordinates(
            left = cursor.column,
            top = cursor.row,
            right = cursor.column + desiredSize.column,
            bottom = cursor.row + desiredSize.row
        )
        addEmptyRowsIfNeed(desiredSize.row)
        for (row in cursor.row until cursor.row + desiredSize.row) {
            for (column in cursor.column until cursor.column + desiredSize.column) {
                matrix[row][column] = true
            }
        }

        calculateCursor()
        return coordinates
    }

    private fun addEmptyRowsIfNeed(desiredSize: Int) {
        while (cursor.row + desiredSize > matrix.size) {
            matrix.add(Array(width) { false })
        }
    }

    private fun calculateCursor() {
        matrix.forEachIndexed row@{ rowIndex, rows ->
            rows.forEachIndexed column@{ columnIndex, isFill ->
                if (!isFill) {
                    cursor.column = columnIndex
                    cursor.row = rowIndex
                    return
                }
            }
        }
    }

    private fun calculateFreeColumn(): Int {
        var size = 0
        for (column in cursor.column until width) {
            val isFill = matrix[cursor.row][column]
            if (isFill) return size else size++
        }
        return size
    }

    class Point(var column: Int, var row: Int)

    data class Coordinates(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    )
}
