<template>
  <div>
    <h1>{{$t('event.search.title')}}</h1>

    <v-card hover>
      <v-card-title>
        <h2>{{$t('event.search.list')}}</h2>
        <v-spacer></v-spacer>
        <v-text-field v-model="search"
                      append-icon="search"
                      :label="$t('menu.search')"
                      hide-details
        ></v-text-field>
      </v-card-title>
      <v-data-table :headers="headers"
                    :items="events"
                    :search="search"
                    :rows-per-page-text="$t('event.search.rowsPerPageText')"
      >
        <template v-slot:items="props">
          <tr @click="$router.push({name: 'showEvent', params: {id: props.item.id}})">
            <td>{{ props.item.id }}</td>
            <td>{{ props.item.name }}</td>
            <td>{{ formatDate(props.item.date) }}</td>
          </tr>
        </template>
        <template v-slot:no-results>
          <v-alert :value="true" color="error" icon="warning">
            {{$t('event.search.noResults')}}
          </v-alert>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script>
import { formatDate } from '../../helper';

export default {
  name: 'SearchEvent',
  data() {
    return {
      search: '',
      events: [],
      headers: [
        {
          text: this.$t('event.search.id'),
          align: 'left',
          sortable: true,
          value: 'id',
        },
        {
          text: this.$t('event.search.name'),
          align: 'left',
          sortable: true,
          value: 'name',
        },
        {
          text: this.$t('event.search.date'),
          align: 'left',
          sortable: true,
          value: 'date',
        },
      ],
    };
  },
  created() {
    // fetch all events
    this.$axios.get('/event')
      .then((response) => {
        response.data.forEach((event) => {
          this.events.push({
            id: event.id,
            name: event.name,
            date: event.date,
          });
        });
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault'), 'error'));
  },
  methods: {
    formatDate,
  },
};
</script>
