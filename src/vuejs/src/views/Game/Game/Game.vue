<template>
  <div>
    <div class="wrapper">
      <div> <span> Your objective: <b><i> OBIETTIVO </i></b></span> </div>
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
            <button type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/infantry.png" width="50" height="50" alt="submit" />
            </button>
            <span>10</span>
            <button type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/cavalry.png" width="50" height="50" alt="submit" />
            </button>
            <span>10</span>
            <button type="submit" style="border: 1px solid black; border-radius:10px; background: transparent">
              <img src="@/assets/artillery.png" width="50" height="50" alt="submit" />
            </button>
            <span>10</span>
          </div>
        </div>
        <div class="stateInfo">
          <h4 id="idInfo"> Info </h4>
          <div class="text">
            <div>
              <span>State: {{state}}</span>
            </div>
            <div>
              <span> Owner:<b>{{proprietario}}&emsp;</b></span>
            </div>
            <div>
              <span> N. Troops:<b>{{truppe}}</b>&emsp;</span>
            </div>
            <div>
              <span> Region:<b>{{regione}}</b>&emsp;</span>
            </div>
          </div>
        </div>
      </div>
      <div id="svgMapContainer"></div>
        <b-img class="map-img" :src='getMapImage()'>
        </b-img>
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
      srcMap:'https://raw.githubusercontent.com/raddrick/risk-map-svg/master/risk.svg',
      players: [],
      state: 'Select a state',
      proprietario: 'Marto',
      truppe: '5',
      regione: 'Europa'
    }
  },
  mounted() {
    this.loadSvg()
    //this.addPlayers()
  },
  methods: {
    bind(){
      var vue = this
      Array.prototype.forEach.call(document.getElementsByTagName("path"), function(el) {
        el.onclick = function(){ 
          //getStateInfo da scala
          if(el.id === 'Move mouse over a country'){
            document.getElementById(vue.state).setAttribute('fill', 'fill')
            document.getElementById(vue.state).style.opacity = 1
            vue.state = 'Select a state'            
          } else{
            if(vue.state !== 'Select a state'){
              document.getElementById(vue.state).setAttribute('fill', 'fill')
              document.getElementById(vue.state).style.opacity = 1
            }
            el.style.opacity = 0.7;
            el.textContent = "aaa"
            el.setAttribute('fill', 'gold')
            el.setAttribute("font-size", "14")
            vue.state = el.id;
          }
        };
      })
    },
    addPlayers(players){
      this.players.push({Name_Player: players.name})
    },
    getMapImage() {
      return imagesContext(`./italy${mapsExt}`);
    },
    loadSvg(){
      var vue = this
      var myD3 = d3;
      myD3.xml(this.srcMap)
      .then(data => {
        var map = myD3.select("#svgMapContainer");
        map.node().append(data.documentElement)
        this.bind()
      });
    }
  }
}
</script>

<style lang="sass" scoped>
.wrapper
  padding-top: 20px
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
      padding: 1%
      width: 15%
  #svgMapContainer
    margin: -20% auto auto -5%
</style>