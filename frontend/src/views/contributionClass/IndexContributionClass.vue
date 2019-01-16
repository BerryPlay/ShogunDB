<template>
  <div>
    <h1>{{$t('contributionClass.plural')}}</h1>

    <v-card>
      <v-card-text>
        <v-list two-line>
          <v-subheader>
            {{$t('contributionClass.categories.all')}}
          </v-subheader>
          <v-divider></v-divider>
          <v-list-tile :to="{name: 'showContributionClass', params: {id: contributionClass.id}}"
                       v-for="contributionClass in contributionClasses"
                       :key="contributionClass.id"
                       avatar
          >
            <v-list-tile-avatar>
              <v-icon>library_books</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="contributionClass.name"></v-list-tile-title>
              <v-list-tile-sub-title>
                {{$t('contributionClass.base.label')}}:
                {{(contributionClass.baseContribution).toFixed(2)}}€,

                {{$t('contributionClass.additional.label')}}:
                {{(contributionClass.additionalContribution).toFixed(2)}}€
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {
  name: 'IndexContributionClass',
  data() {
    return {
      contributionClasses: [],
    };
  },
  created() {
    // fetch the contribution classes
    this.$axios.get('/contributionClass')
      .then((response) => {
        this.contributionClasses = response.data;
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault'), 'error'));
  },
};
</script>

<style scoped>

</style>
