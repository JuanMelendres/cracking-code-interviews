// Default Material search placeholder is a generic "Search" with no hint
// that the index tokenizes technical terms (CAP theorem, CSRF, JMM, etc.)
// well. Overriding the input's placeholder with a real example query
// reduces that discovery friction. Material's own i18n system has no
// single override point for this string short of a full custom language
// file, so this sets the DOM attribute directly -- same pattern as this
// repo's other javascripts/*.js, re-run on every instant-navigation swap.
(function () {
  var HINT = 'intenta "CAP theorem" o "CSRF"';

  function attach() {
    var inputs = document.querySelectorAll('[data-md-component="search-query"]');
    for (var i = 0; i < inputs.length; i++) {
      inputs[i].setAttribute("placeholder", HINT);
    }
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
