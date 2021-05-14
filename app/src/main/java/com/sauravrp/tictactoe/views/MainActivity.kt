package com.sauravrp.tictactoe.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.sauravrp.tictactoe.R
import com.sauravrp.tictactoe.databinding.ActivityMainBinding
import com.sauravrp.tictactoe.viewmodels.Board
import com.sauravrp.tictactoe.viewmodels.Piece
import com.sauravrp.tictactoe.viewmodels.TicTacToeViewModel

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
            layoutManager = GridLayoutManager(context, DEFAULT_COL_SIZE)
        }

        viewModel.viewState.observe(this, {
            boardAdapter.submitList(it.pieces)
        })

        viewModel.init(DEFAULT_COL_SIZE)
    }

    override fun pieceClicked(piece: Piece) {
        viewModel.pieceSelected(piece)
    }

}