<template>
  <v-toolbar
    :clipped-left="clipped"
    app
  >
    <v-toolbar-side-icon @click.stop="$emit('toggleDrawer')"></v-toolbar-side-icon>
    <v-btn @click.stop="$emit('toggleClipped')" class="hidden-sm-and-down" icon>
      <v-icon>web</v-icon>
    </v-btn>
    <v-toolbar-title class="headline ml-0">
      <span>{{title}}</span>
      <span class="font-weight-light hidden-sm-and-down"> {{ subtitle }}</span>
    </v-toolbar-title>
  </v-toolbar>
</template>

<script>
  export default {
    name: 'Navbar',
    props: {
      clipped: Boolean,
      drawer: Boolean,
      title: String,
      subtitle: String,
    },
  };
</script>

<style scoped>
</style>
