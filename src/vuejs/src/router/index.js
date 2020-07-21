import Vue from 'vue'
import VueRouter from 'vue-router'
import Lobby from '../components/Lobby/Lobby.vue'
import Room from '../views/Game/Room/Room.vue'
import CreateRoom from '../views/Game/CreateRoom/CreateRoom.vue'

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
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
