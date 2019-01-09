<template>
  <div>
    <h1>Add new member</h1>

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
  export default {
    name: 'AddMember',
    data() {
      return {
        form: {
          valid: true,
          cards: {
            generalInformation: {
              title: 'General Information',
              inputs: {
                forename: {
                  label: 'Forename',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'Forename is required',
                    v => (v.length >= 1 && v.length <= 200)
                      || 'Forename must have less than 200 character',
                  ],
                },
                surname: {
                  label: 'Surname',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'Surname is required',
                    v => (v.length >= 1 && v.length < 200)
                      || 'Surname must have less than 200 character',
                  ],
                },
                gender: {
                  label: 'Gender',
                  type: 'select',
                  value: '',
                  items: [
                    {
                      value:'MALE',
                      text: 'male',
                    },
                    {
                      value:'FEMALE',
                      text: 'female',
                    },
                  ],
                  rules: [
                    v => !!v || 'Gender is required',
                  ],
                },
                dateOfBirth: {
                  label: 'Date of Birth',
                  type: 'date',
                  value: '',
                  counter: 200,
                  maxDate: new Date().toISOString()
                    .substr(0, 10),
                  hint: '*required',
                  rules: [
                    v => !!v || 'Date of birth is required',
                    v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
                      || 'Incorrect date format',
                  ],
                },
              },
            },
            contactInformation: {
              title: 'Contact',
              inputs: {
                street: {
                  label: 'Street',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'Street is required',
                    v => (v.length >= 1 && v.length <= 200)
                      || 'Street must have less than 200 character',
                  ],
                },
                postcode: {
                  label: 'Postcode',
                  type: 'number',
                  value: '',
                  hint: '*required',
                  rules: [
                    v => !!v || 'Postcode is required',
                    v => (v.length >= 3 && v.length <= 8)
                      || 'Postcode must have between 3 and 8 digits',
                  ],
                },
                city: {
                  label: 'City',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'City is required',
                    v => (v.length >= 1 && v.length <= 200)
                      || 'City must have less than 200 character',
                  ],
                },
                phoneNumber: {
                  label: 'Phone number',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || ' is required',
                    v => (v.length >= 4 && v.length <= 200) || 'Phone number must have between 3 ' +
                      'and 8 digits',
                  ],
                },
                mobileNumber: {
                  label: 'Mobile number',
                  type: 'text',
                  value: '',
                  counter: 200,
                  rules: [
                    v => (v.length === 0 || (v.length >= 4 && v.length <= 200))
                      || 'Mobile number must have between 3 and 8 digits',
                  ],
                },
                email: {
                  label: 'E-Mail address',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'E-Mail address is required',
                    v => (v.length >= 4 && v.length <= 200) || 'E-Mail address must have between ' +
                      '3 and 8 digits',
                    v => this.emailRegEx.test(v) || 'Must be a valid e-mail address',
                  ],
                },
              }
            },
            membershipInformation: {
              title: 'Membership',
              inputs: {
                enteredDate: {
                  label: 'Entered date',
                  type: 'date',
                  value: '',
                  hint: '*required',
                  rules: [
                    v => !!v || 'Entered date is required',
                    v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
                      || 'Incorrect date format',
                  ],
                },
                hasBudoPass: {
                  label: 'Budo pass',
                  type: 'checkbox',
                  value: '',
                },
                budoPassDate: {
                  label: 'Budo pass date',
                  type: 'date',
                  value: '',
                  hint: '*required',
                  rules: [
                    v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
                      || 'Incorrect date format',
                  ],
                },
              },
            },
            moreInformation: {
              title: 'More',
              inputs: {
                disciplines: {
                  label: 'Disciplines',
                  type: 'select',
                  multi: true,
                  value: '',
                  items: [],
                  rules: [
                    v => !!v || 'Contribution class is required',
                  ],
                },
                contributionClass: {
                  label: 'Contribution class',
                  type: 'select',
                  value: '',
                  items: [],
                  rules: [
                    v => !!v || 'Contribution class is required',
                  ],
                },
                accountHolder: {
                  label: 'Account holder',
                  type: 'text',
                  value: '',
                  counter: 200,
                  hint: '*required',
                  rules: [
                    v => !!v || 'Account holder is required',
                    v => (v.length >= 1 && v.length < 200)
                      || 'Account holder must have less than 200 character',
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
              value: contributionClass.id
            });
          });
        })
        .catch(() => alert('something went wrong!'));

      // fetch the disciplines
      this.$axios.get('/discipline')
        .then((response) => {
          response.data.forEach((discipline) => {
            this.form.cards.moreInformation.inputs.disciplines.items.push({
              text: discipline.name,
              value: discipline.id
            });
          });
        })
        .catch(() => alert('something went wrong!'));
    },
    methods: {
      /**
       * Validates all inputs and submits the form
       */
      submit() {
        if (this.$refs.form.validate()) {
          this.$axios.post('/member', this.member)
            .then((response) => {
              alert('success');
            })
            .catch(() => {
              alert('failed');
            })
            .finally(() => {
              this.loading = false;
            });
        }
      },
      /**
       * Converts a `YYYY-MM-DD` date format to `DD.MM.YYYY`.
       *
       * @param {string} date the date to convert
       * @returns {string} the converted date
       */
      formatDate(date) {
        if (date.length === 10) {
          const year = date.substring(0, 4);
          const month = date.substring(5, 7);
          const day = date.substring(8, 10);

          return `${day}.${month}.${year}`;
        }
        return '';
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
