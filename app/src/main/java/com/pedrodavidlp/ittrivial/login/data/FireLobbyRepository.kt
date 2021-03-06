package com.pedrodavidlp.ittrivial.login.data

import android.util.Log
import com.google.firebase.database.*
import com.pedrodavidlp.ittrivial.base.domain.data.Session
import com.pedrodavidlp.ittrivial.base.pattern.Observer
import com.pedrodavidlp.ittrivial.game.domain.model.Game
import com.pedrodavidlp.ittrivial.game.domain.model.Player
import com.pedrodavidlp.ittrivial.login.contract.GameListContract
import com.pedrodavidlp.ittrivial.login.contract.MenuContract
import com.pedrodavidlp.ittrivial.login.contract.UserListContract
import com.pedrodavidlp.ittrivial.login.domain.repository.LobbyRepository
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class FireLobbyRepository : LobbyRepository {
  val randomNames: Array<String> = arrayOf("Ailurophile", "Assemblage",
      "Becoming", "Beleaguer", "Brood", "Bucolic", "Bungalow", "Chatoyant", "Comely",
      "Conflate", "Cynosure", "Dalliance", "Demesne", "Dominion", "Demure", "Denouement", "Desuetude",
      "Desultory", "Diaphanous", "Dissemble", "Dulcet", "Ebullience", " Effervescent", "Efflorescence",
      "Elision", "Elixir", "Eloquence", "Embrocation", "Emollient", "Ephemeral",
      "Epiphany", "Erstwhile", "Ethereal", "Evanescent", "Evocative", "Fetching",
      "Felicity", "Forbearance", "Fugacious", "Furtive", "Gambol", "Glamour",
      "Gossamer", "Halcyon", "Harbinger", "Imbrication", "Imbroglio", "Imbue",
      "Incipient", "Ineffable", "Ingénue", "Inglenook", "Insouciance", "Inure",
      "Labyrinthine", "Lagniappe", "Lagoon", "Languor", "Lassitude", "Leisure",
      "Lilt", "Lissome", "Lithe", "Love", "Mellifluous", "Moiety", "Mondegreen",
      "Murmurous", "Nemesis", "Offing", "Onomatopoeia", "Opulent", "Palimpsest",
      "Panacea", "Panoply", "Pastiche", "Penumbra", "Petrichor", "Plethora", "Propinquity",
      "Pyrrhic", "Quintessential", "Ratatouille", "Ravel", "Redolent", "Riparian",
      "Ripple", "Scintilla", "Sempiternal", "Seraglio", "Serendipity", "Summery",
      "Sumptuous", "Surreptitious", "Susquehanna", "Susurrous", "Talisman", "Tintinnabulation",
      "Umbrella", "Untoward", "Vestigial", "Wafture", "Wherewithal", "Woebegone")

  val firebase: FirebaseDatabase = FirebaseDatabase.getInstance()
  val ref: DatabaseReference = firebase.reference
  lateinit var listener: ValueEventListener

  override fun createGame(admin: Player, callback: MenuContract.InteractorOutput) {
    ref.child("games").addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onCancelled(databaseError: DatabaseError) {
        TODO(databaseError.message)
      }

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        val keys = ArrayList<String>()
        dataSnapshot.children.forEach {
          keys.add(it.key)
        }
        var name = selectRandomName()
        while (keys.contains(name)) {
          name = selectRandomName()
        }
        ref.child("games").child(name).child("started").setValue(false)
        ref.child("games").child(name).child("players").child(admin.username).setValue(admin)
        callback.onGameCreated(Game(name, 1, false))
      }

    })

  }


  override fun getCurrentActivePlayer(game: Game, observer: Observer<Player?>): Player? {
    var playerPlaying: Player? by Delegates.observable(null) {
      _: KProperty<*>, old: Player?, new: Player? ->
      observer.onValueChange(new, old)
    }


    ref.child("games").child(game.name).addValueEventListener(object : ValueEventListener {
      override fun onCancelled(p0: DatabaseError?) {}

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        Log.d("VECES ACIVADO GETTURN", "UNA MAS")

        if (dataSnapshot.child("started").getValue(Boolean::class.java)) {
          val turn = dataSnapshot.child("turn").getValue(Int::class.java)
          val userList = ArrayList<Player>()
          val playerMap: HashMap<*, *> = dataSnapshot.child("players").value as HashMap<*, *>
          playerMap.entries.forEach {
            val map = dataSnapshot.value as HashMap<*, *>
            val player: Player =
                Player(it.key.toString(),
                    map["admin"].toString() == "true",
                    map["history"].toString() == "true",
                    map["hardware"].toString() == "true",
                    map["network"].toString() == "true",
                    map["software"].toString() == "true",
                    map["enterprise"].toString() == "true"
                )
            userList.add(player)
          }
          playerPlaying = userList[turn]
        }
      }

    })
    return playerPlaying
  }


  override fun getGames(observer: Observer<List<Game>>): MutableList<Game> {
    var listGames: MutableList<Game> by Delegates.observable(ArrayList()) {
      _, old, new ->
      Log.d("lista", new.size.toString())
      observer.onValueChange(new, old)
    }

    ref.child("games").addValueEventListener(object : ValueEventListener {
      override fun onCancelled(p0: DatabaseError?) {}

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        val gameList = ArrayList<Game>()
        dataSnapshot.children.forEach {
          gameList.add(
              Game(it.key,
                  it.child("players").childrenCount.toInt(),
                  it.child("started").getValue(Boolean::class.java)))
        }
        listGames = gameList
      }
    })
    return listGames
  }

  override fun getUsersInGame(game: Game, observer: Observer<List<Player>>): List<Player> {
    var listPlayers: MutableList<Player> by Delegates.observable(ArrayList()) {
      _, old, new ->
      observer.onValueChange(new, old)
    }

    listener = object : ValueEventListener {
      override fun onCancelled(p0: DatabaseError?) {}

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        val userList = ArrayList<Player>()
        val playerMap: HashMap<*, *> = dataSnapshot.child("players").value as HashMap<*, *>
        playerMap.entries.forEach {
          val map = (dataSnapshot.child("players").value as HashMap<*, *>)[it.key] as HashMap<*, *>
          val player: Player =
              Player(it.key.toString(),
                  map["admin"].toString() == "true",
                  map["history"].toString() == "true",
                  map["hardware"].toString() == "true",
                  map["network"].toString() == "true",
                  map["software"].toString() == "true",
                  map["enterprise"].toString() == "true"
              )
          userList.add(player)
        }
        userList.sortBy { it.username }
        listPlayers = userList
      }
    }
    ref.child("games").child(game.name).addValueEventListener(listener)

    return listPlayers
  }

  override fun startGame(game: Game, callback: UserListContract.InteractorOutput) {
    listener = object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        val randomTurn = this@FireLobbyRepository
            .selectRandomTurn(dataSnapshot.childrenCount.toInt())
        ref.child("games")
            .child(game.name).child("turn").setValue(randomTurn)
        ref.child("games").child(game.name).child("started").setValue(true)
        Log.d("Esto no debe aparecer", "UNA MAS")
        val userList = ArrayList<Player>()
        val playerMap: HashMap<*, *> = dataSnapshot.value as HashMap<*, *>
        playerMap.entries.forEach {
          val map = (dataSnapshot.value as HashMap<*, *>)[it.key] as HashMap<*, *>
          val player: Player =
              Player(it.key.toString(),
                  map["admin"].toString() == "true",
                  map["history"].toString() == "true",
                  map["hardware"].toString() == "true",
                  map["network"].toString() == "true",
                  map["software"].toString() == "true",
                  map["enterprise"].toString() == "true"
              )
          userList.add(player)
        }


        if (userList[randomTurn].admin) {
          callback.onInitAndMyTurn()
          ref.child("games").child(game.name).child("players").removeEventListener(listener)
        } else {
          callback.onInitAndWait()
          ref.child("games").child(game.name).child("players").removeEventListener(listener)
        }

      }

      override fun onCancelled(p0: DatabaseError?) {
        callback.onError()
      }
    }
    ref.child("games").child(game.name).child("players").addValueEventListener(listener)

  }

  private fun selectRandomTurn(numberPlayers: Int): Int {
    return Random().nextInt(numberPlayers)
  }

  private fun selectRandomName(): String {
    return randomNames[Random().nextInt(randomNames.size - 1)]
  }

  override fun enterGame(game: Game, callback: GameListContract.InteractorOutput) {
    ref.child("games").child(game.name).child("players").addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onCancelled(p0: DatabaseError) {
        callback.onError()
      }

      override fun onDataChange(dataSnapshot: DataSnapshot) {
        ref.child("games").child(game.name).child("players")
            .child(Session.player.username).setValue(Player(Session.player.username, false))
        callback.onJoinGame(game)
      }
    })
  }

  override fun exitGame(game: Game, callback: UserListContract.InteractorOutput) {
    ref.child("games").child(game.name).child("players").child(Session.player.username).removeValue()
  }
}
