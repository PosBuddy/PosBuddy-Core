module.exports = {
  '/api': {
    target: 'http://localhost:8443/staff',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  }
};
