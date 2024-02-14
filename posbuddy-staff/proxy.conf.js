module.exports = {
  '/secure': {
    target: 'https://localhost:8443',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  }
};
