import Vue from 'vue'
import VueRouter from 'vue-router'
import Lobby from '../components/Lobby/Lobby.vue'
import Room from '../views/Game/Room/Room.vue'
import CreateRoom from '../views/Game/CreateRoom/CreateRoom.vue'
import Game from '../views/Game/Game/Game.vue'
import Login from '../views/Login/Login.vue'
import Registration from '../views/Registration/Registration.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'lobby',
    component: Lobby
  },
  {
    path: '/create_room',
    name: 'create_room',
    component: CreateRoom
  },
  {
    path: '/room',
    name: 'room',
    component: Room
  },
  {
    path: '/game',
    name: 'game',
    component: Game
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/registration',
    name: 'registration',
    component: Registration
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
