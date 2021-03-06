package com.pedrodavidlp.ittrivial.login.router

import android.support.v7.app.AppCompatActivity
import com.pedrodavidlp.ittrivial.game.view.activity.GameActivity
import com.pedrodavidlp.ittrivial.login.contract.UserListContract
import org.jetbrains.anko.startActivity

class UserListRouter(private val activity: AppCompatActivity) : UserListContract.Router {
  override fun goToGame(myTurn: Boolean) {
    activity.startActivity<GameActivity>(Pair("turn", myTurn))
    activity.finish()
  }
}