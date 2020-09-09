<template>
  <div class="login">
    <b-card
      img-alt="Image"
      img-top
      tag="article"
      class="cardLogin"
    >
      <b-form @submit="onSubmit" class="formIns">
        <div class="grey-text">
          <b-form-group
            id="input-group-email"
            label="Username:"
            label-for="input-email"
          >
            <b-form-input
              id="input-email"
              v-model="form.username"
              type="text"
              required
              placeholder="Inserisci username"
              aria-describedby="email-help-block"
            ></b-form-input>
          </b-form-group>
          <b-form-group
            id="input-group-password"
            label="Password:"
            label-for="input-password"
          >
            <b-form-input
              id="input-password"
              v-model="form.password"
              type="password"
              required
              placeholder="Inserisci password"
              aria-describedby="password-help-block"
            ></b-form-input>
            <button id="buttonHideShow"
              role="button" title="Clicca per mostrare la tua password"
              @click="changeType" type = "button"></button>
          </b-form-group>
        </div>
        <div class="text-center buttonsDiv block">
          <b-button role="button" variant="outline-primary" type="submit">Login</b-button>
        </div>
      </b-form>
      <hr />
      <span class="disabled">Non sei ancora registrato?</span>
      <router-link to='registration' aria-label="registration"
        class="text-center buttonsDiv" style="text-decoration:none; margin-bottom:30px;">
        <b-button role="button" variant="outline-primary">
          Registrati
        </b-button>
      </router-link>
    </b-card>
  </div>
</template>

<script>

export default {
  name: 'login',
  data() {
    return {
      form: {
        username: '',
        password: '',
      },
    };
  },
  methods: {
    onSubmit(evt) {
      evt.preventDefault();
      const username = this.form.username;
      const psw = this.form.password;

      if(username.length != 0 && psw.length != 0 ) {
        this.$store.state.http.post('login', { username: username, password: psw })
        .then((response) => {
          const t = response.data;
          this.$store.commit('login', { token: t, user: username });
          this.openSocket(t)
        }).catch((error) => {
          this.$store.commit('logout');
          this.$websocket.close();
          if (error.response) {
            if (error.response.status === 404) {
              console.log("Invalid credentials");
            } else {
              console.log("Internal server error!");
            }
          }
        });
      }
    },
    openSocket(token){
      var vue = this
      var HOST = location.origin.replace(/^http/, 'ws')
      this.$store.websocket = new WebSocket(HOST + "/websocket?token=" + token)
      this.$store.websocket.onopen = function() { onOpen(vue) };
      this.$store.websocket.onclose = function() { onClose() };
      this.$store.websocket.onmessage = function(evt) { onMessage(evt) };
      this.$store.websocket.onerror = function(evt) { onError(evt) };

      function onOpen(vue) {
        console.log("CONNECTED");
        vue.$router.push('/')
      }

      function onClose() {
        console.log("DISCONNECTED");
        token = "InvalidToken"
      }

      function onMessage(evt) {
        console.log('LOGIN - MSG recv: ' + evt.data);
      }

      function onError(evt) {
        console.log('WS ERROR' + evt.data);
      }
    },
    changeType() {
      const t = document.getElementById('input-password').type;
      if (t === 'text') {
        document.getElementById('input-password').type = 'password';
        document.getElementById('buttonHideShow').style = 'background-position: 0px 0px';
      } else {
        document.getElementById('input-password').type = 'text';
        document.getElementById('buttonHideShow').style = 'background-position: -44px 0px';
      }
    },
  },
};
</script>

<style lang="sass">
  @import './login.sass'
</style>
