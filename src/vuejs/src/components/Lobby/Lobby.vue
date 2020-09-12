<template>
<div class="container">
  <div class="subcontainer">
    <h1> Lobby </h1>
    <b-tabs content-class="mt-3">
      <b-tab title="Stanze" active>
        <h1>Stanze in corso </h1>
        <b-table striped hover :items="itemsRoom" @row-clicked="myRowClickHandler"></b-table>
      </b-tab>
      <b-tab title="Partite">
        <h1>Partite</h1>
        <b-table striped hover :items="itemsGame" @row-clicked="myRowClickHandler"></b-table>
      </b-tab>
      <b-tab title="Partite Terminate">
        <h1>Partite Terminate</h1>
        <b-table striped hover :items="itemsTerminated" @row-clicked="myRowClickHandler"></b-table>
      </b-tab>
    </b-tabs>
  </div>
  <hr class="divider"/>
  <div class="buttons_div">
    <b-button variant="outline-primary" @click="createRoom">Crea stanza</b-button>
    <b-button id="joinBtn" variant="outline-primary" v-bind:disabled="disabled" @click="joinRoom">Join <b>{{this.join}}</b></b-button>
    <!--<b-button variant="outline-primary">Carica partita</b-button>-->
  </div>
</div>
</template>

<script>
  export default {
    data() {
      return {
        disabled:true,
        join: '',
        itemsRoom: [{Nome_Stanza: '', Giocatori: ''}],
        itemsGame: [{Nome_Partita: ''}],
        itemsTerminated: [{Partita_Terminata: ''}]
      }
    },
    mounted() {
      var vue = this
      var newHandler = function(evt) {
        console.log('LOBBY - Receive message: ' + evt.data);
        ClientLobby.handleLobbyMessage(evt.data, vue)
      }
      this.$store.commit('changeHandler', newHandler)
      this.$store.state.websocket.send(ClientLobby.getJoinMsgWrapped())
      //console.log("Before readSockel LOBBY")
      //console.log(this.$store.state.websocket)
      //this.readSocketMessage()
      /*this.$store.state.websocket.onmessage = function(evt) {
        console.log("received msg from lobby"+evt.data)
      }*/
      //console.log("After readSocket LOBBY")
    },
    methods: {
      readSocketMessage() {
        this.$store.state.websocket.onmessage = function(evt) { console.log("rec.msg"+evt.data); onMessage(evt) };
        console.log("cerco di fare on message")
        function onMessage(evt) {
          console.log('LOBBY - Receive message: ' + evt.data);
          //var  dW = metodo deserializzaWrapped
          ClientLobby.handleLobbyMessage(evt.data, this)
          //scalaUpdateLobby(this)
        }
      },
      updateLobbyInfo(lobbyInfo) {
        console.log("Inside updateLobbyInfo of vue")
        console.log(lobbyInfo)
        /*
        this.$store.state.websocket.send("LOBBY - updated info into lobby")
        console.log(lobbyInfo.rooms, lobbyInfo.games, lobbyInfo.terminatedGames)
        this.itemsRoom = lobbyInfo.rooms
        this.itemsGame = lobbyInfo.games
        this.itemsTerminated = lobbyInfo.terminatedGames
        console.log("rooms"+this.itemsRoom, "games"+this.itemsGame,"terminated"+this.itemsTerminated)
        */
      },
      createRoom(){
        console.log('LOBBY - Call create_room')
        this.$router.push('/create_room')
      },
      joinRoom(){
        this.$store.state.websocket.send('LOBBY - Join room '+this.join)
        this.$router.push('/room')
      },
      notifyError(error) {
        console.log(error)
      },
      myRowClickHandler(row) {
        this.join = row.Nome_Partita
        this.disabled=false
        document.getElementById("joinBtn").style.borderColor= "red"
        document.getElementById("joinBtn").style.color= "red"
        document.getElementById('joinBtn').onmouseover = function() {
          document.getElementById('joinBtn').style.backgroundColor='lightpink';
        }
        document.getElementById('joinBtn').onmouseleave = function() {
          document.getElementById('joinBtn').style.backgroundColor='white';
        }
      }
    }
  }
</script>

<style lang="sass">
@import './Lobby.sass'
</style>