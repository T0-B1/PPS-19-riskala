<template>
  <div>
    <div class="containerDiv">
      <div class="link">
        <router-link class="home" to="">{{navhome}}</router-link>
      </div>
      <b-button v-if="this.$store.state.isLogged === false" class="login" to="/login">Login</b-button> 
      <b-button v-else class="login" role="button" @click="change">Logout</b-button>
    </div>
  </div>
</template>

<script>

export default {
  name: 'navbar',
  data() {
    return {
      navhome: 'Riskala',
    };
  },
  methods: {
    change() {
      this.$store.commit('logout')
      this.$store.state.websocket.send(ClientRoom.getMsgWrapped("LogoutMessage"))
      this.$router.push('/login')
    }
  }
};
</script>

<style lang="sass" scoped>
.nav-bar
  background-color:  lightblue
  padding: 10px
  .containerDiv
    display: flex
    justify-content: space-between
    .home
      display: flex
      justify-content: flex-start
    .home:hover
      text-decoration: none
    .login
      display: flex
      justify-content: flex-end
      padding: 0
      margin: 0
      background: transparent
      border: transparent
      color: #007bff
    .login:hover
      text-decoration: none
      color: #0067d6
  
</style>
