package connectfour

// enums to access boardDimensions list elements
private const val ROW_AMOUNT = 0
private const val COLUMN_AMOUNT = 1

// for diagonals checking
private const val FIRST = 1
private const val SECOND = 2

class Game(
    private val firstPlayer: String, private val secondPlayer: String, private val boardDimensions: List<Int>
) {

    fun starGame() {
        try {
            defineHowManyGamesWillBePlayed()
            repeat(numberOfGames) {
                printIntro()
                printBoard()
                gameCycle()
                clearBoard()
            }
        } catch (e: Exception) {
            // an exception will be thrown when a user enters "end"
        }
        println("Game over!")
    }

    private fun clearBoard() {
        board = MutableList<MutableList<String>> (boardDimensions[ROW_AMOUNT]) {
            MutableList<String> (boardDimensions[COLUMN_AMOUNT]) { " " }
        }
    }

    private fun defineHowManyGamesWillBePlayed() {
        while (true) {
            when (val answer: Int = askForGameTimes()) {
                -1 -> continue
                else -> {
                    numberOfGames = answer
                    return
                }
            }
        }
    }

    companion object {

        enum class GameStatus {
            WIN, DRAW, CONTINUE
        }
    }

    /**
     * Defines who should make a move
     */
    private var isFirstPlayerTurn = true

    private var score = mutableListOf<Int>(0, 0)

    /**
     * Represents the board with given dimensions
     */
    private var board = MutableList<MutableList<String>>(boardDimensions[ROW_AMOUNT]) {
        MutableList<String>(boardDimensions[COLUMN_AMOUNT]) { " " }
    }

    /*
     * The number of games that will be played
     */
    private var numberOfGames = 0

    /**
     * The number of the current game
     */
    private var currentGameNumber = 1

    private fun gameCycle() {
        while (true) {
            val input = askForColumn()
            val atWhichRowMoveHasBeenMade = makeMove(input)
            printBoard()
            when (checkWinningCondition(input, atWhichRowMoveHasBeenMade)) {
                GameStatus.WIN -> {
                    val wonPlayer = if (isFirstPlayerTurn) firstPlayer else secondPlayer
                    score[if (isFirstPlayerTurn) 0 else 1] += 2
                    println("Player $wonPlayer won")
                }
                GameStatus.DRAW -> {
                    println("It is a draw")
                    score[0] += 1
                    score[1] += 1
                }
                else -> {
                    isFirstPlayerTurn = !isFirstPlayerTurn
                    continue
                }
            }
            println(
                "Score\n" +
                        "$firstPlayer: ${score[0]} $secondPlayer: ${score[1]}"
            )
            currentGameNumber++
            isFirstPlayerTurn = !isFirstPlayerTurn
            return
        }
    }

    private fun askForGameTimes(): Int {
        println(
            "Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter"
        )
        print("Input a number of games: ")
        val answer = readLine()!!.replace("\\s+".toRegex(), "")

        if (answer.lowercase() == "end") throw Exception("Game over!")

        return when (answer.matches("[1-9]+".toRegex()) || answer.isEmpty()) {
            false -> {
                println("Invalid input")
                -1
            }
            true -> {
                if (answer.isEmpty()) 1 else answer.toInt()
            }
        }
    }

    private fun checkWinningCondition(move: Int, atWhichRowMoveHasBeenMade: Int): GameStatus {
        var status: GameStatus

        status = checkHorizontally(move, atWhichRowMoveHasBeenMade);
        if (status != GameStatus.CONTINUE) return status

        status = checkVertically(move, atWhichRowMoveHasBeenMade);
        if (status != GameStatus.CONTINUE) return status

        status = checkDiagonals(move, atWhichRowMoveHasBeenMade);
        if (status != GameStatus.CONTINUE) return status

        return if (isDraw()) GameStatus.DRAW
        else status
    }


    private fun checkDiagonals(move: Int, atWhichRowMoveHasBeenMade: Int): GameStatus {
        return if (checkSpecifiedDiagonal(FIRST, move, atWhichRowMoveHasBeenMade) ||
            checkSpecifiedDiagonal(SECOND, move, atWhichRowMoveHasBeenMade)
        ) GameStatus.WIN
        else GameStatus.CONTINUE
    }

    private fun checkSpecifiedDiagonal(DIAGONAL: Int, move: Int, sourceRow: Int): Boolean {
        val checkableSign = if (isFirstPlayerTurn) "o" else "*"
        var combo = 1

        for (rowIndex in board.indices) {
            // skipping if it is very high up  from the source one
            if (rowIndex + 3 < sourceRow || rowIndex == sourceRow) continue
            // and breaking if it is very low
            if (rowIndex - 3 > sourceRow) break

            // if the row is high up from the source one
            if (rowIndex < sourceRow) {
                // column from the row which will be checked
                val columnToCheck = when (DIAGONAL) {
                    FIRST -> move - (sourceRow - rowIndex)
                    else -> move + (sourceRow - rowIndex)
                }
                if (columnToCheck < 0 || columnToCheck > board.first().size - 1) continue
                combo = if (board[rowIndex][columnToCheck] == checkableSign) ++combo else 1
            }

            if (rowIndex > sourceRow) {
                val columnToCheck = when (DIAGONAL) {
                    FIRST -> move + (rowIndex - sourceRow)
                    else -> move - (rowIndex - sourceRow)
                }
                if (columnToCheck < 0 || columnToCheck > board.first().size - 1) continue

                combo = if (board[rowIndex][columnToCheck] == checkableSign) ++combo else 1
            }

            if (combo == 4) break

        }

        return combo == 4
    }

    private fun isDraw(): Boolean {
        for (row in board) {
            for (columnIndex in row.indices) {
                if (!isColumnFull(columnIndex)) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkVertically(move: Int, sourceRow: Int): GameStatus {
        val checkableSign = if (isFirstPlayerTurn) "o" else "*"
        var combo = 1

        for (rowIndex in board.indices) {
            // skipping if it is very high up  from the source one
            if (rowIndex + 3 < sourceRow || rowIndex == sourceRow) continue
            // and breaking if it is very low
            if (rowIndex - 3 > sourceRow) break

            combo = if (board[rowIndex][move] == checkableSign) ++combo else 1

            if (combo == 4) break
        }

        return if (combo == 4) GameStatus.WIN else GameStatus.CONTINUE
    }

    private fun checkHorizontally(move: Int, row: Int): GameStatus {
        val checkableSign = if (isFirstPlayerTurn) "o" else "*"
        var combo = 1

        for (columnIndex in board[row].indices) {
            // skipping if it is very far (on the right side) from the source one
            if (columnIndex + 3 < move || columnIndex == move) continue
            // breaking if it is very far (on the left side)
            if (columnIndex - 3 > move) break

            combo = if (board[row][columnIndex] == checkableSign) ++combo else 1

            if (combo == 4) break
        }

        return if (combo == 4) GameStatus.WIN else GameStatus.CONTINUE
    }

    fun printIntro() {
        if (currentGameNumber == 1) {
            println("$firstPlayer VS $secondPlayer")
            println("${boardDimensions[ROW_AMOUNT]} X ${boardDimensions[COLUMN_AMOUNT]} board")
            println(if (numberOfGames == 1) "Single game" else "Total $numberOfGames games")
        }
        if (numberOfGames != 1) println("Game #$currentGameNumber")
    }

    fun printBoard() {
        printColumnsNumbers()
        printRowsAndColumns()
        println("╚═${"╩═".repeat(boardDimensions[COLUMN_AMOUNT] - 1)}╝")
    }

    private fun printRowsAndColumns() {
        for (row in board) {
            var iterationCount = 1
            for (column in row) {
                if (iterationCount == boardDimensions[COLUMN_AMOUNT]) {
                    print("║$column║")
                } else {
                    print("║$column")
                    iterationCount++
                }
            }
            println()
        }
    }

    /**
     * Prints the columns' numbers in one line
     */
    private fun printColumnsNumbers() {
        for (i in board.first().indices) {
            val columnNum = i + 1
            print(" $columnNum")
        }
        println()
    }

    /**
     * Checks if the specified column is full
     */
    private fun isColumnFull(i: Int): Boolean {
        for (row in board) {
            if (row[i] == " ") return false
        }

        return true
    }

    /**
     * Asks to enter the column number a user want to add a sign to and returns this number.
     * If the input isn't correct, it explains the reason and asks to enter again.
     * So it's guaranteed that the return value will be correct and expected.
     */
    private fun askForColumn(): Int {
        val prompt = "${if (isFirstPlayerTurn) firstPlayer else secondPlayer}'s turn: "
        val columnAmount = boardDimensions[COLUMN_AMOUNT]

        while (true) {
            println(prompt)
            val answer = readLine()!!.replace(Regex("\\s*"), "")
            if (answer.lowercase() == "end") {
                throw Exception("Game over!")
            }

            val regex = "\\d+".toRegex()

            if (!answer.matches(regex)) {
                println("Incorrect column number")
                continue
            } else {
                when (answer.matches("[1-$columnAmount]".toRegex())) {
                    true -> {
                        val column = answer.toInt() - 1
                        if (isColumnFull(column)) {
                            println("Column ${column + 1} is full")
                            continue
                        }
                        return column
                    }
                    false -> {
                        println("The column number is out of range (1 - $columnAmount)")
                        continue
                    }
                } // when
            } // else
        } // while
    } // askForColumn()

    // Makes the needed move and returns at which row it has been made
    private fun makeMove(column: Int): Int {
        val sign = if (isFirstPlayerTurn) "o" else "*"
        val atWhichRowMoveHasBeenMade: Int

        for (rowIndex in board.size - 1 downTo 0) {
            if (board[rowIndex][column] == " ") {
                board[rowIndex][column] = sign
                atWhichRowMoveHasBeenMade = rowIndex
                return atWhichRowMoveHasBeenMade
            }
        }

        throw Exception("Unknown exception: Move hasn't been done")
    }
}