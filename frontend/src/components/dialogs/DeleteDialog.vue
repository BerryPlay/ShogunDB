<template>
  <v-dialog v-model="show" persistent max-width="400">
    <v-card>
      <v-card-title>
        <h2>{{title}}</h2>
      </v-card-title>

      <v-card-text>
        <b>{{warning}}:</b>
        {{warningContent}}
        <div align="right">
          <v-btn @click="$emit('no')">
            {{$t('phrases.no')}}
          </v-btn>
          <v-btn color="error"
                 dark
                 @click="$emit('yes')"
          >
            {{$t('phrases.yes')}}
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'DeleteDialog',
  props: {
    title: String,
    show: Boolean,
    warning: String,
    warningContent: String,
  },
};
</script>
