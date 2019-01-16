<template>
  <div>
    <h1>{{$t('discipline.add.title')}}</h1>
    <v-form lazy-validation
            ref="form"
    >
      <v-card>
        <v-card-text>
          <v-text-field counter="200"
                        :hint="hint"
                        label="Name"
                        :rules="rules"
                        v-model="name"
                        :color="color"
          ></v-text-field>
        </v-card-text>
      </v-card>
      <v-btn @click="submit"
             bottom
             :color="color"
             dark
             fab
             fixed
             right
      >
        <v-icon>save</v-icon>
      </v-btn>
    </v-form>
  </div>
</template>

<script>
export default {
  name: 'AddDiscipline',
  data() {
    return {
      name: '',
      hint: this.$t('discipline.name.hint'),
      rules: [
        v => !!v || this.$t('discipline.name.required'),
        v => (v.length >= 1 && v.length <= 200)
          || this.$t('discipline.name.length'),
      ],
      color: 'blue',
    };
  },
  methods: {
    /**
     * Validates all inputs and submits the form
     */
    submit() {
      if (this.$refs.form.validate()) {
        this.$axios.post('/discipline', {
          name: this.name,
        })
          .then((response) => {
            this.$emit('message', this.$t('discipline.add.success'), 'success');
            this.$router.push({
              name: 'showDiscipline',
              params: {
                id: response.data.id,
              },
            });
          })
          .catch(() => {
            this.$emit('message', this.$t('messages.errorDefault'), 'error');
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },
  },
};
</script>

<style scoped>

</style>
