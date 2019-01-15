/**
 * Prevent webpack to create empty css chunks.
 */
module.exports = {
  configureWebpack: {
    optimization: {
      splitChunks: false,
    },
  },
};
