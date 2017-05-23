package com.pedrodavidlp.ittrivial.login.presenter

import com.pedrodavidlp.ittrivial.base.domain.data.Session
import com.pedrodavidlp.ittrivial.game.contract.MenuContract
import com.pedrodavidlp.ittrivial.login.router.MenuRouter

class MenuPresenter(private val router: MenuRouter): MenuContract.Presenter {
  lateinit var vw: MenuContract.View

  override fun init(){
    this.setWelcome()
  }
  override fun setView(view: MenuContract.View) {
    this.vw = view
  }

  private fun setWelcome(){
    vw.setWelcome(Session.username)
  }

  fun searchGame() {
    router.searchGames()
  }

  fun createGame() {
    router.createGame()
  }
}