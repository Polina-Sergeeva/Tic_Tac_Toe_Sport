package sergeeva.tic_tac_toe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import sergeeva.tic_tac_toe.R
import sergeeva.tic_tac_toe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameField: Array<Array<String>>

    private lateinit var settingsInfo: SettingsActivity.SettingsInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)

        binding.toMenu.setOnClickListener {
            showPopupMenu()
        }

        binding.toGameClose.setOnClickListener {
            onBackPressed()
        }

        binding.cell11.setOnClickListener {
            makeStepOfUser(0,0)
        }

        binding.cell12.setOnClickListener {
            makeStepOfUser(0,1)
        }

        binding.cell13.setOnClickListener {
            makeStepOfUser(0,2)
        }

        binding.cell21.setOnClickListener {
            makeStepOfUser(1,0)
        }

        binding.cell22.setOnClickListener {
            makeStepOfUser(1,1)
        }

        binding.cell23.setOnClickListener {
            makeStepOfUser(1,2)
        }

        binding.cell31.setOnClickListener {
            makeStepOfUser(2,0)
        }

        binding.cell32.setOnClickListener {
            makeStepOfUser(2,1)
        }

        binding.cell33.setOnClickListener {
            makeStepOfUser(2,2)
        }

        setContentView(binding.root)

        initGameField()


        settingsInfo = getSettingsInfo()

    }

    private fun initGameField() {
        gameField = Array(3){ Array(3){" "} }
    }

    private fun makeStep(row: Int, column: Int, symbol: String) {
        gameField[row][column] = symbol

        makeStepUI("$row$column", symbol)
    }

    private fun makeStepUI(position: String, symbol: String) {
        val resId = when(symbol) {
            "X" -> R.drawable.cross
            "0" -> R.drawable.zero
            else -> return
        }

        when(position) {
            "00" -> binding.cell11.setImageResource(resId)
            "01" -> binding.cell12.setImageResource(resId)
            "02" -> binding.cell13.setImageResource(resId)
            "10" -> binding.cell21.setImageResource(resId)
            "11" -> binding.cell22.setImageResource(resId)
            "12" -> binding.cell23.setImageResource(resId)
            "20" -> binding.cell31.setImageResource(resId)
            "21" -> binding.cell32.setImageResource(resId)
            "22" -> binding.cell33.setImageResource(resId)
        }
    }

    private fun makeStepOfUser(row: Int, column: Int) {
        if (isEmptyField(row, column)) {
            makeStep(row, column, "X")

            val status = checkGameField(row, column, "X")
            if (status.status) {
                showGameStatus(STATUS_PLAYER_WIN)
                return
            }

            if (!isFilledGameField()) {
                val resultCell = makeStepOfAI()

                val statusAI = checkGameField(resultCell.row, resultCell.column, "0")
                if (statusAI.status) {
                    showGameStatus(STATUS_PLAYER_LOSE)
                    return
                }

                if (isFilledGameField()) {
                    showGameStatus(STATUS_PLAYER_DRAW)
                    return
                }
            } else {
                showGameStatus(STATUS_PLAYER_DRAW)
                return
            }
        } else {
            Toast.makeText(this, "Already filled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmptyField(row: Int, column: Int): Boolean {
        return gameField[row][column] == " "
    }

    private fun makeStepOfAI() : CellGameFiled {
        return when(settingsInfo.lvl) {
            0 -> makeStepOfAIEasyLvl()
            1 -> makeStepOfAINormalLvl()
            2 -> makeStepOfAIHardLvl()
            else -> CellGameFiled(0, 0)
        }
    }

    data class CellGameFiled(val row: Int, val column: Int)

    private fun makeStepOfAIHardLvl() : CellGameFiled {
        var bestScore = Double.NEGATIVE_INFINITY
        var move = CellGameFiled(0, 0)

        var board = gameField.map { it.clone() }.toTypedArray()

        for(indexRow in 0..2) {
            for(indexCols in 0..2) {
                if (board[indexRow][indexCols] == " ") {
                    board[indexRow][indexCols] = "0"
                    val score = minimax(board, false, false)
                    board[indexRow][indexCols] = " "
                    if (score > bestScore) {
                        bestScore = score
                        move = CellGameFiled(indexRow, indexCols)
                    }
                }
            }
        }

        makeStep(move.row, move.column, "0")

        return move
    }

    private fun minimax(board: Array<Array<String>>, isMaximizing: Boolean, isNormal: Boolean): Double {
        val result = checkWinner(board)
        result?.let{
            return scores[result]!!
        }
        if(!isNormal){
            if (isMaximizing) {
                var bestScore = Double.NEGATIVE_INFINITY
                for(indexRow in 0..2) {
                    for(indexCols in 0..2) {
                        if (board[indexRow][indexCols] == " ") {
                            board[indexRow][indexCols] = "0"
                            val score = minimax(board, false, false)
                            board[indexRow][indexCols] = " "
                            if (score > bestScore) {
                                bestScore = score
                            }
                        }
                    }
                }
                return bestScore
            }
            else {
                var bestScore = Double.POSITIVE_INFINITY
                for(indexRow in 0..2) {
                    for(indexCols in 0..2) {
                        if (board[indexRow][indexCols] == " ") {
                            board[indexRow][indexCols] = "X"
                            val score = minimax(board, true, false)
                            board[indexRow][indexCols] = " "
                            if (score < bestScore) {
                                bestScore = score
                            }
                        }
                    }
                }
                return bestScore
            }
        }
        else{
            var count = 0
            if (isMaximizing) {
                var bestScore = Double.NEGATIVE_INFINITY
                for(indexRow in 0..2) {
                    for(indexCols in 0..2) {
                        count++
                        if (board[indexRow][indexCols] == " ") {
                            board[indexRow][indexCols] = "0"
                            val score = minimax(board, false, true)
                            board[indexRow][indexCols] = " "
                            if (score > bestScore) {
                                bestScore = score
                            }
                        }
                        if(count > 6){
                            break
                        }
                    }
                }
                return bestScore
            }
            else {
                var bestScore = Double.POSITIVE_INFINITY
                for(indexRow in 0..2) {
                    for(indexCols in 0..2) {
                        if (board[indexRow][indexCols] == " ") {
                            board[indexRow][indexCols] = "X"
                            val score = minimax(board, true, true)
                            board[indexRow][indexCols] = " "
                            if (score < bestScore) {
                                bestScore = score
                            }
                        }
                    }
                }
                return bestScore
            }
        }
    }

    private fun checkWinner(board: Array<Array<String>>): Int? {
        var countRowsHu = 0
        var countRowsAI = 0
        var countLDHu = 0
        var countLDAI = 0
        var countRDHu = 0
        var countRDAI = 0

        board.forEachIndexed { indexRow, cols ->
            if (cols.all { it == "X" })
                return STATUS_PLAYER_WIN
            else if(cols.all { it == "0" })
                return STATUS_PLAYER_LOSE

            countRowsHu = 0
            countRowsAI = 0

            for(indexCols in 0..2) {
                if (board[indexCols][indexRow] == "X")
                    countRowsHu++
                else if (board[indexCols][indexRow] == "0")
                    countRowsAI++

                if (indexRow == indexCols && board[indexRow][indexCols] == "X")
                    countLDHu++
                else if (indexRow == indexCols && board[indexRow][indexCols] == "0")
                    countLDAI++

                if (indexRow == 2 - indexCols && board[indexRow][indexCols] == "X")
                    countRDHu++
                else if (indexRow == 2 - indexCols && board[indexRow][indexCols] == "0")
                    countRDAI++
            }

            if (countRowsHu == 3 || countLDHu == 3 || countRDHu == 3)
                return STATUS_PLAYER_WIN
            else if (countRowsAI == 3 || countLDAI == 3 || countRDAI == 3)
                return STATUS_PLAYER_LOSE
        }

        board.forEach {
            if (it.find { it == " " } != null)
                return null
        }
        return STATUS_PLAYER_DRAW
    }

    private fun makeStepOfAINormalLvl() : CellGameFiled {
        var bestScore = Double.NEGATIVE_INFINITY
        var move = CellGameFiled(0, 0)
        var board = gameField.map { it.clone() }.toTypedArray()

        for(indexRow in 0..2) {
            for(indexCols in 0..2) {
                if (board[indexRow][indexCols] == " ") {
                    board[indexRow][indexCols] = "0"
                    val score = minimax(board, false, true)
                    board[indexRow][indexCols] = " "
                    if (score > bestScore) {
                        bestScore = score
                        move = CellGameFiled(indexRow, indexCols)
                    }
                }
            }
        }

        makeStep(move.row, move.column, "0")

        return move
    }

    private fun makeStepOfAIEasyLvl() : CellGameFiled {
        var randRow = 0
        var randColumn = 0

        do {
            randRow = (0..2).random()
            randColumn = (0..2).random()
        } while (!isEmptyField(randRow, randColumn))

        makeStep(randRow, randColumn, "0")

        return CellGameFiled(randRow, randColumn)
    }


    private fun checkGameField(x: Int, y: Int, symbol: String) : StatusInfo {
        var row = 0
        var column = 0
        var leftDiagonal = 0
        var rightDiagonal = 0
        var n = gameField.size

        for (i in 0..2) {
            if (gameField[x][i] == symbol)
                column++
            if (gameField[i][y] == symbol)
                row++
            if (gameField[i][i] == symbol)
                leftDiagonal++
            if (gameField[i][n-i-1] == symbol)
                rightDiagonal++
        }

        return when(settingsInfo.rules) {
            1 -> {
                if (column == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            2 -> {
                if (row == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            3 -> {
                if (column == n || row == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            4 -> {
                if (leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            5 -> {
                if (column == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            6 -> {
                if (row == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            7 -> {
                if (column == n || row == n || leftDiagonal == n || rightDiagonal == n)
                    StatusInfo(true, symbol)
                else
                    StatusInfo(false, "")
            }
            else -> StatusInfo(false, "")
        }
    }

    data class StatusInfo(val status: Boolean, val side: String)

    private fun showGameStatus(status: Int) {
        val dialog = Dialog(this, R.style.Theme_TestTicTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50,0,0,0)))
            setContentView(R.layout.dialog_popp_status_game)
            setCancelable(true)
        }

        val image = dialog.findViewById<ImageView>(R.id.dialog_img)
        val text = dialog.findViewById<TextView>(R.id.dialog_text)
        val button = dialog.findViewById<TextView>(R.id.dialog_ok)

        button.setOnClickListener {
            onBackPressed()
        }

        when(status) {
            STATUS_PLAYER_WIN -> {
                image.setImageResource(R.drawable.win)
                text.text = getString(R.string.dialog_status_win)
            }
            STATUS_PLAYER_LOSE -> {
                image.setImageResource(R.drawable.lose)
                text.text = getString(R.string.dialog_status_lose)
            }
            STATUS_PLAYER_DRAW -> {
                image.setImageResource(R.drawable.draw)
                text.text = getString(R.string.dialog_status_draw)
            }
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_POPUP_MENU) {
            if (requestCode == RESULT_OK) {
                settingsInfo = getSettingsInfo()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showPopupMenu() {
        val dialog = Dialog(this, R.style.Theme_TestTicTacToe)
        with(dialog) {
            window?.setBackgroundDrawable(ColorDrawable(Color.argb(50,0,0,0)))
            setContentView(R.layout.dailag_popup_menu)
            setCancelable(true)
        }

        val toContinue = dialog.findViewById<TextView>(R.id.dialog_continue)
        val toSettings = dialog.findViewById<TextView>(R.id.dialog_settings)
        val toExit = dialog.findViewById<TextView>(R.id.dialog_exit)

        toContinue.setOnClickListener {
            dialog.hide()
        }

        toSettings.setOnClickListener {
            dialog.hide()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, REQUEST_POPUP_MENU)

        }

        toExit.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }

        dialog.show()
    }

    private fun isFilledGameField() : Boolean {
        gameField.forEach { strings ->
            if (strings.find { it == " " } != null)
                return false
        }
        return true
    }


    private fun getSettingsInfo() : SettingsActivity.SettingsInfo {
        with(getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)) {
            val lvl = getInt(SettingsActivity.PREF_LVL, 0)
            val rules = getInt(SettingsActivity.PREF_RULES, 7)

            return SettingsActivity.SettingsInfo(lvl, rules)
        }
    }



    companion object {
        const val STATUS_PLAYER_WIN = 1
        const val STATUS_PLAYER_LOSE = 2
        const val STATUS_PLAYER_DRAW = 3

        val scores = hashMapOf(Pair(STATUS_PLAYER_WIN, -1.0), Pair(STATUS_PLAYER_LOSE, 1.0),
            Pair(STATUS_PLAYER_DRAW, 0.0))

        const val REQUEST_POPUP_MENU = 123
    }
}