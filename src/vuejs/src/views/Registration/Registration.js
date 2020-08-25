export default {
  name: 'registration',
  data() {
    return {
      form: {
        email: '',
        password: '',
        repassword: '',
        username: '',
      
      },
      correctUser: true,
      correctEmail: true,
      correctPsw: false,
      correctRePsw: false,
    };
  },
  methods: {
    onSubmit(evt) {
    
      if (this.form.email.trim().length === 0) {
        this.correctEmail = false;
      }
      if (this.form.username.trim().length === 0) this.correctUser = false;

      evt.preventDefault();
      
      if (this.correctEmail && this.correctUser && this.correctPsw && this.correctRePsw) {
        const dataToStore = {
          username: this.form.username,
          email: this.form.email,
          password: this.form.password,
        };

        console.log("hello. This is the data obj to store: " + dataToStore.email,dataToStore.password, dataToStore.username)

        this.$store.commit('login')
        this.$router.push('/')

      /*this.$store.state.http.post('api/user', dataToStore).then(() => {
        this.$store.state.http.post('api/auth', { email: this.form.email, key: this.form.password })
          .then((response) => {
            const token = response.data.token.toString();
            const user = response.data.user;
            this.$store.commit('login', { token: token, user: user });
            //this.$bvModal.show('modal-ach');
          }).catch((error) => {
            if (error.response) {
              //this.$root.$emit('openModalError', 'internal_server_errorTitle', 'internal_server_error');
            } else {
              //this.$root.$emit('openModalError', 'noAnswerTitle', 'noAnswer');
            }
          });
      }).catch((err) => {
        if (err.response) {
          this.$root.$emit('openModalError', 'internal_server_errorTitle', 'internal_server_error');
        } else {
          this.$root.$emit('openModalError', 'noAnswerTitle', 'noAnswer');
        }
      });*/
      }
    },
    onBlurUser() {
      const u = this.form.username.trim();
        this.correctUser = true;
        document.getElementById('input-username').className = 'form-control';
      if (u.length < 0) {
        this.correctUser = false;
        // invalid user
        document.getElementById('input-username').className = 'form-control regUserError';
      }
    },
    onBlurEmail() {
      const e = this.form.email.trim();
      this.correctEmail = true;
      document.getElementById('input-email').className = 'form-control';
      if (e.length < 0) {
        this.correctEmail = false;
        document.getElementById('input-email').className = 'form-control regEmailError';
      }
    },
    onBlurPsw() {
      const pwd = this.form.password;
      this.correctPsw = true;
      document.getElementById('input-password').className = 'form-control';
      // check psw: length 8--20, no-space
      if (pwd.length < 8 || pwd.length > 20 || /\s/.test(pwd)) {
        // invalid psw
        this.correctPsw = false;
        document.getElementById('input-password').className = 'form-control regPswError';
      }
    },
    onBlurRePsw() {
      // check repsw
      const pwd = this.form.password;
      const repwd = this.form.repassword;
      this.correctRePsw = true;
      document.getElementById('re-input-password').className = 'form-control';
      if (pwd !== repwd) {
        // repsw no match
        this.correctRePsw = false;
        document.getElementById('re-input-password').className = 'form-control regRePswError';
      }
    },
    changeTypePsw() {
      const t = document.getElementById('input-password').type;
      if (t === 'text') {
        document.getElementById('input-password').type = 'password';
        document.getElementById('buttonHideShowPsw').style = 'background-position: 0px 0px';
      } else {
        document.getElementById('input-password').type = 'text';
        document.getElementById('buttonHideShowPsw').style = 'background-position: -44px 0px';
      }
    },
    changeTypeRePsw() {
      const t = document.getElementById('re-input-password').type;
      if (t === 'text') {
        document.getElementById('re-input-password').type = 'password';
        document.getElementById('buttonHideShowRePsw').style = 'background-position: 0px 0px';
      } else {
        document.getElementById('re-input-password').type = 'text';
        document.getElementById('buttonHideShowRePsw').style = 'background-position: -44px 0px';
      }
    },
  },
};