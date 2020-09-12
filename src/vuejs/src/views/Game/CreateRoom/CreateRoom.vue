<template>
  <div class="container">
      <div class="subcontainer">
        <b-card
          class="card"
          title="Crea partita">
          <div class="formInput">
            <h5 class="title">Nome Stanza</h5>
            <input type="text" v-model="nomePartita" class="form-control" placeholder="Nome della partita" required>
          </div>
          <hr class="divider"/>
          <div class="infoPlay">
            <div>
              <h5>Numero giocatori</h5>
              <input type="number" v-model="numeroGiocatori" class="form-control" placeholder="Nome della partita" required>
            </div>
            <div>
              <h5>Seleziona scenario</h5>
              <b-form-select v-model="selected" :options="options"></b-form-select>
            </div>
            <!--<div>
              <h5>Imposta le tue regole</h5>
               <router-link to='new_rules'>
                  <b-button variant="outline-primary">Crea regole</b-button>
                </router-link>
            </div>-->
          </div>
          <hr class="divider"/>
          <div>
          <b-button variant="outline-primary" @click="createGame">Crea Partita</b-button>
          </div>
        </b-card>
      </div>
      <b-modal id="modal-error" auto-focus-button="ok" ok-only title="Error Message">
        <p class="my-4"><i>{{this.error}}</i></p>
      </b-modal>
  </div>
</template>

<script>
export default {
  name:'CreateRoom',
  data() {
    return {
      nomePartita: '',
      numeroGiocatori: 4,
      selected: '',
      error: '',
      passed: false,
      options: [{text: 'Europa'}],
    }
  },
  methods: {
    checkForm(){
      if(this.nomePartita !== '' && this.numeroGiocatori != 0 && this.selected !== '') {
        this.$bvModal.hide('modal-error')
        this.passed = true
      } else {
          if(this.numeroGiocatori == 0) {
            this.error = 'Numero di giocatori non può essere 0.'
          } else {
              if(!this.nomePartita) {
                this.error = 'Devi inserire il nome della partita.'
              } else {
                if(!this.selected) {
                  this.error= 'Devi selezionare lo scenario su cui giocare.'
                }
              }
          }
          this.passed = false
        this.$bvModal.show('modal-error')
      }
    },
    createGame() {
      this.checkForm()
      if(this.passed) {
        //chiama wrapped message e manda i dati 
        this.$router.push('/room')
      }      
    }
  }

}
</script>

<style lang="sass">
@import './CreateRoom.sass'
</style>