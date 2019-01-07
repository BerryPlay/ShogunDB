<template>
  <v-footer
    app
    fixed
    height="auto"
    inset
  >
    <v-card
      class="flex"
      flat
      tile
    >
      <v-card-actions class="justify-center">
        <strong>
          Visit ShogunDB on
          <a href="https://github.com/BerryPlay/ShogunDB" target="_blank">
            Github
          </a>
        </strong>
        <v-icon></v-icon>
      </v-card-actions>
    </v-card>
  </v-footer>
</template>

<script>
  export default {
    name: 'ApplicationFooter',
  };
</script>

<style scoped>
</style>
