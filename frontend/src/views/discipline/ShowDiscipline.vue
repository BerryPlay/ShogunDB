<template>
  <div>
    <!-- remove dialog -->
    <v-dialog v-model="showDeleteDialog" persistent max-width="400">
      <v-card>
        <v-card-title>
          <h2>{{$t('discipline.delete.title')}}</h2>
        </v-card-title>

        <v-card-text>
          <b>{{$t('discipline.delete.warning')}}:</b>
          {{$t('discipline.delete.body')}}
          <div align="right">
            <v-btn @click="showDeleteDialog = false">
              {{$t('phrases.no')}}
            </v-btn>
            <v-btn color="error"
                   dark
                   @click="remove"
            >
              {{$t('phrases.yes')}}
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- content -->
    <h1>{{$t('discipline.singular')}}: {{name}}</h1>

    <v-form lazy-validation
            ref="form"
    >
      <v-card>
        <v-card-text>
          <v-text-field counter="200"
                        :hint="hint"
                        label="Name"
                        :rules="rules"
                        v-model="discipline.name"
                        :color="color"
          ></v-text-field>
        </v-card-text>
      </v-card>

      <v-speed-dial v-model="openSpeedDial"
                    bottom
                    right
                    fixed
      >
        <v-btn slot="activator"
               v-model="fab"
               :color="color"
               dark
               fab
        >
          <v-icon>more_vert</v-icon>
          <v-icon>close</v-icon>
        </v-btn>
        <v-btn fab
               dark
               small
               color="blue"
               @click="submit"
        >
          <v-icon>save</v-icon>
        </v-btn>
        <v-btn fab
               dark
               small
               color="red"
               @click="showDeleteDialog = true"
        >
          <v-icon>delete</v-icon>
        </v-btn>
      </v-speed-dial>
    </v-form>
  </div>
</template>

<script>
export default {
  name: 'showDiscipline',
  data() {
    return {
      id: null,
      discipline: {},
      name: '',
      hint: this.$t('discipline.name.hint'),
      rules: [
        v => !!v || this.$t('discipline.name.required'),
        v => (v.length >= 1 && v.length <= 200)
          || this.$t('discipline.name.length'),
      ],
      color: 'blue',
      openSpeedDial: false,
      showDeleteDialog: false,
      fab: false,
    };
  },
  created() {
    // fetch the discipline
    this.$axios.get(`/discipline/${this.$route.params.id}`)
      .then((response) => {
        this.discipline = response.data;
        this.name = this.discipline.name;
      })
      .catch(() => this.$emit('message', this.$t('messages.errorDefault', 'error')));
  },
  methods: {
    /**
     * Validates all inputs and submits the form.
     */
    submit() {
      if (this.$refs.form.validate()) {
        this.$axios.put('/discipline', this.discipline)
          .then((response) => {
            this.$emit('message', this.$t('discipline.save.success'), 'success');
            this.discipline = response.data;
          })
          .catch(() => {
            this.$emit('message', this.$t('messages.errorDefault'), 'error');
          });
      }
    },
    /**
     * Removes the discipline from the database.
     */
    remove() {
      this.showDeleteDialog = false;

      this.$axios.delete(`/discipline/${this.discipline.id}`)
        .then(() => this.$router.push({
          name: 'indexDiscipline',
        }))
        .catch(() => {
          this.$emit('message', this.$('messages.errorDefault'), 'error');
        });
    },
  },
};
</script>

<style scoped>

</style>
