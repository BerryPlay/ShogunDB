<template>
  <div>
    <h1>{{$t('message')}}</h1>
    <v-btn @click="$i18n.locale = 'en'">en</v-btn>
    <v-btn @click="$i18n.locale = 'de'">de</v-btn>
  </div>
</template>

<script>
  export default {};
</script>
