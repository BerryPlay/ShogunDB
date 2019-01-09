import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Home,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import(/* webpackChunkName: "about" */ './views/Login.vue'),
      meta: {
        noAuth: true,
      },
    },
    // contribution class routes
    {
      path: '/contributionClass/add',
      name: 'addContributionClass',
      component: () => import('./views/contributionClass/AddContributionClass.vue'),
    },
    {
      path: '/contributionClass/index',
      name: 'indexContributionClass',
      component: () => import('./views/contributionClass/IndexContributionClass.vue'),
    },
    {
      path: '/contributionClass/show/:id',
      name: 'showContributionClass',
      component: () => import('./views/contributionClass/ShowContributionClass.vue'),
    },
    // discipline routes
    {
      path: '/discipline/add',
      name: 'addDiscipline',
      component: () => import('./views/discipline/AddDiscipline.vue'),
    },
    {
      path: '/member/add',
      name: 'addMember',
      component: () => import('./views/member/AddMember.vue'),
    },
    {
      path: '/member/search',
      name: 'searchMember',
      component: () => import('./views/About.vue'),
    },
  ],
});
