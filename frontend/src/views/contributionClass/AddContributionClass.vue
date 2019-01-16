<template>
  <div>
    <h1>{{$t('contributionClass.add.title')}}</h1>

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

                <!-- select field -->
                <div v-else-if="input.type === 'select'">
                  <v-select :counter="input.counter"
                            :hint="input.hint"
                            :items="input.items"
                            :label="input.label"
                            :rules="input.rules"
                            v-model="input.value"
                            :color="color"
                            :append-icon="input.appendIcon"
                            :prepend-icon="input.prependIcon"
                  ></v-select>
                </div>

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

                <v-checkbox :label="input.label"
                            v-model="input.value"
                            v-if="input.type === 'checkbox'"
                            :color="color"
                ></v-checkbox>
              </div>
            </v-flex>
          </v-layout>
        </v-card-text>
      </v-card>
      <v-btn @click="submit"
             bottom
             :color="color"
             dark
             fab
             fixed
             right
      >
        <v-icon>save</v-icon>
      </v-btn>
    </v-form>
  </div>
</template>

<script>
export default {
  name: 'AddContributionClass',
  data() {
    return {
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
    };
  },
  methods: {
    /**
     * Validates all inputs and submits the form
     */
    submit() {
      if (this.$refs.form.validate()) {
        this.$axios.post('/contributionClass', this.contributionClass)
          .then((response) => {
            this.$emit('message', this.$t('contributionClass.add.success'), 'success');
            this.$router.push({
              name: 'showContributionClass',
              params: {
                id: response.data.id,
              },
            });
          })
          .catch(() => {
            this.$emit('message', this.$t('messages.errorDefault'), 'error');
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },
  },
  computed: {
    /**
     * A computed object, which holds all entered information from the form.
     */
    contributionClass() {
      const generalInformation = this.form.cards.generalInformation.inputs;
      const contributionInformation = this.form.cards.contributionInformation.inputs;
      return {
        // general
        name: generalInformation.name.value,
        baseContribution: contributionInformation.baseContribution.value,
        additionalContribution: contributionInformation.additionalContribution.value,
      };
    },
  },
};
</script>

<style scoped>

</style>
