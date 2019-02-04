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
          Visit ShogunDB <img alt="" class="icon" src="../assets/logo.svg"> on
          <a href="https://github.com/BerryPlay/ShogunDB" target="_blank">
            Github
            <v-icon class="black--text">fab fa-github</v-icon>
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
  .icon {
    width: 1.5rem;
    display: inline;
    position: relative;
    top: 0.15em;
  }
</style>
