<template>
  <div>
    <h1>Contribution classes</h1>

    <v-card>
      <v-card-text>
        <v-list two-line>
          <v-subheader>
            All contribution classes
          </v-subheader>
          <v-divider></v-divider>
          <v-list-tile :to="{name: 'showContributionClass', params: {id: contributionClass.id}}"
                       v-for="contributionClass in contributionClasses"
                       :key="contributionClass.id"
                       append-icon="library_books"
                       avatar
          >
            <v-list-tile-avatar>
              <v-icon>library_books</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="contributionClass.name"></v-list-tile-title>
              <v-list-tile-sub-title>
                base contribution: {{(contributionClass.baseContribution).toFixed(2)}}€,
                additional contribution: {{(contributionClass.additionalContribution).toFixed(2)}}€
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
        response.data.forEach((contributionClass) => {
          this.contributionClasses.push(contributionClass);
        });
      })
      .catch(() => this.$emit('showError', 'Something went wrong!'));
  },
};
</script>

<style scoped>

</style>
