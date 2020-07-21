<template>
  <div>
    <div class="containerDiv">
      <div class="link">
        <b-link class="home" href="/">{{navhome}}</b-link>
      </div>
      <b-link class="login" href="/login">Login</b-link> 
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
    .login:hover
      text-decoration: none
  
</style>
