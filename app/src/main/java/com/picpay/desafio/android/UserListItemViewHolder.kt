package com.picpay.desafio.android

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserListItemViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val username: TextView = itemView.findViewById(R.id.username)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val picture: ImageView = itemView.findViewById(R.id.picture)

    fun bind(user: User) {
        name.text = user.name
        username.text = user.username
        progressBar.visibility = View.VISIBLE
        Picasso.get()
            .load(user.img)
            .error(R.drawable.ic_round_account_circle)
            .into(picture, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                }
            })
    }
}