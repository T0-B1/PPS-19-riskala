<template>
<div class="container">
  <div class="subcontainer">
    <h1> Lobby </h1>
    <b-tabs content-class="mt-3">
      <b-tab title="Rooms" active>
        <h1>Rooms</h1>
        <b-table striped hover :items="itemsRoom" @row-clicked="myRowClickHandler"></b-table>
      </b-tab>
      <b-tab title="Games">
        <h1>Games</h1>
        <b-table striped hover :items="itemsGame" @row-clicked="myRowClickHandlerGame"></b-table>
      </b-tab>
      <b-tab title="Terminated Games">
        <h1>Terminated Games</h1>
        <b-table striped hover :items="itemsTerminated" @row-clicked="myRowClickHandlerTerminated"></b-table>
      </b-tab>
    </b-tabs>
  </div>
  <hr class="divider"/>
  <div class="buttons_div">
    <b-button variant="outline-primary" @click="createRoom">Create Room</b-button>
    <b-button id="joinBtn" variant="outline-primary" v-bind:disabled="disabled" @click="joinRoom">Join <b>{{this.join}}</b></b-button>
  </div>
</div>
</template>

<script>
  export default {
    data() {
      return {
        disabled:true,
        join: '',
        itemsRoom: [{Room_Name: '', Players: ''}],
        itemsGame: [{Game_Name: ''}],
        itemsTerminated: [{Terminated_Game_Name: ''}]
      }
    },
    mounted() {
      var vue = this
      var newHandler = function(evt) {
        ClientLobby.handleLobbyMessage(evt.data, vue)
      }
      this.$store.commit('changeHandler', newHandler)
      if(this.$store.state.lobbyInfo !== ''){
        ClientLobby.setupLobby(this.$store.state.lobbyInfo, this)
      }
    },
    methods: {
      readSocketMessage() {
        this.$store.state.websocket.onmessage = function(evt) { onMessage(evt) };
        function onMessage(evt) {
          ClientLobby.handleLobbyMessage(evt.data, this)
        }
      },
      cleanLobby() {
        this.itemsRoom.splice(0)
        this.itemsGame.splice(0)
        this.itemsTerminated.splice(0)
      },
      addRoom(name,player) {
        this.itemsRoom.push({Room_Name: name, Players:player})
      },
      addGame(name) {
        this.itemsGame.push({Game_Name: name})
      },
      addTerminated(name) {
        this.itemsTerminated.push({Terminated_Game_Name: name})
      },
      createRoom() {
        this.$router.push('/create_room')
      },
      joinRoom() {
        if(this.join !== ''){
          this.$store.state.websocket.send(ClientLobby.getJoinMsgWrapped(this.join))
        }
      },
      goToRoom(newRoom){
        this.$store.commit('changeLobbyInfo', '')
        this.$store.commit('changeRoomInfo', newRoom)
        this.$router.push('/room')
      },
      goToGame(newGame){
        this.$store.commit('changeLobbyInfo', '')
        this.$store.commit('changeGameInfo', newGame)
        this.$router.push('/game')
      },
      notifyError(error) {
        console.error(error)
      },
      myRowClickHandler(row) {
        this.join = row.Room_Name
        this.disabled=false
        document.getElementById("joinBtn").style.borderColor= "red"
        document.getElementById("joinBtn").style.color= "red"
        document.getElementById('joinBtn').onmouseover = function() {
          document.getElementById('joinBtn').style.backgroundColor='lightpink';
        }
        document.getElementById('joinBtn').onmouseleave = function() {
          document.getElementById('joinBtn').style.backgroundColor='white';
        }
      },
      myRowClickHandlerGame(row) {
        this.join = row.Game_Name
        this.disabled=false
        document.getElementById("joinBtn").style.borderColor= "red"
        document.getElementById("joinBtn").style.color= "red"
        document.getElementById('joinBtn').onmouseover = function() {
          document.getElementById('joinBtn').style.backgroundColor='lightpink';
        }
        document.getElementById('joinBtn').onmouseleave = function() {
          document.getElementById('joinBtn').style.backgroundColor='white';
        }
      },
      myRowClickHandlerTerminated(row) {
        this.join = row.Terminated_Game_Name
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