<template>
  <div class="setup">
    <v-dialog width="800"
              v-model="show"
              hide-overlay
              no-click-animation
              persistent
              fullscreen
    >
      <v-card>
        <!-- title bar -->
        <v-toolbar primary
                   :color="color"
                   class="text--white"
                   style="border-radius: 0"
        >
          <v-toolbar-side-icon>
            <v-icon class="white--text">fas fa-cogs</v-icon>
          </v-toolbar-side-icon>
          <v-toolbar-title class="white--text">{{$t('setup.title')}}</v-toolbar-title>
        </v-toolbar>

        <v-divider></v-divider>

        <div align="center" class="card--flex-toolbar">
          <v-flex md10 lg8>
            <v-card-text>
              <!-- stepper -->
              <v-stepper v-model="currentStep">
                <!-- stepper headers -->
                <v-stepper-header>
                  <!-- welcome stepper (header) -->
                  <v-stepper-step :complete="currentStep > 1" step="1" :color="color">
                    {{$t('setup.steps.welcome.name')}}
                  </v-stepper-step>
                  <v-divider></v-divider>

                  <!-- create user stepper (header) -->
                  <v-stepper-step :complete="currentStep > 2" step="2" :color="color">
                    {{$t('setup.steps.createUser.name')}}
                  </v-stepper-step>
                  <v-divider></v-divider>

                  <!-- finish stepper (header) -->
                  <v-stepper-step step="3" :color="color">
                    {{$t('setup.steps.finish.name')}}
                  </v-stepper-step>
                </v-stepper-header>

                <!-- welcome content -->
                <v-stepper-content step="1" :color="color">
                  <h1 class="mb-1">{{$t('setup.steps.welcome.title')}}</h1>
                  <p>
                    <span v-for="sentence in $t('setup.steps.welcome.body')"
                          :key="sentence"
                          class="font-weight-medium"
                    >
                    {{sentence}}
                  </span>
                  </p>
                  <div class="stepper-buttons">
                    <v-btn :color="color" @click="currentStep = 2" class="white--text">
                      {{$t('setup.steps.buttons.continue')}}
                    </v-btn>
                  </div>
                </v-stepper-content>

                <!-- create user content -->
                <v-stepper-content step="2">
                  <h1 class="mb-1">{{$t('setup.steps.createUser.title')}}</h1>

                  <v-form lazy-validation ref="form">
                    <v-layout row wrap>
                      <v-flex md6 class="pa-4">
                        <!-- username -->
                        <v-text-field :label="username.label"
                                      :rules="username.rules"
                                      v-model="username.value"
                                      prepend-icon="fas fa-user"
                                      counter="200"
                                      @keypress.enter="submit"
                        ></v-text-field>

                        <!-- email address -->
                        <v-text-field :label="email.label"
                                      :rules="email.rules"
                                      v-model="email.value"
                                      prepend-icon="fas fa-envelope"
                                      type="email"
                                      counter="200"
                                      @keypress.enter="submit"
                        ></v-text-field>
                      </v-flex>
                      <v-flex md6 class="pa-4">
                        <!-- password -->
                        <v-text-field :label="password.label"
                                      :rules="password.rules"
                                      v-model="password.value"
                                      prepend-icon="fas fa-key"
                                      :append-icon="visible ? 'visibility' : 'visibility_off'"
                                      :type="visible ? 'text' : 'password'"
                                      @click:append="visible = !visible"
                                      @keypress.enter="submit"
                        ></v-text-field>

                        <!-- repeat password -->
                        <v-text-field :label="passwordRepeat.label"
                                      :rules="passwordRepeat.rules"
                                      v-model="passwordRepeat.value"
                                      prepend-icon="fas fa-key"
                                      :append-icon="visible ? 'visibility' : 'visibility_off'"
                                      :type="visible ? 'text' : 'password'"
                                      @click:append="visible = !visible"
                                      @keypress.enter="submit"
                        ></v-text-field>
                      </v-flex>
                    </v-layout>
                  </v-form>

                  <div class="stepper-buttons">
                    <v-btn :color="color" @click="currentStep = 1" class="white--text">
                      {{$t('setup.steps.buttons.back')}}
                    </v-btn>
                    <v-btn :color="color" @click="submit" class="white--text">
                      {{$t('setup.steps.buttons.continue')}}
                    </v-btn>
                  </div>
                </v-stepper-content>

                <!-- finish content -->
                <v-stepper-content step="3">
                  <h1 class="mb-1">{{$t('setup.steps.finish.title')}}</h1>
                  <p>{{$t('setup.steps.finish.body')}}</p>
                  <div class="stepper-buttons">
                    <v-btn :color="color" dark @click="$router.push({name: 'dashboard'})">
                      {{$t('setup.steps.finish.name')}}
                    </v-btn>
                  </div>
                </v-stepper-content>
              </v-stepper>
            </v-card-text>
          </v-flex>
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'InitialSetup',
  data() {
    return {
      show: true,
      color: 'deep-purple darken-1',
      currentStep: 1,
      username: {
        label: this.$t('setup.steps.createUser.username.label'),
        rules: [
          v => !!v || this.$t('setup.steps.createUser.username.required'),
          v => (v && v.length > 1 && v.length <= 200)
            || this.$t('setup.steps.createUser.username.length'),
        ],
        value: '',
      },
      email: {
        label: this.$t('setup.steps.createUser.email.label'),
        rules: [
          v => !!v || this.$t('setup.steps.createUser.email.required'),
          v => (v.length >= 4 && v.length <= 200)
            || this.$t('setup.steps.createUser.email.length'),
          v => this.emailRegEx.test(v)
            || this.$t('setup.steps.createUser.email.valid'),
        ],
        value: '',
      },
      password: {
        label: this.$t('setup.steps.createUser.password.label'),
        rules: [
          v => !!v || this.$t('setup.steps.createUser.password.required'),
          v => (v && v.length >= 4 && v.length <= 200)
            || this.$t('setup.steps.createUser.password.length'),
        ],
        value: '',
      },
      passwordRepeat: {
        label: this.$t('setup.steps.createUser.password.labelRepeat'),
        rules: [
          v => !!v || this.$t('setup.steps.createUser.password.requiredRepeat'),
          v => (v && v.length >= 4 && v.length <= 200)
            || this.$t('setup.steps.createUser.password.lengthRepeat'),
          v => (v && v === this.password.value)
            || this.$t('setup.steps.createUser.password.match'),
        ],
        value: '',
      },
      visible: false,
      emailRegEx: /^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\.[A-Za-z0-9_.]+$/,
    };
  },
  create() {
    this.$axios.head('/user/exists')
      .then((response) => {
        if (response.status !== 204) {
          this.$router.push({
            name: 'login',
          });
        }
      });
  },
  methods: {
    /**
     * Validates the form and performs a post request to the backend to store the first user.
     */
    submit() {
      if (this.$refs.form.validate()) {
        // add the first user to the database
        this.$axios.post('/user/exists', {
          username: this.username.value,
          password: this.password.value,
          email: this.email.value,
        })
          .then(() => {
            this.$emit('message', this.$t('setup.steps.createUser.success'), 'success');
            this.currentStep += 1;
          })
          .catch(() => {
            this.$emit('message', this.$t('messages.errorDefault'), 'error');
          });
      }
    },
  },
};
</script>

<style scoped>
  .setup {
    position: fixed;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    background: white;
    z-index: 10;
  }

  .stepper-buttons {
    text-align: right;
  }
</style>
