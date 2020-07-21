<template>
  <div class="login">
    <b-card
      img-alt="Image"
      img-top
      tag="article"
      class="cardLogin"
    >
      <!--<img src="../../assets/ecology.png" class="loginImg" alt="logo"/>-->
      <b-form @submit="onSubmit" class="formIns">
        <div class="grey-text">
          <b-form-group
            id="input-group-email"
            label="Email:"
            label-for="input-email"
          >
            <b-form-input
              id="input-email"
              v-model="form.email"
              type="email"
              required
              placeholder="Inserisci email"
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
          <router-link to='/' aria-label="home">
            <b-button role="button" variant="outline-primary"  type="submit">Login</b-button>
          </router-link>
        </div>
      </b-form>
      <hr />
      <span class="disabled">Non sei ancora registrato?   </span>
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
        email: '',
        password: '',
      },
    };
  },
  methods: {
    onSubmit(evt) {
      evt.preventDefault();
      /*const mail = this.form.email;
      const psw = this.form.password;

      this.$store.state.http.post('api/auth', { email: mail, key: psw })
        .then((response) => {
          const t = response.data.token.toString();
          const u = response.data.user.toString();
          this.$store.commit('login', { token: t, user: u });
          this.$router.push('/meals');
        }).catch((error) => {
          if (error.response) {
            if (error.response.status === 401) {
              this.$root.$emit('openModalError', 'unauthorizedTitle', 'unauthorized');
            } else {
              this.$root.$emit('openModalError', 'internal_server_errorTitle', 'internal_server_error');
            }
          } else {
            this.$root.$emit('openModalError', 'noAnswerTitle', 'noAnswer');
          }
        });*/
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
