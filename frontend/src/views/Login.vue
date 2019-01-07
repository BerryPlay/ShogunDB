<template>
  <div>
    <v-dialog max-width="600px"
              persistent
              v-model="showModal"
    >
      <v-card>
        <v-card-title>
          <span class="headline">Login</span>
        </v-card-title>
        <v-card-text>
          <v-form lazy-validation
                  ref="form"
                  v-model="valid"
          >
            <!-- username -->
            <v-text-field :loading="loading"
                          :rules="usernameRules"
                          @keydown.enter="login"
                          label="Username"
                          prepend-icon="person"
                          required
                          v-model="username"
            ></v-text-field>

            <!-- password -->
            <v-text-field :append-icon="showPassword ? 'visibility_off' : 'visibility'"
                          :loading="loading"
                          :rules="passwordRules"
                          :type="showPassword ? 'text' : 'password'"
                          @click:append="showPassword = !showPassword"
                          @keydown.enter="login"
                          label="Password"
                          prepend-icon="vpn_key"
                          required
                          v-model="password"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn @click="login" color="blue darken-1" flat>Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- the snackbar to indecate an authentication error -->
    <v-snackbar
      bottom
      color="error"
      v-model="showAuthenticationError"
    >
      {{ errorText }}
      <v-btn
        @click="showAuthenticationError = false"
        flat
        timeout="4000"
      >
        Close
      </v-btn>
    </v-snackbar>
  </div>
</template>

<script>
  export default {
    name: 'Login',
    data() {
      return {
        valid: true,
        showModal: true,
        loading: false,
        username: '',
        usernameRules: [
          v => !!v || 'Name is required',
          v => (v && v.length > 3 && v.length < 200)
            || 'Name can only have more than 3 and less than 200 characters',
        ],
        password: '',
        showPassword: false,
        passwordRules: [
          v => !!v || 'Password is required',
        ],
        showAuthenticationError: false,
        errorText: 'Login failed',
      };
    },
    created() {
      // set the authenticated flag to false
      localStorage.authenticated = false;
    },
    methods: {
      /**
       * Validates the form inputs and sends a request with the login credentials to obtain a token.
       */
      login() {
        this.loading = true;

        if (this.$refs.form.validate()) {
          this.$axios.post('/token', {
            username: this.username,
            password: this.password,
          })
            .then((response) => {
              // store the token to the local storage
              localStorage.authenticationToken = response.data;
              localStorage.authenticated = true;

              // apply the token as the default authentication header for axios
              this.$axios.defaults.headers.common.Authorization = `Bearer ${response.data}`;

              // emit the login event to display the logout button
              this.$emit('login');

              // exit the login view
              this.$router.push('/');
            })
            .catch(() => {
              // opens the error snackbar
              this.showAuthenticationError = true;
              localStorage.authenticated = false;
            })
            .finally(() => {
              this.loading = false;
            });
        }
      },
    },
  };
</script>

<style scoped>

</style>
