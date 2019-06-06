<template>
  <v-speed-dial v-model="show"
                bottom
                right
                fixed
  >
    <v-btn slot="activator"
           v-model="activator"
           :color="color"
           dark
           fab
    >
      <v-icon v-if="!show">{{icon}}</v-icon>
      <v-icon v-else>close</v-icon>
    </v-btn>
    <v-btn v-for="button in buttons"
           :key="button.icon"
           :color="button.color"
           fab
           dark
           small
           @click="$emit(button.event)"
    >
      <v-icon>{{button.icon}}</v-icon>
    </v-btn>
  </v-speed-dial>
</template>

<script>
export default {
  name: 'FloatingButton',
  props: {
    color: {
      type: String,
      default: 'blue',
    },
    buttons: {
      type: Array,
      default() {
        return [
          {
            color: 'blue',
            icon: 'save',
            event: 'save',
          },
          {
            color: 'red',
            icon: 'delete',
            event: 'delete',
          },
        ];
      },
    },
    icon: {
      type: String,
      default: 'more_vert',
    },
  },
  data() {
    return {
      show: false,
      activator: true,
    };
  },
};
</script>

<style scoped>

</style>
