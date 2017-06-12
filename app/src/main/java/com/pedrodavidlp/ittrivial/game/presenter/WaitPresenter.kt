package com.pedrodavidlp.ittrivial.game.presenter

import com.pedrodavidlp.ittrivial.base.domain.data.Session
import com.pedrodavidlp.ittrivial.game.contract.PlayerListContract
import com.pedrodavidlp.ittrivial.game.contract.WaitContract
import com.pedrodavidlp.ittrivial.game.domain.model.Player
import com.pedrodavidlp.ittrivial.game.domain.usecase.GetTurn
import com.pedrodavidlp.ittrivial.game.domain.usecase.LeaveGame
import com.pedrodavidlp.ittrivial.game.router.WaitRouter
import com.pedrodavidlp.ittrivial.login.domain.usecase.GetPlayerList

class WaitPresenter(private val turn: GetTurn,
                    private val leave: LeaveGame,
                    private val players: GetPlayerList,
                    private val router: WaitRouter) :
    WaitContract.InteractorOutput,
    PlayerListContract.InteractorOutput {

  override fun gameFinished(winner: Player) {
    router.goToFinishGame(winner)
  }

  override fun onFetchPlayerList(list: List<Player>) {
    v.showListPlayers(list)
  }

  lateinit private var v: WaitContract.View


  fun setView(view: WaitContract.View) {
    this.v = view
  }

  fun init() {
    this.getTurn()
    this.getPlayers()
  }

  override fun onMyTurn() {
    v.myTurn()
    router.goToRoulette()
  }

  override fun onChangeTurn(player: Player) {
    v.changeTurn(player)
  }

  private fun getTurn() {
    turn.getTurn(Session.game, this)
  }

  private fun getPlayers() {
    players.getUserList(Session.game, this)
  }

  override fun onLeaveGame() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onError() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}
