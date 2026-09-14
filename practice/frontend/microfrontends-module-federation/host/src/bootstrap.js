// Module Federation requires the module that actually consumes a remote
// to be loaded asynchronously (this file), imported dynamically from a
// tiny synchronous entry point (index.js) -- this is what lets webpack
// negotiate shared dependencies with the remote container before any
// of this code runs.
import mountButton from 'remoteApp/Button';

const container = document.getElementById('app');
const label = document.createElement('p');
label.id = 'host-label';
label.textContent = 'Host shell loaded a component from a separately built remote:';
container.appendChild(label);
mountButton(container);
