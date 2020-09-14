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
          <div id="svgMapContainer">
          </div>
          <b-button id="joinBtn" variant="outline-danger" @click="leaveRoom">Leave room</b-button>
        </b-card>
      </div>    
  </div>
</template>

<script>
//con mounted chiamo API se num max giocatori
import * as d3 from 'd3'
//import seedRandom from 'seedrandom'
var seedRandom = require('seedrandom')
export default {
  data(){
   return {
     srcMap:'https://raw.githubusercontent.com/raddrick/risk-map-svg/master/risk.svg',
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
    //this.myRng = new Math.seedrandom(this.roomName)
    this.myRng = seedRandom(this.roomName)
    console.log(this.myRng())
    var vue = this
    var newHandler = function(evt) {
      console.log('ROOM - Receive message: ' + evt.data);
      ClientRoom.handleRoomMessage(evt.data, vue)
    }
    this.$store.commit('changeHandler', newHandler)

    this.loadSvg();
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
    loadSvg(){
      d3.xml(this.srcMap)
      .then(data => {
        d3.select("#svgMapContainer").node().append(data.documentElement);
      }); 
    },
    readyClick() {
      this.ready=true
      this.$store.state.websocket.send(ClientRoom.getReadyMsgWrapped())
    },
    unready(){
      this.ready=false
      this.$store.state.websocket.send(ClientRoom.getUnReadyMsgWrapped())
    },
    leaveRoom(){
      this.$store.state.websocket.send(ClientRoom.getLeaveMsgWrapped())
      this.$router.push('/')
      this.$store.state.roomInfo = ''
    }
  }
}
</script>

<style lang="sass">
.readyBtn
  margin: 0 auto
svg
  width: 50%
</style>