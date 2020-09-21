<template>
  <div>
    <div class="wrapper">
      <div class="objective"> 
        <span> Your objective is: <b><i> {{objective}} </i></b></span>
        <span class="winner" v-if="isEnded === true"> The winner is: <b><i>{{winnerPlayer}}</i></b></span>
      </div>
      <div class="infoContainer">
        <div class="leftContainer">
          <div class="radioDiv">
            <h5> Turn of: </h5>
            <ul id="listPlayers">
              <li v-for="(player,index) in players" :key="index">
                <label v-bind:style="[player.My_Turn ? {'color': getRandomColor(player.Name_Player), 'font-weight':'bold'} : 
                {'color': 'black', 'font-weight': 'normal'}]">
                  {{player.Name_Player}}</label>
              </li>
            </ul>
            <b-button :disabled="isNotMyTurn" variant="danger" @click="endTurn"> End Turn </b-button>
          </div>
          <hr/>
          <div class="buttonDiv" >
            <button class="submitBtn" id="btnInf" type="submit" @click="redeemBonus('Infantry')" 
            v-bind:style="[isInfEnable ? opacity = 1 : opacity = 0.4]">
              <img id="submitBtnI" src="@/assets/buttonsImg/infantry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{infantryCards}}</span>
            <button class="submitBtn" id="btnCav" type="submit" @click="redeemBonus('Cavalry')"
            v-bind:style="[isCavEnable ? opacity = 1 : opacity = 0.4]">
              <img id="submitBtnC" src="@/assets/buttonsImg/cavalry.png" width="50" height="50" alt="submit" />
            </button>
            <span>{{cavalryCards}}</span>
            <button class="submitBtn" id="btnArt" type="submit" @click="redeemBonus('Artillery')"
            v-bind:style="[isArtEnable ? opacity = 1 : opacity = 0.4]">
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
              <input type="radio" :id="neighbor.id" :checked="neighbor.checked" name="selectedNeighbor" 
              @click="neighborRadioSelection(neighbor.neighbor_name)">
              <label v-model="selectedNeighbor" :for="neighbor.id">{{neighbor.neighbor_name}}</label>
            </div>
            <h4> How many troops? </h4>
            <input id="inputTroop" type="number" :min="minAvailableTroops" :max="maxAvailableTroops" v-model="troopsDeployed" @focusout="handleFocus" number></br></br>
            <span> Troops for {{nameActionBtn.toLowerCase()}}: {{maxAvailableTroops}}</span></br></br>
            <b-button :disabled="isNotMyTurn" @click="actionOnMap">{{nameActionBtn}}</b-button>
          </div>
        </div>
      </div>
      <b-button id="leaveButton" class="leaveBtn" variant="outline-danger" @click="leave">Leave Game</b-button>
      <div id="svgMapContainer"></div>
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
      myName: localStorage.riskalaUser,
      players: [],
      state: 'Select a state',
      owner: '',
      troops: '',
      objective: '',
      infantryCards: 0,
      cavalryCards: 0,
      artilleryCards: 0,
      region: '',
      neighbors: [],
      visible: false,
      maxAvailableTroops: '',
      nameActionBtn: 'Deploy',
      troopsDeployed: 0,
      selectedNeighbor: '',
      winnerPlayer: '',
      isEnded: false,
      isNotMyTurn: true,
      minAvailableTroops: 0
    }
  },
  mounted() {
    var vue = this
    var myD3 = d3;
    myD3.xml(this.getMapImage())
    .then(data => {
      var map = myD3.select("#svgMapContainer");
      var myMap = map.node().append(data.documentElement);
      this.bind()
      var newHandler = function(evt) {
        console.log('GAME - Receive message');
        ClientGame.handleGameMessage(evt.data, vue)
      }
      this.$store.commit('changeHandler', newHandler)
        if(vue.$store.state.gameInfo !== ''){
        ClientGame.setupGame(vue.$store.state.gameInfo, vue)
      }
    });
  },
  methods: {
    neighborRadioSelection(radioSelection){
      this.selectedNeighbor = radioSelection
      ClientGame.neighborClick(this.selectedNeighbor, localStorage.riskalaUser, this.state, this)
    },
    handleFocus(){
      if(this.troopsDeployed > this.maxAvailableTroops)
        this.troopsDeployed = this.maxAvailableTroops
      if(this.troopsDeployed < this.minAvailableTroops) 
        this.troopsDeployed = this.minAvailableTroops
    },
    endTurn(){
      this.visible = false
      this.$store.state.websocket.send(ClientGame.getEmptyMsgWrapped("EndTurnMessage"))
      this.selectedNeighbor = ""
      this.neighbors.splice(0)
      document.getElementById(this.state).style.opacity = 1
      this.setStateInfo('Select a state', '', '', '')
    },
    leave(){
      this.$store.state.websocket.send(ClientGame.getEmptyMsgWrapped("LeaveMessage"))
    },    
    addPlayer(player, myTurn){
      this.players.push({Name_Player: player, My_Turn: myTurn})
      if(player === this.myName){
        this.isNotMyTurn = !myTurn
      } 
    },
    setCurrentPlayer(player){
      this.players.forEach(pl => pl.My_Turn = false)
      this.players.find( function(p){
        return p.Name_Player === player
      }).My_Turn = true
      if(player === this.myName){
        this.isNotMyTurn = false
      }else{
        this.isNotMyTurn = true
      }
    },
    setCardInfo(infantry, cavalry, artillery){
      this.infantryCards = infantry
      if(this.infantryCards >= 3) {
         document.getElementById('submitBtnI').style.opacity = 1
         document.getElementById('btnInf').disabled = false
      } else{
        document.getElementById('submitBtnI').style.opacity = 0.4
        document.getElementById('btnInf').disabled = true
      }
      this.cavalryCards = cavalry
      if(this.cavalryCards >= 3) {
         document.getElementById('submitBtnC').style.opacity = 1
         document.getElementById('btnCav').disabled = false
      } else{
        document.getElementById('submitBtnC').style.opacity = 0.4
        document.getElementById('btnCav').disabled = true
      }
      this.artilleryCards = artillery
      if(this.artilleryCards >= 3) {
         document.getElementById('submitBtnA').style.opacity = 1
         document.getElementById('btnArt').disabled = false
      } else{
        document.getElementById('submitBtnA').style.opacity = 0.4
        document.getElementById('btnArt').disabled = true
      }
      if(this.isNotMyTurn){
        document.getElementById('submitBtnI').style.opacity = 0.4
        document.getElementById('btnInf').disabled = true
        document.getElementById('submitBtnC').style.opacity = 0.4
        document.getElementById('btnCav').disabled = true
        document.getElementById('submitBtnA').style.opacity = 0.4
        document.getElementById('btnArt').disabled = true
      }
    },
    setObjective(obj) {
      this.objective = obj
    },
    setStateInfo(nameState, owner, troops, region){
      this.state = nameState
      this.owner = owner
      this.troops = troops
      this.region = region
    },
    addNeighbor(neighbor, checked){
      this.neighbors.push({neighbor_name: neighbor, checked: checked})
    },
    setPlayerState(playerState,owner,troops){
      document.getElementById(playerState).setAttribute("fill", this.getRandomColor(owner))
      document.getElementById("T_"+playerState).innerHTML = troops
      document.getElementById("T_"+playerState).setAttribute("fill", 'black')
    },
    setStateRegion(state, region) {
      document.getElementById(state).style.stroke = this.getRandomColor(region)
      document.getElementById(state).style.strokeWidth = '4'
    },
    redeemBonus(cardType){
      this.$store.state.websocket.send(ClientGame.getRedeemBonusMsgWrapped(cardType))
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
        el.onclick = function(){
          if(vue.state !== 'Select a state'){
            document.getElementById(vue.state).style.opacity = 1
            vue.neighbors.splice(0)
            vue.setStateInfo('Select a state', '', '', '')
          }
          if(el.id !== 'Select a state'){
            document.getElementById(el.id).style.opacity = 0.5
            vue.state = el.id;
            ClientGame.clickedState(vue.state, localStorage.riskalaUser, vue)
          } else {
            vue.state = "Select a state"
            vue.visible = false
            vue.neighbors.splice(0)
            vue.setStateInfo(vue.state, '', '', '')
          }
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
    },
    actionOnMap(){
      this.$store.state.websocket.send(
        ClientGame.getActionMsgWrapped(this.nameActionBtn, this.state, this.selectedNeighbor, parseInt(this.troopsDeployed)))
      this.troopsDeployed = 0
      this.selectedNeighbor = ""
      this.neighbors.splice(0)
    },
    setWinner(winner){
      this.isEnded = true
      this.winnerPlayer = winner
      document.getElementsByTagName("button").forEach(function(input) {
        input.disabled = true
      })
      document.getElementById("loginLogout").disabled = false
      document.getElementById("leaveButton").disabled = false
    },
    goToLobby(lobbyInfo){
      this.$store.commit('changeLobbyInfo', lobbyInfo)
      this.$store.commit('changeGameInfo', '')
      this.$router.push('/')
    }
  }
}
</script>

<style lang="sass">
@import './Game.sass'
</style>