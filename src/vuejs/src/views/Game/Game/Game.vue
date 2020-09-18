<template>
  <div>
    <div class="wrapper">
      <div class="objective"> <span> Your objective is: <b><i> {{objective}} </i></b></span> </div>
      <div class="infoContainer">
        <div class="leftContainer">
          <div class="radioDiv">
            <h5> Turn of: </h5>
            <ul id="listPlayers">
              <li v-for="(player,index) in players" :key="index">
                <label>{{player.nome_giocatore}}</label>
              </li>
            </ul>
            <b-button variant="danger"> End Turn </b-button>
          </div>
          <hr/>
          <div class="buttonDiv" >
            <button :disabled='isInfEnable' class="submitBtn" type="submit" @click="handleEvent">
              <img id="submitBtnI" src="@/assets/buttonsImg/infantry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{infantryCards}}</span>
            <button :disabled="isCavEnable" class="submitBtn" type="submit" @click="handleEvent">
              <img id="submitBtnC" src="@/assets/buttonsImg/cavalry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{cavalryCards}}</span>
            <button :disabled="isArtEnable" class="submitBtn" type="submit" @click="handleEvent">
              <img id="submitBtnA" src="@/assets/buttonsImg/artillery.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{artilleryCards}}</span>
          </div>
        </div>
        <div class="stateInfo">
          <h4 id="idInfo"> Info </h4>
          <div class="textInfo">
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
          <div v-if="visible == true" class="action">
            <h4> Choose one state </h4>
            <div v-for="(neighbor,index) in neighbors" :key="index" class="form-check">
              <input type="radio" :id="neighbor.id" :checked:"neighbor.checked">
              <label :for="neighbor.id">{{neighbor.neighbor_name}}</label>
            </div>
          </div>
        </div>
      </div>
      <div id="svgMapContainer"></div>
      <hr/>
      <b-button class="leaveBtn" variant="outline-danger" @click="leave">Leave Game</b-button>
    </div>
    <b-modal id="modal-error" auto-focus-button="ok" ok-only title="Error Message">
      <p class="my-4"><i>{{this.error}}</i></p>
    </b-modal>
  </div>
</template>

<script>
import * as d3 from 'd3'
var seedRandom = require('seedrandom')
const mapsContext = require.context('@/assets/maps/', true, /\.svg$/);
const mapsExt = '.svg';

export default {
  data(){
    return {
      players: [],
      state: 'Select a state',
      owner: '',
      troops: '',
      objective: '',
      infantryCards: 0,
      cavalryCards: 4,
      artilleryCards: 0,
      region: '',
      infantryEnable: false,
      artilleryEnable: false,
      cavalryEnable:false,
      neighbors: [],
      visible: false,
    }
  },
  computed: {
    isInfEnable: function() {
      return !this.infantryEnable
    },
    isCavEnable: function() {
      return !this.cavalryEnable
    },
    isArtEnable: function() {
      return !this.artilleryEnable
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
    leave(){
      console.log("leave")
      ClientGame.getEmptyMsgWrapped("LeaveMessage")
    },
    onLoad(){
      this.loadSvg()
      /*this.loadCardsInfo()
      this.addPlayers()
      this.setMap()
      this.addPlayerState()
      this.loadObjective(),*/
      this.setCardInfo()
    },
    addNeighbors(neighbor, checked){
      this.neighbors.push({neighbor_name: neighbor, checked: checked})
    },
    setStateInfo(nameState, owner, troops, region){
      this.state = nameState
      this.owner = owner
      this.troops = troops
      this.region = region
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
      //this.infantryCards = infantry
      this.infantryCards = 0
      this.checkInfantry()
      //this.cavalryCards = cavalry
      this.cavalryCards = 4
      this.checkCavalry()
      //this.artilleryCards = artillery
      this.artilleryCards = 0
      this.checkArtillery()
    },
    notifyError(error){
      console.log("error " + error)
    },
    checkInfantry() {
      if(this.infantryCards >= 3) {
        this.infantryEnable = true
        document.getElementById("submitBtnoI").style.opacity = 1
      } else {
        this.infantryEnable = false
        document.getElementById("submitBtnI").style.opacity = 0.4
      }
    },
    checkCavalry(){
      if(this.cavalryCards >= 3) {
        this.cavalryEnable = true
        document.getElementById("submitBtnC").style.opacity = 1
      } else {
        this.cavalryEnable = false
        document.getElementById("submitBtnC").style.opacity = 0.4
      }
    },
    checkArtillery() {
      if(this.artilleryCards >= 3) {
        this.artilleryEnable = true
        document.getElementById("submitBtnA").style.opacity = 1
      } else {
        this.artilleryEnable = false
        document.getElementById("submitBtnA").style.opacity = 0.4
      }
    },
    handleEvent(){
      /*Aggiungi parametri al metodo: fromState, toState, numTroops */
      if(localStorage.riskalaUser != ""){
        ClientGame.getActionMsgWrapped(localStorage.riskalaUser)
      }
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
          if(vue.state !== 'Select a state' || vue.state == el.id){
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
          ClientGame.clickedState(vue.state, localStorage.riskalaUser, vue)
        };
      })
    },
    notifyGameError(error){
      this.error = error
      this.$bvModal.show('modal-error')
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

<style lang="sass">
@import './Game.sass'
</style>