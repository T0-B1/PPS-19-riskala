<template>
<div class="container">
  <div class="subcontainer">
    <h1> Lobby </h1>
    <h2> Stanze </h2>
    <b-table striped hover :items="itemsRoom" @row-clicked="myRowClickHandler"></b-table>
    <h2> Partite </h2>
    <b-table striped hover :items="itemsGame"></b-table>
    <h2> Partite Terminate </h2>
    <b-table striped hover :items="itemsTerminated"></b-table>
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
      this.readSocketMessage()
    },
    methods: {
      readSocketMessage() {
        this.$store.websocket.onmessage = function(evt) { onMessage(evt) };
        function onMessage(evt) {
          console.log('LOBBY - Receive message: ' + evt.data);
          //var  dW = metodo deserializzaWrapped
          //scalaUpdateLobby(this)
        }
      },
      updateLobbyInfo(lobbyInfo) {
        this.$store.websocket.send("LOBBY - updated info into lobby")
        console.log(lobbyInfo.rooms, lobbyInfo.games, lobbyInfo.terminatedGames)
        //this.items = lobbyInfo
      },
      createRoom(){
        console.log('LOBBY - Call create_room')
        this.$router.push('/create_room')
      },
      joinRoom(){
        this.$store.websocket.send('LOBBY - Join room '+this.join)
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