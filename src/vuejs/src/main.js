import Vue from 'vue'
import App from './App.vue'
import router from './router'
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
  },
  mutations: {
    login(state) {
      state.isLogged = true;
    },
    logout(state) {
      state.isLogged = false;
    }
  }
});

router.beforeEach((to, from, next) => {
  const realRoute = ['/', '/create_room', '/room', '/game', '/login', '/registration'];
  const loggedRoute = ['/create_room', '/room', '/game', '/'];
  const p = to.path;
  if (!realRoute.includes(p)) {
    next('/');
  } else if (loggedRoute.includes(p) && store.state.isLogged === 'false') {
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
