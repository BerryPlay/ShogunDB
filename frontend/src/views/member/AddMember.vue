<template>
  <div>
    <h1>{{$t('member.add.title')}}</h1>

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
              <h3>{{card.title}}</h3>
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
                ></v-text-field>

                <!-- date input -->
                <div v-else-if="input.type === 'date'">
                  <v-menu
                    :close-on-content-click="false"
                    :nudge-right="40"
                    full-width
                    lazy
                    max-width="290px"
                    min-width="290px"
                    offset-y
                    transition="scale-transition"
                    :color="color"
                  >
                    <v-text-field :counter="input.counter"
                                  :hint="input.hint"
                                  :label="input.label"
                                  :rules="input.rules"
                                  :value="formatDate(input.value)"
                                  prepend-icon="event"
                                  readonly
                                  slot="activator"
                                  :color="color"
                    ></v-text-field>
                    <v-date-picker :max="input.maxDate"
                                   locale="de-DE"
                                   no-title
                                   scrollable
                                   v-model="input.value"
                                   :color="color"
                    >
                    </v-date-picker>
                  </v-menu>
                </div>

                <!-- select field -->
                <div v-else-if="input.type === 'select'">
                  <v-select :counter="input.counter"
                            :hint="input.hint"
                            :items="input.items"
                            :label="input.label"
                            :rules="input.rules"
                            :multiple="input.multi"
                            v-model="input.value"
                            :color="color"
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
import formatDate from '../../helper';

export default {
  name: 'AddMember',
  data() {
    return {
      form: {
        valid: true,
        cards: {
          generalInformation: {
            title: this.$t('member.add.generalInformation.title'),
            inputs: {
              forename: {
                label: this.$t('member.add.generalInformation.forename.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.generalInformation.forename.hint'),
                rules: [
                  v => !!v || this.$t('member.add.generalInformation.forename.required'),
                  v => (v.length >= 1 && v.length <= 200)
                    || this.$t('member.add.generalInformation.forename.length'),
                ],
              },
              surname: {
                label: this.$t('member.add.generalInformation.surname.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.generalInformation.surname.hint'),
                rules: [
                  v => !!v || this.$t('member.add.generalInformation.surname.required'),
                  v => (v.length >= 1 && v.length < 200)
                    || this.$t('member.add.generalInformation.surname.length'),
                ],
              },
              gender: {
                label: this.$t('member.add.generalInformation.gender.label'),
                type: 'select',
                value: '',
                items: [
                  {
                    value: 'MALE',
                    text: this.$t('member.add.generalInformation.gender.male'),
                  },
                  {
                    value: 'FEMALE',
                    text: this.$t('member.add.generalInformation.gender.female'),
                  },
                  {
                    value: 'DIVERSE',
                    text: this.$t('member.add.generalInformation.gender.diverse'),
                  },
                ],
                rules: [
                  v => !!v || this.$t('member.add.generalInformation.gender.required'),
                ],
              },
              dateOfBirth: {
                label: this.$t('member.add.generalInformation.dateOfBirth.label'),
                type: 'date',
                value: '',
                maxDate: new Date().toISOString()
                  .substr(0, 10),
                hint: this.$t('member.add.generalInformation.dateOfBirth.hint'),
                rules: [
                  v => !!v || this.$t('member.add.generalInformation.dateOfBirth.required'),
                  v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
                    || this.$t('member.add.generalInformation.dateOfBirth.format'),
                ],
              },
            },
          },
          contactInformation: {
            title: this.$t('member.add.contactInformation.title'),
            inputs: {
              street: {
                label: this.$t('member.add.contactInformation.street.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.contactInformation.street.hint'),
                rules: [
                  v => !!v || this.$t('member.add.contactInformation.street.required'),
                  v => (v.length >= 1 && v.length <= 200)
                    || this.$t('member.add.contactInformation.street.length'),
                ],
              },
              postcode: {
                label: this.$t('member.add.contactInformation.postcode.label'),
                type: 'number',
                value: '',
                hint: this.$t('member.add.contactInformation.postcode.hint'),
                rules: [
                  v => !!v || this.$t('member.add.contactInformation.postcode.required'),
                  v => (v.length >= 3 && v.length <= 8)
                    || this.$t('member.add.contactInformation.postcode.length'),
                ],
              },
              city: {
                label: this.$t('member.add.contactInformation.city.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.contactInformation.city.hint'),
                rules: [
                  v => !!v || this.$t('member.add.contactInformation.city.required'),
                  v => (v.length >= 1 && v.length <= 200)
                    || this.$t('member.add.contactInformation.city.length'),
                ],
              },
              phoneNumber: {
                label: this.$t('member.add.contactInformation.phoneNumber.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.contactInformation.phoneNumber.hint'),
                rules: [
                  v => !!v || this.$t('member.add.contactInformation.phoneNumber.required'),
                  v => (v.length >= 4 && v.length <= 200)
                    || this.$t('member.add.contactInformation.phoneNumber.length'),
                ],
              },
              mobileNumber: {
                label: this.$t('member.add.contactInformation.mobileNumber.label'),
                type: 'text',
                value: '',
                counter: 200,
                rules: [
                  v => (v.length === 0 || (v.length >= 4 && v.length <= 200))
                    || this.$t('member.add.contactInformation.mobileNumber.length'),
                ],
              },
              email: {
                label: this.$t('member.add.contactInformation.email.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.contactInformation.email.hint'),
                rules: [
                  v => !!v || this.$t('member.add.contactInformation.email.required'),
                  v => (v.length >= 4 && v.length <= 200)
                    || this.$t('member.add.contactInformation.email.length'),
                  v => this.emailRegEx.test(v)
                    || this.$t('member.add.contactInformation.email.valid'),
                ],
              },
            },
          },
          membershipInformation: {
            title: this.$t('member.add.membershipInformation.title'),
            inputs: {
              enteredDate: {
                label: this.$t('member.add.membershipInformation.enteredDate.label'),
                type: 'date',
                value: '',
                hint: this.$t('member.add.membershipInformation.enteredDate.hint'),
                rules: [
                  v => !!v || this.$t('member.add.membershipInformation.enteredDate.required'),
                  v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
                    || this.$t('member.add.membershipInformation.enteredDate.format'),
                ],
              },
              hasBudoPass: {
                label: this.$t('member.add.membershipInformation.hasBudoPass.label'),
                type: 'checkbox',
                value: '',
              },
              budoPassDate: {
                label: this.$t('member.add.membershipInformation.budoPassDate.label'),
                type: 'date',
                value: '',
                rules: [
                  v => (v.length === 0)
                    || ((v.substring(2, 3) === '.' && v.substring(5, 6) === '.'))
                    || this.$t('member.add.membershipInformation.budoPassDate.format'),
                ],
              },
            },
          },
          moreInformation: {
            title: this.$t('member.add.moreInformation.title'),
            inputs: {
              disciplines: {
                label: this.$t('member.add.moreInformation.disciplines.label'),
                type: 'select',
                multi: true,
                value: '',
                items: [],
                rules: [
                  v => !!v || this.$t('member.add.moreInformation.disciplines.required'),
                ],
              },
              contributionClass: {
                label: this.$t('member.add.moreInformation.contributionClass.label'),
                type: 'select',
                value: '',
                items: [],
                rules: [
                  v => !!v || this.$t('member.add.moreInformation.contributionClass.required'),
                ],
              },
              accountHolder: {
                label: this.$t('member.add.moreInformation.accountHolder.label'),
                type: 'text',
                value: '',
                counter: 200,
                hint: this.$t('member.add.moreInformation.accountHolder.hint'),
                rules: [
                  v => !!v || this.$t('member.add.moreInformation.accountHolder.required'),
                  v => (v.length >= 1 && v.length < 200)
                    || this.$t('member.add.moreInformation.accountHolder.length'),
                ],
              },
            },
          },
        },
      },
      emailRegEx: /^[A-Za-z0-9_.]+@[A-Za-z0-9_.]+\.[A-Za-z0-9_.]+$/,
      color: 'pink',
    };
  },
  created() {
    // fetch the contribution classes
    this.$axios.get('/contributionClass')
      .then((response) => {
        response.data.forEach((contributionClass) => {
          this.form.cards.moreInformation.inputs.contributionClass.items.push({
            text: contributionClass.name,
            value: contributionClass.id,
          });
        });
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault'), 'error'));

    // fetch the disciplines
    this.$axios.get('/discipline')
      .then((response) => {
        response.data.forEach((discipline) => {
          this.form.cards.moreInformation.inputs.disciplines.items.push({
            text: discipline.name,
            value: discipline.id,
          });
        });
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault'), 'error'));
  },
  methods: {
    formatDate,
    /**
     * Validates all inputs and submits the form
     */
    submit() {
      if (this.$refs.form.validate()) {
        this.$axios.post('/member', this.member)
          .then(() => {
            this.$emit('message', this.$t('member.add.success'), 'success');
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
    member() {
      const generalInformation = this.form.cards.generalInformation.inputs;
      const contactInformation = this.form.cards.contactInformation.inputs;
      const membershipInformation = this.form.cards.membershipInformation.inputs;
      const moreInformation = this.form.cards.moreInformation.inputs;
      return {
        // general
        forename: generalInformation.forename.value,
        surname: generalInformation.surname.value,
        gender: generalInformation.gender.value,
        dateOfBirth: generalInformation.dateOfBirth.value,

        // contact
        street: contactInformation.street.value,
        postcode: contactInformation.postcode.value,
        city: contactInformation.city.value,
        phoneNumber: contactInformation.phoneNumber.value,
        mobileNumber: contactInformation.mobileNumber.value,
        email: contactInformation.email.value,

        // membershipInformation
        enteredDate: membershipInformation.enteredDate.value,
        hasBudoPass: membershipInformation.hasBudoPass.value,
        budoPassDate: membershipInformation.budoPassDate.value,

        // moreInformation
        disciplines: moreInformation.disciplines.value,
        contributionClass: moreInformation.contributionClass.value,
        accountHolder: moreInformation.accountHolder.value,

        // other data
        hasLeft: false,
        isPassive: false,
      };
    },
  },
};
</script>

<style scoped>

</style>
