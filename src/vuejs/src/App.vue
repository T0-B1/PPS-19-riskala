<template>
  <div id="app">
    <div id="nav">
      <navbar class="nav-bar"></navbar>
    </div>
    <router-view/>
  </div>
</template>

<script>
import Navbar from './components/Navbar/Navbar.vue';
export default {
  name: 'app',
  components: {
    navbar: Navbar,
  },
}
</script>

<style lang="sass">
  @import './components/Navbar/navbar.sass'
</style>
