<template>
  <div class="container">
    
      <div class="subcontainer">
        <b-card
          class="card"
          title="Stanza partita">
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
                  <td>{{player.nome_giocatore}}</td>
                  <td>
                    <div class="square" :style="{backgroundColor:player.colore, height:'10px',width:'10px', margin:'0 auto'}"></div>
                  </td>
                </tr>
              </tbody>
            </table>
            <b-button class="readyBtn" variant="outline-primary" v-on:click="start">Ready</b-button>
          </div>
          <hr class="divider"/>
          <div id="svgMapContainer">
          </div>
          <router-link to='/'>
            <b-button id="joinBtn" variant="outline-danger">Abbandona partita</b-button>
          </router-link>
        </b-card>
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
     titleTable: [
       {prima_colonna: "Nome giocatore", seconda_colonna: "Colore"}
     ],
     players: [
       {nome_giocatore: "Giordo", colore:'red'},
       {nome_giocatore: "Ale", colore:'blue'},
       {nome_giocatore: "Marto", colore:'yellow'}
     ]
   }
 },
 mounted() {
   this.loadSvg()
 },
 methods: {
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
  start() {
    setTimeout(function(){ window.location.href = "/new_play";}, 1000);
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