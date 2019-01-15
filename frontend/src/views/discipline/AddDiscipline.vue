<template>
  <div>
    <h1>Add new discipline</h1>
    <v-form lazy-validation
            ref="form"
    >
      <v-card>
        <v-card-text>
          <v-text-field counter="200"
                        hint="*required"
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
      rules: [
        v => !!v || 'Name is required',
        v => (v.length >= 1 && v.length <= 200)
          || 'Forename must have less than 200 character',
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
          .then(() => {
            this.$emit('message', 'Discipline was added successfully.', 'success');
          })
          .catch(() => {
            this.$emit('message', 'Something went wrong.', 'error');
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
