<template>
  <div>
    <!-- success alert -->
    <v-alert :value="showSuccess"
             type="success"
             dismissible
    >
      The contribution class was saved successfully.
    </v-alert>

    <!-- error alert -->
    <v-alert :value="showError"
             type="error"
    >
      An error occurs.
    </v-alert>

    <!-- remove dialog -->
    <v-dialog v-model="showDeleteDialog" persistent max-width="400">
      <v-card>
        <v-card-title>
          <h2>Delete contribution class?</h2>
        </v-card-title>

        <v-card-text>
          <b>Warning:</b> the contribution class will be removed from the database! Are you sure?
          <div align="right">
            <v-btn @click="showDeleteDialog = false">
              no
            </v-btn>
            <v-btn color="error"
                   dark
                   @click="remove"
            >
              yes
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- content -->
    <div v-if="!showError">
      <h1>Contribution class {{name}}</h1>

      <v-form lazy-validation
              ref="form"
              v-model="form.valid"
      >
        <v-card>
          <v-card-text>
            <v-layout row wrap>
              <v-flex :key="i"
                      class="px-2 py-2"
                      md6
                      sm12
                      v-for="(card, i) in form.cards"
                      xl6
                      xs12
              >
                <h3 v-if="card.title !== null">{{card.title}}</h3>
                <div :key="`${card.title}_${fieldIndex}`"
                     v-for="(input, fieldIndex) in card.inputs"
                >
                  <!-- text input -->
                  <v-text-field :counter="input.counter"
                                :hint="input.hint"
                                :label="input.label"
                                :rules="input.rules"
                                v-if="input.type === 'text'"
                                v-model="input.value"
                                :color="color"
                                :append-icon="input.appendIcon"
                                :prepend-icon="input.prependIcon"
                  ></v-text-field>

                  <!-- number input -->
                  <v-text-field :counter="input.counter"
                                :hint="input.hint"
                                :label="input.label"
                                :rules="input.rules"
                                v-if="input.type === 'number'"
                                type="number"
                                v-model="input.value"
                                :color="color"
                                :append-icon="input.appendIcon"
                                :prepend-icon="input.prependIcon"
                  ></v-text-field>
                </div>
              </v-flex>
            </v-layout>
          </v-card-text>
        </v-card>

        <v-speed-dial v-model="openSpeedDial"
                      bottom
                      right
                      fixed
        >
          <v-btn slot="activator"
                 v-model="fab"
                 :color="color"
                 dark
                 fab
          >
            <v-icon>more_vert</v-icon>
            <v-icon>close</v-icon>
          </v-btn>
          <v-btn fab
                 dark
                 small
                 color="blue"
                 @click="submit"
          >
            <v-icon>save</v-icon>
          </v-btn>
          <v-btn fab
                 dark
                 small
                 color="red"
                 @click="showDeleteDialog = true"
          >
            <v-icon>delete</v-icon>
          </v-btn>
        </v-speed-dial>
      </v-form>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ShowContributionClass",
    data() {
      return {
        id: null,
        name: null,
        form: {
          valid: true,
          cards: {
            generalInformation: {
              title: 'General information',
              inputs: {
                name: {
                  label: 'Name',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'Forename is required',
                    v => (v.length >= 1 && v.length <= 200)
                      || 'Name must have less than 200 character',
                  ],
                },
              },
            },
            contributionInformation: {
              title: 'Contribution',
              inputs: {
                baseContribution: {
                  label: 'Base contribution',
                  type: 'text',
                  value: '',
                  hint: '*required',
                  prependIcon: 'euro_symbol',
                  rules: [
                    v => !!v || 'Base contribution is required',
                    v => this.numberPattern.test(v) || 'Invalid format! Must be X.XX',
                  ],
                },
                additionalContribution: {
                  label: 'Additional contribution',
                  type: 'text',
                  value: '',
                  hint: '*required',
                  prependIcon: 'euro_symbol',
                  rules: [
                    v => !!v || 'Additional contribution is required',
                    v => this.numberPattern.test(v) || 'Invalid format! Must be X.XX',
                  ],
                },
              },
            },
          },
        },
        numberPattern: /^[0-9]+\.[0-9][0-9]$/,
        color: 'green',
        showSuccess: false,
        showError: false,
        openSpeedDial: false,
        showDeleteDialog: false,
        fab: false,
      };
    },
    created() {
      // fetch the contribution classes
      this.$axios.get(`/contributionClass/${this.$route.params.id}`)
        .then((response) => this.setContributionClass(response.data))
        .catch(() => this.showError = true);
    },
    methods: {
      /**
       * Validates all inputs and submits the form
       */
      submit() {
        this.showSuccess = false;
        if (this.$refs.form.validate()) {
          this.$axios.put('/contributionClass', {
            id: this.id,
            name: this.form.cards.generalInformation.inputs.name.value,
            baseContribution: this.form.cards.contributionInformation.inputs.baseContribution.value,
            additionalContribution: this.form.cards
              .contributionInformation.inputs.additionalContribution.value,
          })
            .then((response) => {
              this.showSuccess = true;
              this.setContributionClass(response.data)
            })
            .catch(() => this.showError = true);
        }
      },
      /**
       * Removes the contribution class from the database.
       */
      remove() {
        this.showDeleteDialog = false;

        this.$axios.delete(`/contributionClass/${this.id}`)
          .then(() => this.$router.push({name: 'indexContributionClass'}))
          .catch(() => this.showError = true);
      },
      /**
       * Applies the given contribution class to this view.
       *
       * @param contributionClass the contribution class to apply
       */
      setContributionClass(contributionClass) {
        // id
        this.id = contributionClass.id;

        // name
        this.form.cards.generalInformation.inputs.name.value = contributionClass.name;
        this.name = contributionClass.name;

        // base contribution
        this.form.cards.contributionInformation.inputs.baseContribution.value =
          (contributionClass.baseContribution).toFixed(2);

        // additional contribution
        this.form.cards.contributionInformation.inputs.additionalContribution.value =
          (contributionClass.additionalContribution).toFixed(2);
      }
    }
  }
</script>

<style scoped>

</style>
