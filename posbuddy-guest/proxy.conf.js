module.exports = {
  '/api': {
    target: 'https://posbuddy.gve.elsenroth',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  },
  '/asset': {
    target: 'https://posbuddy.gve.elsenroth',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  }

};
