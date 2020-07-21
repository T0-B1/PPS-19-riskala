import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../components/Home/Homepage.vue'
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
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
