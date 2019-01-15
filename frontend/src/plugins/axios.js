/* eslint-disable */
'use strict';

import Vue from 'vue';
import axios from 'axios';

// Full config:  https://github.com/axios/axios#request-config
axios.defaults.baseURL = process.env.VUE_APP_BASE_URL
  || `${window.location.protocol}//${window.location.host}`;
axios.defaults.headers.common.Authorization = `Bearer ${localStorage.authenticationToken || ''}`;
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

const config = {
  // baseURL: process.env.baseURL || process.env.apiUrl || ""
  // timeout: 60 * 1000, // Timeout
  // withCredentials: true, // Check cross-site Access-Control
};

const axiosInitialized = axios.create(config);

axiosInitialized.interceptors.request.use(
  (conf) => {
    // Do something before request is sent
    return conf;
  },
  (error) => {
    // Do something with request error
    return Promise.reject(error);
  },
);

// Add a response interceptor
axiosInitialized.interceptors.response.use(
  (response) => {
    // Do something with response data
    return response;
  },
  (error) => {
    // Do something with response error
    return Promise.reject(error);
  },
);

Plugin.install = (Vue, options) => {
  Vue.axios = axiosInitialized;
  window.axios = axiosInitialized;
  Object.defineProperties(Vue.prototype, {
    axios: {
      get() {
        return axiosInitialized;
      },
    },
    $axios: {
      get() {
        return axiosInitialized;
      },
    },
  });
};

Vue.use(Plugin);

export default Plugin;
