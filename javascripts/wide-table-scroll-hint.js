// Wide tables scroll horizontally inside Material's own
// `.md-typeset__scrollwrap` wrapper, but nothing signals that on a phone --
// confirmed via a real 375px-viewport check (2026-09-16): a 5-column
// table's last column silently ran off the right edge, with the OS's
// auto-hidden mobile scrollbar giving no visual hint there was more to
// see. This adds a one-line "swipe to see more" caption above any table
// whose wrapper is genuinely overflowing -- computed per-table at
// render time, not assumed from column count, so it never appears on a
// table that already fits.
(function () {
  var HINT_CLASS = "cci-scroll-hint";

  function attach() {
    var wraps = document.querySelectorAll(".md-typeset__scrollwrap");
    for (var i = 0; i < wraps.length; i++) {
      var wrap = wraps[i];
      var prev = wrap.previousElementSibling;
      if (prev && prev.classList.contains(HINT_CLASS)) {
        prev.remove();
      }
      if (wrap.scrollWidth <= wrap.clientWidth + 1) continue;
      var hint = document.createElement("p");
      hint.className = HINT_CLASS;
      hint.textContent = "↔ swipe to see more";
      wrap.parentNode.insertBefore(hint, wrap);
    }
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
