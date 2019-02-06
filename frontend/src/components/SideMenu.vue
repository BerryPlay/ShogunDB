<template>
  <!-- side menu container -->
  <v-navigation-drawer :clipped="clipped"
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
        <v-list-tile-title>{{$t('menu.home')}}</v-list-tile-title>
      </v-list-tile>

      <!-- manage group -->
      <v-list-group prepend-icon="work"
                    value="true"
      >
        <v-list-tile slot="activator">
          <v-list-tile-title>{{$t('menu.manage.label')}}</v-list-tile-title>
        </v-list-tile>

        <!-- iterator over every subgroup of the manage group -->
        <v-list-group :key="`${groupIndex}_${group.name}`"
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
          <v-list-tile :key="i"
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

      <!-- dark mode switch -->
      <v-list-tile @click="$emit('dark')">
        <v-list-tile-action>
          <v-icon v-if="!dark">fas fa-moon</v-icon>
          <v-icon v-else>fas fa-sun</v-icon>
        </v-list-tile-action>
        <v-list-tile-title v-if="!dark">{{$t('menu.switch.dark')}}</v-list-tile-title>
        <v-list-tile-title v-else>{{$t('menu.switch.light')}}</v-list-tile-title>
      </v-list-tile>

      <!-- language switch -->
      <v-list-tile @click="switchLanguage">
        <v-list-tile-action>
          <v-icon v-if="$i18n.locale === 'de'">fas fa-globe-americas</v-icon>
          <v-icon v-else>fas fa-globe-europe</v-icon>
        </v-list-tile-action>
        <v-list-tile-title v-if="$i18n.locale === 'de'">{{$t('menu.switch.en')}}</v-list-tile-title>
        <v-list-tile-title v-else>{{$t('menu.switch.de')}}</v-list-tile-title>
      </v-list-tile>

      <!-- logout -->
      <v-list-tile @click="$emit('logout')" to="/login" v-if="showLogout">
        <v-list-tile-action>
          <v-icon>exit_to_app</v-icon>
        </v-list-tile-action>
        <v-list-tile-title>{{$t('menu.logout')}}</v-list-tile-title>
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
    dark: Boolean,
  },
  data() {
    return {
      homeRoute: {
        name: 'dashboard',
      },
      groups: [
        {
          name: this.$t('menu.manage.member'),
          icon: 'fas fa-users',
          items: [
            {
              label: this.$t('menu.add'),
              icon: 'fas fa-user-plus',
              route: {
                name: 'addMember',
              },
            },
            {
              label: this.$t('menu.search'),
              icon: 'fas fa-search',
              route: {
                name: 'searchMember',
              },
            },
          ],
        },
        {
          name: this.$t('menu.manage.seminars'),
          icon: 'fas fa-graduation-cap',
          items: [
            {
              label: this.$t('menu.add'),
              icon: 'fas fa-plus',
              route: {
                name: 'about',
              },
            },
            {
              label: this.$t('menu.search'),
              icon: 'fas fa-search',
              route: {
                name: 'dashboard',
              },
            },
          ],
        },
        {
          name: this.$t('menu.manage.events'),
          icon: 'event',
          items: [
            {
              label: this.$t('menu.add'),
              icon: 'fas fa-plus',
              route: 'Route',
            },
            {
              label: this.$t('menu.search'),
              icon: 'fas fa-search',
              route: 'Route',
            },
          ],
        },
        {
          name: this.$t('menu.manage.disciplines'),
          icon: 'fas fa-dumbbell',
          items: [
            {
              label: this.$t('menu.add'),
              icon: 'fas fa-plus',
              route: {
                name: 'addDiscipline',
              },
            },
            {
              label: this.$t('menu.show.all'),
              icon: 'fas fa-list-ul',
              route: {
                name: 'indexDiscipline',
              },
            },
          ],
        },
        {
          name: this.$t('menu.manage.contributionClasses'),
          icon: 'library_books',
          items: [
            {
              label: this.$t('menu.add'),
              icon: 'library_add',
              route: {
                name: 'addContributionClass',
              },
            },
            {
              label: this.$t('menu.show.all'),
              icon: 'fas fa-list-ul',
              route: {
                name: 'indexContributionClass',
              },
            },
          ],
        },
      ],
    };
  },
  methods: {
    switchLanguage() {
      // todo: this does not update the site menu labels
      if (this.$i18n.locale === 'de') {
        this.$set(this.$i18n, 'locale', 'en');
      } else {
        this.$set(this.$i18n, 'locale', 'de');
      }
    },
  },
};
</script>

<style scoped>
</style>
