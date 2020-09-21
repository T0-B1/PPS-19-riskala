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
      if (this.form.email.trim().length === 0) this.correctEmail = false;
      
      if (this.form.username.trim().length === 0) this.correctUser = false;

      evt.preventDefault();
      
      if (this.correctEmail && this.correctUser && this.correctPsw && this.correctRePsw) {
        const dataToStore = {
          username: this.form.username,
          password: this.form.password,
          email: this.form.email,
        };

        this.$store.state.http.post('register', dataToStore)
        .then((response) => {
          const token = response.data;
          const user = dataToStore.username;
          this.$store.commit('login', { token: token, user: user });
          this.$router.push('/')
        }).catch((error) => {
          this.$store.commit('logout');
          if (error.response) {
            if (error.response.status === 404) {
              console.log("User already exists");
            } else {
              console.log("Internal server error!");
            }
          }
        });
      }
    },
    onBlurUser() {
      const u = this.form.username.trim();
        this.correctUser = true;
        document.getElementById('input-username').className = 'form-control';
      if (u.length < 0) {
        this.correctUser = false;
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
      if (pwd.length < 8 || pwd.length > 20 || /\s/.test(pwd)) {
        this.correctPsw = false;
        document.getElementById('input-password').className = 'form-control regPswError';
      }
    },
    onBlurRePsw() {
      const pwd = this.form.password;
      const repwd = this.form.repassword;
      this.correctRePsw = true;
      document.getElementById('re-input-password').className = 'form-control';
      if (pwd !== repwd) {
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
