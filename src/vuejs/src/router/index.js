import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../components/Home/Homepage.vue'
import Join from '../views/Game/loadGame/loadGame.vue'
import newGame from '../views/Game/newGame/newGame.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/new_game',
    name: 'new_game',
    component: newGame
  },
  {
    path: '/load_game',
    name: 'load_game',
    component: Join
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
