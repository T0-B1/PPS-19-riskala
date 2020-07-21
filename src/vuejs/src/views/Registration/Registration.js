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
    };
  },
  methods: {
    onSubmit() {
      this.$router.push('/')
    },
    onBlurUser() {
    },
    onBlurEmail() {
    },
    onBlurPsw() {
    },
    onBlurRePsw() {
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
