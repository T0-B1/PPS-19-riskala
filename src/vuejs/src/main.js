import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Axios from 'axios'
import Vuex from 'vuex';
import './custom.sass'
import BootstrapVue from 'bootstrap-vue'

Vue.use(BootstrapVue)
Vue.use(Vuex);

Vue.config.productionTip = false

function openSocket(oldSocket, token){
  if(oldSocket != null && oldSocket.readyState === WebSocket.OPEN) {
    return oldSocket
  }
  var vue = this
  var HOST = location.origin.replace(/^http/, 'ws')
  var mySocket = new WebSocket(HOST + "/websocket?token=" + token)
  return mySocket
}

const store = new Vuex.Store({
  state: {
    websocket: null,
    isLogged: false,
    roomInfo: '',
    gameInfo: '',
    lobbyInfo: '',
    http: Axios.create({
      timeout: 10000,
      headers: { token: 'InvalidToken' },
    }),
  },
  mutations: {
    login(state, newState) {
      state.isLogged = true;
      sessionStorage.riskalaToken = newState.token;
      sessionStorage.riskalaUser = newState.user;
      state.http = Axios.create({
        timeout: 10000,
        headers: { token: newState.token },
      });
    },
    logout(state) {
      state.isLogged = false;
      sessionStorage.riskalaToken = 'InvalidToken';
      sessionStorage.riskalaUser = '';
      state.websocket = null;
      state.http = Axios.create({
        timeout: 10000,
        headers: { token: 'InvalidToken' },
      })
    },
    openWebsocket(state, token) {
      state.websocket = openSocket(state.websocket, token);
      // For debug purposes
      Window.websocket = state.websocket;
    },
    changeHandler(state, newHandler) {
      state.websocket = openSocket(state.websocket, sessionStorage.riskalaToken);
      state.websocket.onmessage = newHandler;
    },
    changeRoomInfo(state, newRoom){
      state.roomInfo = newRoom;
    },
    changeGameInfo(state, newGame){
      state.gameInfo = newGame
    },
    changeLobbyInfo(state, newLobby){
      state.lobbyInfo = newLobby
    }
  }
});

if (sessionStorage.riskalaToken === undefined) {
  sessionStorage.riskalaToken = 'InvalidToken';
}

if (sessionStorage.riskalaToken !== 'InvalidToken') {
  const t = sessionStorage.riskalaToken;
  const u = sessionStorage.riskalaUser;
  store.commit('login', { token: t, user: u });
  store.state.http.post('login', { username: u, password: '' })
  .then((response) => {
    store.commit('login', { token: response.data , user: u });
  }).catch(() => {
    store.commit('logout');
  });
} else {
  store.commit('logout');
}


router.beforeEach((to, from, next) => {
  const realRoute = ['/', '/create_room', '/room', '/game', '/login', '/registration'];
  const loggedRoute = ['/create_room', '/room', '/game', '/'];
  const p = to.path;
  if (!realRoute.includes(p)) {
    next('/');
  } else if (loggedRoute.includes(p) && !store.state.isLogged) {
    next('/login');
  } else {
    next();
  }
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
