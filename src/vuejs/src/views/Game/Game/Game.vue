<template>
  <div>
    <div class="wrapper">
      <div class="objective"> <span> Your objective is: <b><i> {{objective}} </i></b></span> </div>
      <div class="infoContainer">
        <div class="leftContainer">
          <div class="radioDiv">
            <h5> Turn of: </h5>
            <div v-for="(player,index) in players" :key="index" class="form-check">
              <input type="radio" :id="player.id" :checked="player.checked">
              <label :for="player.id">{{player.nome_giocatore}}</label>
            </div>
            <b-button variant="danger"> End Turn </b-button>
          </div>
          <hr/>
          <div class="buttonDiv" >
            <button v-on="infantryEnable" type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/buttonsImg/infantry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{infantryCards}}</span>
            <button v-on="cavalryEnable" type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/buttonsImg/cavalry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{cavalryCards}}</span>
            <button v-on="artilleryEnable" type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/buttonsImg/artillery.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{artilleryCards}}</span>
          </div>
        </div>
        <div class="stateInfo">
          <h4 id="idInfo"> Info </h4>
          <div class="text">
            <div>
              <span>State: <b><i>{{state}}</i></b></span>
            </div>
            <div>
              <span> Owner:<b>{{owner}}&emsp;</b></span>
            </div>
            <div>
              <span> N. Troops:<b>{{troops}}</b>&emsp;</span>
            </div>
            <div>
              <span> Region:<b>{{region}}</b>&emsp;</span>
            </div>
          </div>
          <div class="buttons">
            </br>
            <div class="btns">
              <b-button variant="outline-info" class="insideBtn"> Schiera truppe </b-button>
              <b-button variant="outline-info" class="insideBtn"> Attacca </b-button>
            </div>
            <div class="btns">
              <b-button variant="outline-info" id ="id" class="insideBtn"> Sposta truppe </b-button>
              <b-button variant="outline-info" class="insideBtn"> Gioca bonus </b-button>
            </div>
          </div>
        </div>
      </div>
      <div id="svgMapContainer"></div>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3'

const mapsContext = require.context('@/assets/maps/', true, /\.svg$/);
const mapsExt = '.svg';

export default {
  data(){
    return {
      players: [],
      state: 'Select a state',
      owner: 'Marto',
      troops: '5',
      objective: '',
      infantryCards: Number,
      cavalryCards: Number,
      artilleryCards: Number,
      region: 'Europa',
      infantryEnable: false,
      artilleryEnable: false,
      cavalryEnable:false,
      neighbors: [],
      playerStates: [],
    }
  },
  mounted() {
    this.myRng = seedRandom(this.roomName)
    var vue = this
    var newHandler = function(evt) {
      console.log('GAME - Receive message: ' + evt.data);
      ClientGame.handleGameMessage(evt.data, vue)
    }
    this.$store.commit('changeHandler', newHandler)

    this.onLoad()
  },
  methods: {
    onLoad(){
      this.loadSvg()
      this.loadCardsInfo()
      this.addPlayers()
      this.setMap()
      this.addPlayerState()
      this.loadObjective(),
      this.setCardInfo()
    },
    addPlayer(player, myTurn){
      console.log("addPlayer")
      this.players.push({Name_Player: players, My_Turn: myTurn})
      console.log(this.players)
    },
    setMap(map){
      console.log("mappp")
      console.log(map)
    },
    cleanPlayerState() {
      console.log("cleanPlayerState")
      this.playerStates.splice(0)
    },
    addPlayerState(playerState){
      console.log("playerState " + playerState)
      this.playerStates.push({playerState})
      console.log(this.playerState)
    },
    loadObjective(obj) {
      this.objective = obj
    },
    setCardInfo(infantry, cavalry, artillery){
      this.infantryCards = infantry
      this.cavalryCards = cavalry
      this.artilleryCards = artillery
    },
    notifyError(error){
      console.log("error " + error)
    },
    getRandomColor(name) {
      var rng = seedRandom(name)
      var letters = '0123456789ABCDEF';
      var color = '#';
      for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(rng() * 16)];
      }
      return color;
    },
    bind(){
      var vue = this
      Array.prototype.forEach.call(document.getElementsByTagName("path"), function(el) {
        //set the color of el as owner color
        el.setAttribute('fill', 'blue')
        el.style.stroke = 'red'
        el.style.strokeWidth = '2'

        el.onclick = function(){ 
          if(vue.state !== 'Select a state'){
            document.getElementById(vue.state).setAttribute('fill', 'blue')
            document.getElementById(vue.state).style.opacity = 1
            document.getElementById(vue.state).style.stroke = 'red'
            document.getElementById(vue.state).style.strokeWidth = '2'
          }
          el.style.opacity = 0.7;
          el.setAttribute('fill', 'gold')
          el.setAttribute("font-size", "14") 
          el.style.stroke = 'green'
          el.style.strokeWidth = '6'
          vue.state = el.id;
        };
      })
    },
    getMapImage() {
      return mapsContext(`./italy${mapsExt}`);
    },
    loadSvg(){
      var vue = this
      var myD3 = d3;
      myD3.xml(this.getMapImage())
      .then(data => {
        var map = myD3.select("#svgMapContainer");
        var myMap = map.node().append(data.documentElement);
        this.bind()
      });

    }
  }
}
</script>

<style lang="sass" scoped>
.wrapper
  padding-top: 20px
  max-width: 98%
  margin: 0 auto
  .objective
    border: 1px solid black
    border-radius: 10px
    width: fit-content
    margin: 0 auto
    padding: .5%
  .infoContainer
    display: flex
    justify-content: space-between
    height: fit-content
    .leftContainer
      display: flex
      flex-direction: column
      max-width: 15%
      width: 15%
      .radioDiv
        border: 1px black solid
        border-radius: 10px
        flex-direction: column
        width: fit-content
        margin-left: .5%
        padding: 1% 
        .form-check
          text-align: justify
      .buttonDiv
        display: flex
        flex-direction: column
        justify-content: space-around
    .stateInfo
      display: flex
      flex-direction: column
      border: 1px black solid
      border-radius: 10px
      padding: 1%
      width: 25%
      .buttons
        .btns
          margin: 2% auto
          display: flex
          justify-content: space-between
  #svgMapContainer
    margin: -20% auto auto auto
</style>