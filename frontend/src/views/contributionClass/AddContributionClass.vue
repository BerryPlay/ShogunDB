<template>
  <div>
    <h1>{{$t('contributionClass.addNewContributionClass')}}</h1>

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
            title: this.$t('contributionClass.generalInformation'),
            inputs: {
              name: {
                label: this.$t('name'),
                type: 'text',
                value: '',
                counter: 200,
                hint: `*${this.$t('required')}`,
                rules: [
                  v => !!v || this.$t('nameRequired'),
                  v => (v.length >= 1 && v.length <= 200)
                    || 'Name must have less than 200 character',
                ],
              },
            },
          },
          contributionInformation: {
            title: this.$t('contributionClass.contribution'),
            inputs: {
              baseContribution: {
                label: this.$t('contributionClass.baseContribution'),
                type: 'text',
                value: '',
                hint: `*${this.$t('required')}`,
                prependIcon: 'euro_symbol',
                rules: [
                  v => !!v || this.$t('contributionClass.baseContributionRequired'),
                  v => this.numberPattern.test(v)
                    || this.$t('contributionClass.wrongContributionFormat'),
                ],
              },
              additionalContribution: {
                label: this.$t('contributionClass.additionalContribution'),
                type: 'text',
                value: '',
                hint: `*${this.$t('required')}`,
                prependIcon: 'euro_symbol',
                rules: [
                  v => !!v || this.$t('contributionClass.additionalContributionRequired'),
                  v => this.numberPattern.test(v)
                    || this.$t('contributionClass.wrongContributionFormat'),
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
            this.$emit(
              'message',
              this.$t('contributionClass.addNewContributionClassSuccessMessage'),
              'success',
            );
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
