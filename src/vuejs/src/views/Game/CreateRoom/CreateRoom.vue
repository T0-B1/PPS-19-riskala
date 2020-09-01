<template>
  <div class="container">
    
      <div class="subcontainer">
        <b-card
          class="card"
          title="Crea partita">
          <div class="formInput">
            <h5 class="title">Nome partita</h5>
            <input type="text" class="form-control" placeholder="Nome della partita">
          </div>
          <hr class="divider"/>
          <div class="infoPlay">
            <div>
              <h5>Numero giocatori</h5>
              <select class="form-control">
                <option disabled value="">Select</option>
                  <option v-for="index in 10" :key="index">{{index}}</option>
              </select>
            </div>
            <div>
              <h5>Seleziona scenario</h5>
              <select class="form-control">
                <option disabled value="">Select</option>
                  <option v-for="index in 5" :key="index">{{index}}</option>
              </select>
            </div>
            <div>
              <h5>Imposta le tue regole</h5>
               <router-link to='new_rules'>
                  <b-button variant="outline-primary">Crea regole</b-button>
                </router-link>
            </div>
          </div>
          <hr class="divider"/>
          <div>
            <router-link to='room'>
              <b-button variant="outline-primary">Crea Partita</b-button>
            </router-link>
          </div>
        </b-card>
      </div>
      
    
    
  </div>
</template>

<style lang="sass">
@import './CreateRoom.sass'
</style>