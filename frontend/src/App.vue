<template>
  <v-app>
    <!-- side menu -->
    <side-menu :clipped="clipped"
               :drawer="drawer"
               :show-logout="showLogout === 'true'"
               @logout="logout"
    ></side-menu>

    <!-- navbar -->
    <navbar :clipped="clipped"
            :drawer="drawer"
            :subtitle="subtitle"
            :title="title"
            @toggleClipped="clipped = !clipped"
            @toggleDrawer="drawer = !drawer"
    ></navbar>

    <v-content>
      <v-container>
        <router-view @login="login"/>
      </v-container>
    </v-content>
    <application-footer></application-footer>
  </v-app>
</template>

<script>
  import ApplicationFooter from './components/ApplicationFooter.vue';
  import Navbar from './components/Navbar.vue';
  import SideMenu from './components/SideMenu.vue';

  export default {
    name: 'App',
    components: {
      ApplicationFooter,
      Navbar,
      SideMenu,
    },
    data() {
      return {
        clipped: false,
        drawer: true,
        fixed: false,
        showLogout: localStorage.authenticated,
        items: [{
          icon: 'bubble_chart',
          title: 'Inspire',
        }],
        title: 'ShogunDB',
        subtitle: 'Membership Management Tool',
      };
    },
    methods: {
      /**
       * Removes the authentication token from the local storage and sets the authentication flag to
       * false.
       */
      logout() {
        // remove the token from the database
        this.$axios.delete(`/token/${localStorage.authenticationToken}`);

        // remove the token from the local storage
        localStorage.removeItem('authenticationToken');

        // set the authentication flag to false
        localStorage.authenticated = false;

        // hide the logout button
        this.showLogout = 'false';

        // enter the login route
        this.$router.push('/login');
      },
      /**
       * Shows the logout button.
       */
      login() {
        this.showLogout = 'true';
      },
    },
  };
</script>
