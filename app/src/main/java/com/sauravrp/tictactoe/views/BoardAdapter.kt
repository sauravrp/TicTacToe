package com.sauravrp.tictactoe.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sauravrp.tictactoe.databinding.ItemPieceViewBinding
import com.sauravrp.tictactoe.viewmodels.Piece
import com.sauravrp.tictactoe.viewmodels.Type

class BoardAdapter(private val clickListener: BoardListener) :
    ListAdapter<Piece, BoardAdapter.PieceViewHolder>(PieceDiffCallBack()) {

    inner class PieceViewHolder(val binding: ItemPieceViewBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        override fun onClick(v: View) {
            clickListener.pieceClicked(currentList[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieceViewHolder {
        val binding: ItemPieceViewBinding = ItemPieceViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PieceViewHolder(binding).apply {
            binding.root.setOnClickListener(this)
        }
    }

    override fun onBindViewHolder(holder: PieceViewHolder, position: Int) {
        holder.binding.piece.text = when (currentList[position].type) {
            Type.EMPTY -> ""
            Type.O -> "O"
            Type.X -> "X"
        }
    }

    interface BoardListener {
        fun pieceClicked(piece: Piece)
    }
}

class PieceDiffCallBack : DiffUtil.ItemCallback<Piece>() {
    override fun areItemsTheSame(oldItem: Piece, newItem: Piece): Boolean {
        return oldItem.position == newItem.position
    }

    override fun areContentsTheSame(oldItem: Piece, newItem: Piece): Boolean {
        return oldItem == newItem
    }
}