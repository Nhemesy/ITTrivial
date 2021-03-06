package com.pedrodavidlp.ittrivial.login.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrodavidlp.ittrivial.R
import com.pedrodavidlp.ittrivial.game.domain.model.Player
import kotlinx.android.synthetic.main.items_player_list.view.*

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.UserListViewHolder>(){
  private var listUsers: List<Player> = ArrayList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder =
      UserListViewHolder(LayoutInflater.from(parent.context)
          .inflate(R.layout.items_user_list, parent, false))

  override fun getItemCount(): Int {
    return listUsers.size
  }

  override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
    holder.setData(listUsers[position].username)
  }


  fun setList(list: List<Player>) {
    this.listUsers = list
    notifyDataSetChanged()
  }

  inner class UserListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    fun setData(username: String) {
      itemView.username.text = username
    }

  }
}