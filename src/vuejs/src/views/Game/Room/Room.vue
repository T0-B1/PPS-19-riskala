<template>
  <div class="container">
      <div class="subcontainer">
        <b-card
          class="card"
          :title="roomName">
          <hr class="divider"/>
          <div class="infoPlay">
            <table class="table">
              <thead>
                <tr v-for="(title,index) in titleTable" scope="col" :key="index">
                  <th>{{title.prima_colonna}}</th>
                  <th> {{title.seconda_colonna}}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(player,index) in players" scope="col span 2" :key="index">
                  <td>{{player.Name_Of_Player}}</td>
                  <td>
                    <div id="square" class="square" :style="{backgroundColor:player.color,border: '2px black', height:'10px',width:'10px', margin:'0 auto'}"></div>
                  </td>
                </tr>
              </tbody>
            </table>
            <b-button v-if="this.ready === false" class="readyBtn" variant="outline-primary" v-on:click="readyClick">Ready</b-button>
            <b-button v-if="this.ready === true" class="readyBtn" variant="outline-primary"  v-on:click="unready">Unready</b-button>
          </div>
          <hr class="divider"/>
          <div class="preview">
            <h3>Preview map game</h3> 
            <img id="svgMapContainer" src="@/assets/maps/italy.svg" />
            <b-button id="joinBtn" variant="outline-danger" @click="leaveRoom">Leave room</b-button>
          </div>
        </b-card>
      </div>
      <b-modal id="modal-error" auto-focus-button="ok" ok-only title="Error Message">
        <p class="my-4"><i>{{this.error}}</i></p>
      </b-modal>
  </div>
</template>

<script>
import * as d3 from 'd3'
var seedRandom = require('seedrandom')

export default {
  data(){
   return {
     titleTable: [
       {prima_colonna: "Name of Player", seconda_colonna: "Color"}
     ],
     roomName: 'Room',
     myRng: null,
     ready:false,
     players: []
   }
  },
  mounted() {
    this.myRng = seedRandom(this.roomName)
    var vue = this
    var newHandler = function(evt) {
      console.log('ROOM - Receive message: ' + evt.data);
      ClientRoom.handleRoomMessage(evt.data, vue)
    }
    this.$store.commit('changeHandler', newHandler)

    if(this.$store.state.roomInfo !== ''){
      ClientRoom.setupRoom(this.$store.state.roomInfo, this)
    }
  },
  methods: {
    getRandomColor(name) {
      var rng = seedRandom(name)
      var letters = '0123456789ABCDEF';
      var color = '#';
      for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(rng() * 16)];
      }
      return color;
    },
    addPlayers(name){
      this.players.push({Name_Of_Player: name, color: this.getRandomColor(name)})
    },
    clearPlayer(){
      this.players.splice(0)
    },
    setName(roomName){
      this.roomName = roomName
    },
    bind(){
      Array.prototype.forEach.call( document.getElementsByTagName("path"), function(el) {
        el.onclick = function(){ alert(el.id); };
      })
    },
    goToGame(newGame){
      this.$store.commit('changeGameInfo', newGame)
      this.$router.push('/game')
    },
    readyClick() {
      this.ready=true
      this.$store.state.websocket.send(ClientRoom.getReadyMsgWrapped("ReadyMessage", this.getRandomColor(localStorage.riskalaUser)))
    },
    unready(){
      this.ready=false
      this.$store.state.websocket.send(ClientRoom.getMsgWrapped("UnReadyMessage"))
    },
    leaveRoom(){
      this.$store.state.websocket.send(ClientRoom.getMsgWrapped("LeaveMessage"))
      this.$router.push('/')
      this.$store.state.roomInfo = ''
    },
    notifyError(error){
      this.error = error
      this.$bvModal.show('modal-error')
    }
  }
}
</script>

<style lang="sass">
.readyBtn
  margin: 0 auto
.preview
  display: flex
  flex-direction: column
  #svgMapContainer, #joinBtn
    max-width: 30%
    margin: 0 auto
</style>