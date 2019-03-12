<template>
  <!-- popup to obtain a member from the backend -->
  <v-dialog v-model="show" width="500" persistent>
    <v-card>
      <!-- header -->
      <v-card-title class="headline lighten-2" primary-title>
        {{$t('components.memberAddModal.search.title')}}
      </v-card-title>

      <!-- body -->
      <v-card-text>
        <v-text-field :label="$t('components.memberAddModal.search.input')"
                      v-model="input"
                      @keyup="searchMembers"
        ></v-text-field>

        <!-- list of found members -->
        <v-list style="max-height: 22rem; overflow: auto">
          <template v-for="(member, index) in members">
            <v-list-tile ripple @click="submitMember(member)" :key="member.id" avatar>
              <v-list-tile-content>
                <v-list-tile-title>{{member.name}} ({{member.age}})</v-list-tile-title>
                <v-list-tile-sub-title>{{member.disciplines}}</v-list-tile-sub-title>
              </v-list-tile-content>
            </v-list-tile>

            <!-- divider to separate the members -->
            <v-divider :key="index" v-if="index + 1 < members.length"></v-divider>
          </template>
        </v-list>
      </v-card-text>

      <!-- footer -->
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary"
               flat
               @click="closeModal"
        >
          {{$t('phrases.close')}}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { calculateAge } from '../helper';

export default {
  name: 'MemberAddModal',
  props: {
    show: Boolean,
  },
  data() {
    return {
      input: '',
      members: [],
    };
  },
  methods: {
    closeModal() {
      this.$set(this, 'input', '');
      this.$set(this, 'members', []);
      this.$emit('closeModal');
    },
    searchMembers() {
      if (this.input.length > 0) {
        this.$axios.get(`/member/byName/${this.input}`)
          .then((response) => {
            const members = [];

            // iterate over the found members
            response.data.forEach((member) => {
              // extract the names of the disciplines
              const disciplines = [];
              member.disciplines.forEach((discipline) => {
                disciplines.push(discipline.name);
              });

              // add the member to the list of found members
              members.push({
                id: member.id,
                forename: member.forename,
                surname: member.surname,
                name: `${member.forename} ${member.surname}`,
                age: calculateAge(member.dateOfBirth),
                disciplines: disciplines.join(', '),
              });
            });
            this.$set(this, 'members', members);
          })
          .catch();
      } else {
        this.$set(this, 'members', []);
      }
    },
    /**
     * Emits the given member to the parent component.
     *
     * @param member the member to emit
     */
    submitMember(member) {
      this.$emit('submitMember', member);
      this.closeModal();
    },
  },
};
</script>

<style scoped>

</style>
