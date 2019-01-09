<template>
  <!-- side menu container -->
  <v-navigation-drawer
    :clipped="clipped"
    app
    enable-resize-watcher
    fixed
    persistent
    v-model="drawer"
  >
    <v-list>
      <!-- home -->
      <v-list-tile :to="homeRoute" exact-active-class>
        <v-list-tile-action>
          <v-icon>home</v-icon>
        </v-list-tile-action>
        <v-list-tile-title>Home</v-list-tile-title>
      </v-list-tile>

      <!-- manage group -->
      <v-list-group
        prepend-icon="work"
        value="true"
      >
        <v-list-tile slot="activator">
          <v-list-tile-title>Manage</v-list-tile-title>
        </v-list-tile>

        <!-- iterator over every subgroup of the manage group -->
        <v-list-group
          :key="`${groupIndex}_${group.name}`"
          no-action
          sub-group
          v-for="(group, groupIndex) in groups"
        >
          <!-- subgroup toggle -->
          <v-list-tile append-icon="work" slot="activator">
            <v-list-tile-title>{{group.name}}</v-list-tile-title>
            <v-list-tile-action>
              <v-icon v-text="group.icon"></v-icon>
            </v-list-tile-action>
          </v-list-tile>

          <!-- items of the subgroup -->
          <v-list-tile
            :key="i"
            :to="item.route"
            v-for="(item, i) in group.items"
          >
            <v-list-tile-title :to="item.route"
                               exact-active-class
                               v-text="item.label"
            ></v-list-tile-title>
            <v-list-tile-action>
              <v-icon v-text="item.icon"></v-icon>
            </v-list-tile-action>
          </v-list-tile>
        </v-list-group>
      </v-list-group>

      <!-- logout -->
      <v-list-tile @click="$emit('logout')" to="/login" v-if="showLogout">
        <v-list-tile-action>
          <v-icon>exit_to_app</v-icon>
        </v-list-tile-action>
        <v-list-tile-title>Logout</v-list-tile-title>
      </v-list-tile>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
  export default {
    name: 'Menu',
    props: {
      clipped: Boolean,
      drawer: Boolean,
      showLogout: Boolean,
    },
    data() {
      return {
        homeRoute: {name: 'dashboard'},
        groups: [
          {
            name: 'Member',
            icon: 'people',
            items: [
              {
                label: 'Add new',
                icon: 'person_add',
                route: {name: 'addMember'},
              },
              {
                label: 'Search',
                icon: 'search',
                route: {name: 'searchMember'},
              },
            ],
          },
          {
            name: 'Seminars',
            icon: 'school',
            items: [
              {
                label: 'Add new',
                icon: 'add',
                route: {name: 'about'},
              },
              {
                label: 'Search',
                icon: 'search',
                route: {name: 'dashboard'},
              },
            ],
          },
          {
            name: 'Events',
            icon: 'event',
            items: [
              {
                label: 'Add new',
                icon: 'add',
                route: 'Route',
              },
              {
                label: 'Search',
                icon: 'search',
                route: 'Route',
              },
            ],
          },
          {
            name: 'Discipline',
            icon: 'rowing',
            items: [
              {
                label: 'Add new',
                icon: 'add',
                route: {name: 'addDiscipline'},
              },
            ],
          },
          {
            name: 'Contribution class',
            icon: 'library_books',
            items: [
              {
                label: 'Add new',
                icon: 'library_add',
                route: {name: 'addContributionClass'},
              },
              {
                label: 'Show all',
                icon: 'view_list',
                route: {name: 'indexContributionClass'},
              },
            ],
          },
        ],
      };
    },
  };
</script>

<style scoped>
</style>
