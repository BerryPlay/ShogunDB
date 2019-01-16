import Vue from 'vue';
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css';
import de from 'vuetify/es5/locale/de';

Vue.use(Vuetify, {
  iconfont: 'fa',
  lang: {
    locales: {
      de,
    },
    current: 'de',
  },
});
