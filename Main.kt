package connectfour

// enums
private const val FIRST = 1
private const val SECOND = 2

fun main() {
    println("Connect Four")
    val firstPlayer = readName(FIRST)
    val secondPlayer = readName(SECOND)
    val boardDimensions = readBoardDimensions()

    val game = Game(firstPlayer, secondPlayer, boardDimensions)
    game.starGame()

}

fun readBoardDimensions(): List<Int> {
    while (true){
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")

        val generalRegex = Regex("\\d+x\\d+")
        val concreteRegex = Regex("[5-9]x[5-9]")

        val answer = readLine()!!.replace(Regex("\\s*"), "").lowercase()

        when (true) {
            answer == "" -> return listOf<Int>(6, 7)
            answer.matches(generalRegex) -> {
                if (answer.matches(concreteRegex)) return listOf(answer[0].digitToInt(), answer[2].digitToInt())
                else printWhatIsWrong(answer)
            }
            else -> {
                println("Invalid input")
            } // when's else branch
        } // when
    } // while
} // function

fun printWhatIsWrong(answer: String) {
    val rowSize = answer.substringBefore("x").toInt()
    val columnSize = answer.substringAfter("x").toInt()

    val reason = when {
        rowSize !in 5..9 && columnSize !in 5..9 -> "Board rows and columns should be from 5 to 9"
        rowSize !in 5..9 && columnSize in 5..9 -> "Board rows should be from 5 to 9"
        else -> "Board columns should be from 5 to 9"
    }

    println(reason)
}

fun readName(PLAYER: Int): String {
    val prompt = when (PLAYER) {
        FIRST -> "First player's name: "
        SECOND -> "Second player's name: "
        else -> throw Exception("You should pass FIRST (1) or SECOND (2) constant as an argument")
    }

    print(prompt)
    return readLine()!!
}