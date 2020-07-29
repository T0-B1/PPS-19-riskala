<template>
<div class="container">
  <div class="subcontainer">
    <h1> Lobby </h1>
    <b-table striped hover :items="items" @row-clicked="myRowClickHandler"></b-table>
  </div>
  <hr class="divider"/>
  <div class="buttons_div">
    <router-link to='create_room'>
      <b-button variant="outline-primary" >Crea partita</b-button>
    </router-link>
    <router-link to='room'>
      <b-button id="joinBtn" variant="outline-primary" v-bind:disabled="disabled">Join partita</b-button>
    </router-link>
    <b-button variant="outline-primary">Carica partita</b-button>
  </div>
</div>
</template>

<script>
  export default {
    data() {
      return {
        disabled:true,
        join: '',
        items: [
          { Nome_Partita: 'Classic', Giocatori: '3/8'},
          { Nome_Partita: 'Small', Giocatori: '2/4'},
          { Nome_Partita: 'Random', Giocatori: '1/5'},
          { Nome_Partita: '1Vs1', Giocatori: '2/2'}
        ]
      }
    },
    methods: {
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