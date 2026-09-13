const path = require('path');
const webpack = require('webpack');

module.exports = {
  mode: 'development',
  devtool: false,
  context: __dirname,
  entry: './src/index.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    publicPath: 'http://localhost:3002/',
  },
  plugins: [
    new webpack.container.ModuleFederationPlugin({
      name: 'hostApp',
      remotes: {
        remoteApp: 'remoteApp@http://localhost:3001/remoteEntry.js',
      },
    }),
  ],
};
