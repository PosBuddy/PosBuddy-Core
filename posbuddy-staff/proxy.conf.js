module.exports = {
  '/secure': {
    target: 'http://localhost:8443',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  }
};
