<template>
  <div class="container">
      <div class="subcontainer">
        <b-card
          class="card"
          title="Create Room">
          <div class="formInput">
            <h5 class="title">Room Name</h5>
            <input type="text" v-model="nomePartita" class="form-control" placeholder="Room Name" required>
          </div>
          <hr class="divider"/>
          <div class="infoPlay">
            <div>
              <h5>Number of players</h5>
              <input type="number" v-model="numeroGiocatori" class="form-control" placeholder="Number of players" required>
            </div>
            <div>
              <h5>Select scenario</h5>
              <b-form-select v-model="selectedScenario" :options="options">
                <template v-slot:first>
                  <b-form-select-option :value="null" disabled>Select scenario</b-form-select-option>
                </template>
              </b-form-select>
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
          <b-button variant="outline-primary" @click="createRoom">Create Game</b-button>
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
      selectedScenario: null,
      error: '',
      passed: false,
      options: [{value: 'Europe', text: 'Europe'}],
    }
  },
  mounted() {
    var vue = this
    var newHandler = function(evt) {
      console.log('CREATEROOM - Receive message: ' + evt.data);
      ClientCreateRoom.handleCreateMessage(evt.data, vue)
    }
    this.$store.commit('changeHandler', newHandler)
  },
  methods: {
    checkForm(){
      if(this.nomePartita !== '' && this.numeroGiocatori > 1 && this.selectedScenario !== '') {
        this.$bvModal.hide('modal-error')
        this.passed = true
      } else {
          if(this.numeroGiocatori < 2) {
            this.error = 'Number of players cannot be minor then 2.'
          } else {
              if(!this.nomePartita) {
                this.error = 'Room name cannot be empty.'
              } else {
                if(!this.selectedScenario) {
                  this.error= 'Scenarion cannot be empty'
                }
              }
          }
        this.passed = false
        this.$bvModal.show('modal-error')
      }
    },
    notifyCreateError(error) {
      console.log("inside notifyCreateError")
      this.error = error
      this.$bvModal.show('modal-error')
    },
    createRoom() {
      this.checkForm();
      if(this.passed) {
        
        console.log("createRoom inside")
        this.$store.state.websocket.send(
          ClientCreateRoom.getCreateMsgWrapped(this.nomePartita, this.numeroGiocatori, this.selectedScenario))
        //this.$router.push('/room')
      }      
    }
  }

}
</script>

<style lang="sass">
@import './CreateRoom.sass'
</style>