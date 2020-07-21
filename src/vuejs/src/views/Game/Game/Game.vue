<template>
  <div>
    <div class="myContainer">
      <div class="playTurnAndMap">
        <div class="radioDiv">
          <h5> Turno </h5>
          <div v-for="(player,index) in players" :key="index" class="form-check">
            <input type="radio" :id="player.id" :checked="player.checked">
            <label :for="player.id">{{player.nome_giocatore}}</label>
          </div>
          <b-button variant="danger"> Fine turno </b-button>
        </div>
        <div id="svgMapContainer"></div>
      </div>
    </div>
    <hr class="divider">
    <div>
      <h4> Info </h4>
      <div class="containerInfo">
        <div class="text">
          <div>
            <span> Stato:<b>{{stato}}&emsp;</b></span>
            <span> Proprietario:<b>{{proprietario}}&emsp;</b></span>
          </div>
          <div>
            <span> N. Truppe:<b>{{truppe}}</b>&emsp;</span>
            <span> Regione:<b>{{regione}}</b>&emsp;</span>
          </div>
        </div>
        <div class="buttons">
          <div class="btns">
            <b-button variant="outline-info" class="insideBtn"> Schiera truppe </b-button>
            <b-button variant="outline-info" class="insideBtn"> Attacca </b-button>
          </div>
          <div class="btns">
            <b-button variant="outline-info" class="insideBtn"> Sposta truppe </b-button>
            <b-button variant="outline-info" class="insideBtn"> Gioca bonus </b-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
//con mounted chiamo API se num max giocatori
import * as d3 from 'd3'
export default {
 data(){
   return {
     srcMap:'https://raw.githubusercontent.com/raddrick/risk-map-svg/master/risk.svg',
     stato: 'Italia',
     proprietario: 'Marto',
     truppe: '5',
     regione: 'Europa',
     players: [
       {nome_giocatore: "Giordo", id:"01",colore:'red', checked:true},
       {nome_giocatore: "Ale", id:"02", colore:'blue', checked:false},
       {nome_giocatore: "Marto", id:"03", colore:'yellow', checked:false}
     ]
   }
 },
 mounted() {
   this.loadSvg()
 },
 methods: {
  bind(){
    Array.prototype.forEach.call( document.getElementsByTagName("path"), function(el) {
      el.onclick = function(){ 
        alert(el.id)
        this.stato = el.id;
        console.log(this.stato)
        };
    })
  },
  loadSvg(){
    d3.xml(this.srcMap)
    .then(data => {
      d3.select("#svgMapContainer").node().append(data.documentElement);
      this.bind();
    });
    
  }
 }
}
</script>

<style lang="sass" scoped>
.myContainer
  max-width: 95%
  padding-top: 40px
  margin: 0 auto
  .playTurnAndMap
    .radioDiv
      max-width: 20%
      border: 1px solid black
      padding: 2px
      .form-check
        text-align: justify
    #svgMapContainer
      margin-top: -11em
.containerInfo
  display: flex
  justify-content: space-around
  padding-bottom: 30px
  .text
    padding: 10px
    max-width: 40%
    line-height: 2em
    letter-spacing: 2px
  .buttons
    display: flex
    max-width: 60%
    flex-direction: column
    .btns
      display: flex
      justify-content: space-between
      .insideBtn
      margin: 5px 
</style>