package com.pedrodavidlp.ittrivial.login.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.pedrodavidlp.ittrivial.R
import com.pedrodavidlp.ittrivial.base.pattern.ServiceLocator
import com.pedrodavidlp.ittrivial.game.domain.model.Player
import com.pedrodavidlp.ittrivial.login.contract.UserListContract
import com.pedrodavidlp.ittrivial.login.presenter.UserListPresenter
import kotlinx.android.synthetic.main.activity_player_list_guest.*

class PlayerListGuestActivity : AppCompatActivity(), UserListContract.View {
  lateinit var presenter: UserListPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_player_list_guest)
    presenter = ServiceLocator.Lobby.Presenter.providePlayerListGuest(this)
    presenter.setView(this)
    presenter.init()
  }

  override fun onLoadList(list: List<Player>) {
    (adminPlayerList.adapter as UserListAdapter).setList(list)
  }

  override fun showError(message: String) {
    Toast.makeText(applicationContext, "Error: $message", Toast.LENGTH_LONG).show()
  }

  override fun initUI() {
    this.title = presenter.getCurrentGame().name
    adminPlayerList.adapter = UserListAdapter()
    adminPlayerList.layoutManager = LinearLayoutManager(applicationContext)
  }
}
