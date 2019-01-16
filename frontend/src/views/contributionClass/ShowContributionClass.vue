<template>
  <div>
    <!-- remove dialog -->
    <v-dialog v-model="showDeleteDialog" persistent max-width="400">
      <v-card>
        <v-card-title>
          <h2>{{$t('contributionClass.delete.title')}}</h2>
        </v-card-title>

        <v-card-text>
          <b>{{$t('contributionClass.delete.warning')}}</b>
          {{$t('contributionClass.delete.body')}}
          <div align="right">
            <v-btn @click="showDeleteDialog = false">
              {{$t('phrases.no')}}
            </v-btn>
            <v-btn color="error"
                   dark
                   @click="remove"
            >
              {{$t('phrases.yes')}}
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- content -->
    <h1>{{$t('contributionClass.singular')}}: {{name}}</h1>

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
</template>

<script>
export default {
  name: 'ShowContributionClass',
  data() {
    return {
      id: null,
      name: null,
      form: {
        valid: true,
        cards: {
          generalInformation: {
            title: this.$t('contributionClass.categories.general'),
            inputs: {
              name: {
                label: this.$t('contributionClass.name.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('contributionClass.name.hint'),
                rules: [
                  v => !!v || this.$t('contributionClass.name.required'),
                  v => (v.length >= 1 && v.length <= 200)
                    || this.$t('contributionClass.name.length'),
                ],
              },
            },
          },
          contributionInformation: {
            title: this.$t('contributionClass.categories.contribution'),
            inputs: {
              baseContribution: {
                label: this.$t('contributionClass.base.label'),
                type: 'text',
                value: '',
                hint: this.$t('contributionClass.base.hint'),
                prependIcon: 'fas fa-euro-sign',
                rules: [
                  v => !!v || this.$t('contributionClass.base.required'),
                  v => this.numberPattern.test(v)
                    || this.$t('contributionClass.base.format'),
                ],
              },
              additionalContribution: {
                label: this.$t('contributionClass.additional.label'),
                type: 'text',
                value: '',
                hint: this.$t('contributionClass.additional.hint'),
                prependIcon: 'euro_symbol',
                rules: [
                  v => !!v || this.$t('contributionClass.additional.required'),
                  v => this.numberPattern.test(v)
                    || this.$t('contributionClass.additional.format'),
                ],
              },
            },
          },
        },
      },
      numberPattern: /^[0-9]+\.[0-9][0-9]$/,
      color: 'green',
      openSpeedDial: false,
      showDeleteDialog: false,
      fab: false,
    };
  },
  created() {
    // fetch the contribution classes
    this.$axios.get(`/contributionClass/${this.$route.params.id}`)
      .then((response) => {
        this.setContributionClass(response.data);
      })
      .catch(() => {
        this.$emit('message', 'Something went wrong.', 'error');
      });
  },
  methods: {
    /**
     * Validates all inputs and submits the form.
     */
    submit() {
      if (this.$refs.form.validate()) {
        this.$axios.put('/contributionClass', {
          id: this.id,
          name: this.form.cards.generalInformation.inputs.name.value,
          baseContribution: this.form.cards.contributionInformation.inputs.baseContribution.value,
          additionalContribution: this.form.cards
            .contributionInformation.inputs.additionalContribution.value,
        })
          .then((response) => {
            this.$emit('message', this.$t('contributionClass.save.success'), 'success');
            this.setContributionClass(response.data);
          })
          .catch(() => {
            this.$emit('message', this.$t('messages.errorDefault'), 'error');
          });
      }
    },
    /**
     * Removes the contribution class from the database.
     */
    remove() {
      this.showDeleteDialog = false;

      this.$axios.delete(`/contributionClass/${this.id}`)
        .then(() => this.$router.push({
          name: 'indexContributionClass',
        }))
        .catch(() => {
          this.$emit('message', this.$('messages.errorDefault'), 'error');
        });
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
      this.form.cards.contributionInformation
        .inputs.baseContribution.value = (contributionClass.baseContribution).toFixed(2);

      // additional contribution
      this.form.cards.contributionInformation.inputs
        .additionalContribution.value = (contributionClass.additionalContribution).toFixed(2);
    },
  },
};
</script>

<style scoped>

</style>
