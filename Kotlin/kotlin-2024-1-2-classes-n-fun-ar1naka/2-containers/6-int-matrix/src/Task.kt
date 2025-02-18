class IntMatrix(val rows: Int, val columns: Int) {
    init {
        require(rows >= 0 && columns >= 0) { "Rows and columns should be positive!" }
    }

    private val matrix = IntArray(rows * columns)

    operator fun get(row: Int, column: Int): Int {
        require(row in 0..<rows && column in 0..<columns) { "Indexes outside the bounds of the matrix!" }
        return matrix[row * columns + column]
    }

    operator fun set(row: Int, column: Int, value: Int) {
        require(row in 0..<rows && column in 0..<columns) { "Indexes outside the bounds of the matrix!" }
        matrix[row * columns + column] = value
    }
}

fun main() {
    val matrix = IntMatrix(3, 4)
    println(matrix.rows)
    println(matrix.columns)
    println(matrix[0, 0])
    matrix[2, 3] = 42
    println(matrix[2, 3])
}
