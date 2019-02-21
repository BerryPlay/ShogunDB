<template>
  <div>
    <h1>{{$t('event.add.title')}}</h1>
    <v-form lazy-validation
            ref="form"
    >
      <v-layout row wrap>
        <!-- information -->
        <v-flex lg4 xs12 class="pa-1">
          <v-card hover>
            <v-card-title>
              <h2>{{$t('event.add.information')}}</h2>
            </v-card-title>
            <v-card-text>
              <!-- name of the event -->
              <v-text-field counter="200"
                            :hint="event.name.hint"
                            :label="event.name.label"
                            :rules="event.name.rules"
                            v-model="event.name.value"
                            :color="color"
              ></v-text-field>

              <!-- date of the event -->
              <v-menu :close-on-content-click="false"
                      :nudge-right="40"
                      full-width
                      lazy
                      max-width="290px"
                      min-width="290px"
                      offset-y
                      transition="scale-transition"
                      :color="color"
              >
                <v-text-field :label="event.date.label"
                              :rules="event.date.rules"
                              :value="formatDate(event.date.value)"
                              prepend-icon="event"
                              readonly
                              slot="activator"
                              :color="color"
                ></v-text-field>
                <v-date-picker locale="de-DE"
                               no-title
                               scrollable
                               v-model="event.date.value"
                               :color="color"
                >
                </v-date-picker>
              </v-menu>
            </v-card-text>
          </v-card>
        </v-flex>

        <!-- member -->
        <v-flex lg8 xs12 class="pa-1">
          <v-card hover>
            <v-card-title>
              <h2>{{$t('event.add.member.title')}}</h2>
            </v-card-title>
            <v-card-text>
              <v-data-table :headers="event.member.headers"
                            :items="event.member.items"
                            hide-actions
              >
                <template slot="items" slot-scope="props">
                  <td>{{ props.item.forename }}</td>
                  <td>{{ props.item.surname }}</td>
                  <td>{{ props.item.age }}</td>
                  <td>
                    <v-icon small
                            @click="removeMember(props.item.id)"
                            class="text-xs-right"
                    >
                      fas fa-trash-alt
                    </v-icon>
                  </td>
                </template>
              </v-data-table>
              <v-btn
                color="green"
                dark
                small
                absolute
                bottom
                left
                fab
              >
                <v-icon>add</v-icon>
              </v-btn>
              <!-- todo: add a member picker -->
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
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
  name: 'AddEvent',
  data() {
    return {
      color: 'purple darken-3',
      event: {
        name: {
          label: this.$t('event.add.name.label'),
          hint: this.$t('event.add.name.hint'),
          value: '',
          rules: [
            v => !!v || this.$t('event.add.name.required'),
            v => (v.length >= 1 && v.length <= 200)
              || this.$t('event.add.name.length'),
          ],
        },
        date: {
          label: this.$t('event.add.date.label'),
          value: '',
          rules: [
            v => !!v || this.$t('event.add.date.required'),
            v => (v.substring(2, 3) === '.' && v.substring(5, 6) === '.')
              || this.$t('event.add.date.format'),
          ],
        },
        member: {
          headers: [
            {
              text: this.$t('event.add.member.forename'),
              value: 'forename',
              align: 'left',
              sortable: true,
            },
            {
              text: this.$t('event.add.member.surname'),
              value: 'surname',
              align: 'left',
              sortable: true,
            },
            {
              text: this.$t('event.add.member.age'),
              value: 'age',
              align: 'left',
              sortable: true,
            },
            {
              value: 'icon',
              align: 'right',
              width: '1rem',
            },
          ],
          items: [
            {
              id: 1,
              forename: 'Max',
              surname: 'Mustermann',
              age: 14,
            },
            {
              id: 2,
              forename: 'Maxima',
              surname: 'Musterfrau',
              age: 14,
            },
          ],
        },
      },
    };
  },
  methods: {
    formatDate,
    submit() {
      // todo: implement this
    },
    /**
     * Removes the member with the given id from the list of members.
     * @param {number} id the unique identifier of the member to remove from the list
     */
    removeMember(id) {
      const member = this.event.member.items.filter(m => m.id === id)[0];
      const index = this.event.member.items.indexOf(member);

      this.event.member.items.splice(index, 1);
    },
  },
};
</script>

<style scoped>

</style>
