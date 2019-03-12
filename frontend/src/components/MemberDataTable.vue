<template>
  <div>
    <!-- member search field -->
    <v-layout row>
      <v-flex lg6 offset-lg6>
        <v-text-field v-model="search"
                      append-icon="search"
                      :label="$t('discipline.member.search')"
                      single-line
                      hide-details
        ></v-text-field>
      </v-flex>

    </v-layout>

    <!-- member table -->
    <v-data-table :items="member"
                  :headers="tableHeaders"
                  :search="search"
                  :no-data-text="$t('table.empty')"
                  :no-results-text="$t('table.result')"
                  :rows-per-page-text="$t('table.rows')"
    >
      <template slot="items" scope="props">
        <tr @click="showMember(props.item.id)">
          <td class="text-xs-left">{{props.item.forename}}</td>
          <td class="text-xs-left">{{props.item.surname}}</td>
          <td class="text-xs-left">{{calculateAge(props.item.dateOfBirth)}}</td>
        </tr>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import { calculateAge } from '../helper';

export default {
  name: 'MemberDataTable',
  props: {
    member: Array,
  },
  data() {
    return {
      search: '',
      tableHeaders: [
        {
          text: this.$t('discipline.member.forename'),
          align: 'left',
          value: 'forename',
        },
        {
          text: this.$t('discipline.member.surname'),
          align: 'left',
          value: 'surname',
        },
        {
          text: this.$t('discipline.member.age'),
          align: 'left',
          value: 'age',
        },
      ],
    };
  },
  methods: {
    /**
     * Pushes to the member show route.
     *
     * @param {number} id the unique identifier of the member to show
     */
    showMember(id) {
      // todo: insert the correct route
      this.$router.push(`/dashboard/${id}`);
    },
    calculateAge,
  },
};
</script>

<style scoped>

</style>
