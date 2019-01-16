// import stylesheets
// import 'material-design-icons-iconfont/dist/material-design-icons.css';
import '@fortawesome/fontawesome-free/css/all.css';
import 'roboto-fontface/css/roboto/roboto-fontface.css';

// relative imports
import Vue from 'vue';
import '@babel/polyfill';

// absolute imports
import './plugins/axios';
import './plugins/vuetify';
import i18n from './i18n';
import App from './App.vue';
import router from './router';

Vue.config.productionTip = false;

/**
 * Authentication check if the meta value `noAuth` is set for a route.
 * If the token is not valid, the route `/login` will be loaded.
 */
router.beforeEach((to, from, next) => {
  if (to.meta.noAuth) {
    next();
  } else {
    Vue.axios.head(`/token/${localStorage.authenticationToken}`)
      .then(() => {
        next();
      })
      .catch(() => {
        next('/login');
      });
  }
});

new Vue({
  router,
  i18n,
  render: h => h(App),
}).$mount('#app');
