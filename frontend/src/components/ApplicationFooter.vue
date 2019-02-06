<template>
  <v-footer app
            fixed
            height="auto"
            inset
  >
    <v-card class="flex"
            flat
            tile
    >
      <v-card-actions class="justify-center">
        <strong>
          <a href="https://github.com/BerryPlay/ShogunDB" target="_blank">
            <v-chip class="hidden-md-and-down">
              {{$t('footer')}}
              <v-icon right>fab fa-github</v-icon>
            </v-chip>
            <v-chip class="hidden-lg-and-up">
              <v-icon>fab fa-github</v-icon>
            </v-chip>
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
  props: {
    dark: Boolean,
  },
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
