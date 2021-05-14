package com.sauravrp.tictactoe.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.sauravrp.tictactoe.databinding.ActivityMainBinding
import com.sauravrp.tictactoe.viewmodels.Piece
import com.sauravrp.tictactoe.viewmodels.TicTacToeViewModel
import com.sauravrp.tictactoe.viewmodels.ViewEvent
import com.sauravrp.tictactoe.viewmodels.ViewState

class MainActivity : AppCompatActivity(), BoardAdapter.BoardListener {
    private lateinit var binding: ActivityMainBinding

    private val DEFAULT_COL_SIZE = 3

    private val viewModel: TicTacToeViewModel by viewModels()

    private var boardAdapter = BoardAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.board.apply {
            adapter = boardAdapter
        }

        binding.reset.setOnClickListener { viewModel.setupBoard(binding.columnInput.text.toString().toInt()) }

        viewModel.viewState.observe(this, {
            when (it) {
                ViewState.Init -> showInputView()
                is ViewState.Success -> showBoardView(it)
            }

        })

        viewModel.viewEvent.observe(this, {
            when (it) {
                ViewEvent.Draw -> notifyDraw()
                is ViewEvent.UserWon -> showWinner(it.firstUser)
                is ViewEvent.UserTurn -> showTurn(it.firstUser)
            }
        })

        viewModel.init()
    }

    private fun showBoardView(viewState: ViewState.Success) {
        with(binding) {
            columnInput.isEnabled = false
            columnInput.isVisible = false
            reset.text = "Reset"
            board.isVisible = true
        }
        binding.board.apply {
            layoutManager = GridLayoutManager(context, viewState.boardSize)
        }
        boardAdapter.submitList(viewState.pieces)
    }

    private fun showInputView() {
        with(binding) {
            board.isVisible = false
            reset.text = "Start"
            columnInput.isEnabled = true
            columnInput.setText(DEFAULT_COL_SIZE.toString())
        }

    }

    private fun showWinner(firstUser: Boolean) {
        binding.msg.text = if (firstUser) "User A Won" else "User B Won"
    }

    private fun showTurn(firstUser: Boolean) {
        binding.msg.text = if (firstUser) "User A's Turn" else "User B's Turn"
    }

    private fun notifyDraw() {
        Toast.makeText(this, "Draw", Toast.LENGTH_SHORT).show()
    }

    override fun pieceClicked(piece: Piece) {
        viewModel.pieceSelected(piece)
    }

}