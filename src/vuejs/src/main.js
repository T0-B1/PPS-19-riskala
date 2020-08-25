import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Axios from 'axios'
import Vuex from 'vuex';
import './custom.sass'
import BootstrapVue from 'bootstrap-vue'

// Install BootstrapVue
Vue.use(BootstrapVue)
Vue.use(Vuex);

Vue.config.productionTip = false


const store = new Vuex.Store({
  state: {
    isLogged: false,
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
      state.http = Axios.create({
        timeout: 10000,
        headers: { token: 'InvalidToken' },
      })
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
  /*store.state.http.get('api/checkOldToken')
    .then((res) => {
      if (res.data !== 'Ok') {
        store.commit('logout');
      }
    }).catch(() => {
      store.commit('logout');
    });*/
} else {
  //store.commit('logout');
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
