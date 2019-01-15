/**
 * Prevent webpack to create empty css chunks.
 */
module.exports = {
  configureWebpack: {
    optimization: {
      splitChunks: false,
    },
  },

  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: true
    }
  }
};
