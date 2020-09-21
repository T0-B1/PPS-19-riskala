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
  console.log(oldSocket)
  if(oldSocket != null && oldSocket.readyState === WebSocket.OPEN) {
    console.log("Socket already open")
    console.log(oldSocket)
    return oldSocket
  }
  console.log("Opening socket")
  var vue = this
  var HOST = location.origin.replace(/^http/, 'ws')
  var mySocket = new WebSocket(HOST + "/websocket?token=" + token)
  mySocket.onopen = function() { onOpen() };
  mySocket.onclose = function() { onClose() };
  mySocket.onmessage = function(evt) { onMessage(evt) };
  mySocket.onerror = function(evt) { onError(evt) };
  //this.$store.commit('openWebsocket', mySocket)

  function onOpen() {
    console.log("CONNECTED");
  }

  function onClose() {
    console.log("DISCONNECTED");
    token = "InvalidToken"
  }

  function onMessage(evt) {
    console.log('MSG received');
  }

  function onError(evt) {
    console.log('WS ERROR');
  }

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
      localStorage.riskalaToken = newState.token;
      localStorage.riskalaUser = newState.user;
      state.http = Axios.create({
        timeout: 10000,
        headers: { token: newState.token },
      });
    },
    logout(state) {
      state.isLogged = false;
      localStorage.riskalaToken = 'InvalidToken';
      localStorage.riskalaUser = '';
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
      state.websocket = openSocket(state.websocket, localStorage.riskalaToken);
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

if (localStorage.riskalaToken === undefined) {
  localStorage.riskalaToken = 'InvalidToken';
}

if (localStorage.riskalaToken !== 'InvalidToken') {
  const t = localStorage.riskalaToken;
  const u = localStorage.riskalaUser;
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
    console.log("goin to "+to+" redirect /")
    next('/');
  } else if (loggedRoute.includes(p) && !store.state.isLogged) {
    console.log("goin to "+to+" redirect /login")
    next('/login');
  } else {
    console.log("goin to "+to)
    next();
  }
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
