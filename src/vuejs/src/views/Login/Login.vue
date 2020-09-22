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
      <span class="disabled">Not registered?</span>
      <router-link to='registration' aria-label="registration"
        class="text-center buttonsDiv" style="text-decoration:none; margin-bottom:30px;">
        <b-button role="button" variant="outline-primary">
          Register
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
  mounted(){
    var token = localStorage.riskalaToken
    if(token !== 'InvalidToken'){
      this.$store.commit('login', { token: token, user: localStorage.riskalaUser });
      this.$router.push('/')
    }
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
          this.$router.push('/')
        }).catch((error) => {
          this.$store.commit('logout');
          if (error.response) {
            if (error.response.status === 404) {
              console.error("Invalid credentials");
            } else {
              console.error("Internal server error!");
            }
          }
        });
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
