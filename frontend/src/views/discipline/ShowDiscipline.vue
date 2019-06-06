<template>
  <div>
    <delete-dialog :show="showDeleteDialog"
                   :title="$t('discipline.delete.title')"
                   :warning="$t('discipline.delete.warning')"
                   :warning-content="$t('discipline.delete.body')"
                   @yes="remove"
                   @no="showDeleteDialog = false"
    />

    <!-- content -->
    <h1>{{$t('discipline.singular')}}: {{name}}</h1>

    <v-form lazy-validation
            ref="form"
    >
      <v-layout row fill-height wrap class="pa-2">
        <v-flex lg4 xs12 class="pa-1">
          <v-card hover>
            <v-card-title>
              <h2>{{$t('discipline.categories.edit')}}</h2>
            </v-card-title>
            <v-card-text>
              <v-text-field counter="200"
                            :hint="hint"
                            :label="$t('discipline.name.label')"
                            :rules="rules"
                            v-model="discipline.name"
                            :color="color"
              ></v-text-field>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex lg8 xs12 class="pa-1">
          <v-card hover>
            <v-card-text>
              <h2>{{$t('discipline.member.title')}}</h2>

              <member-data-table :member="member"></member-data-table>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>

      <floating-button :color="color"
                       @delete="showDeleteDialog = true"
                       @save="submit"
      />
    </v-form>
  </div>
</template>

<script>
import DeleteDialog from '../../components/dialogs/DeleteDialog.vue';
import FloatingButton from '../../components/FloatingButton.vue';
import MemberDataTable from '../../components/MemberDataTable.vue';

export default {
  name: 'showDiscipline',
  components: {
    DeleteDialog,
    FloatingButton,
    MemberDataTable,
  },
  data() {
    return {
      id: null,
      discipline: {},
      member: [],
      search: '',

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

        // fetch the members of the discipline
        this.$axios.get(`/discipline/member/${this.discipline.id}`)
          .then((memberResponse) => {
            this.$set(this, 'member', memberResponse.data);
          });
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
