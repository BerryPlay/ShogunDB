<template>
  <div>
    <h1>{{$t('discipline.plural')}}</h1>

    <v-card>
      <v-card-text>
        <v-list two-line>
          <v-subheader>
            {{$t('discipline.categories.all')}}
          </v-subheader>
          <v-divider></v-divider>
          <v-list-tile :to="{name: 'showDiscipline', params: {id: discipline.id}}"
                       v-for="discipline in disciplines"
                       :key="discipline.id"
                       avatar
          >
            <v-list-tile-avatar>
              <v-icon>fas fa-dumbbell</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="discipline.name"></v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  name: 'IndexDiscipline',
  data() {
    return {
      disciplines: [],
    };
  },
  created() {
    // fetch the contribution classes
    this.$axios.get('/discipline')
      .then((response) => {
        this.disciplines = response.data;
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault'), 'error'));
  },
};
</script>

<style scoped>

</style>
