// Fixed bar at the top of the viewport showing how far the reader has
// scrolled down the current page (0% at the top, 100% at the bottom) --
// real request, 2026-09-17: a quick visual sense of "how much is left" on
// long chapters. Hidden entirely on pages short enough not to scroll (the
// homepage, short index pages), rather than sitting frozen at 0%/100%.
(function () {
  var bar = null;

  function ensureBar() {
    if (bar && document.body.contains(bar)) {
      return bar;
    }
    bar = document.getElementById("cci-reading-progress");
    if (!bar) {
      bar = document.createElement("div");
      bar.id = "cci-reading-progress";
      document.body.appendChild(bar);
    }
    return bar;
  }

  function update() {
    var el = ensureBar();
    var doc = document.documentElement;
    var scrollable = doc.scrollHeight - doc.clientHeight;

    if (scrollable <= 0) {
      el.hidden = true;
      return;
    }
    el.hidden = false;

    var pct = (doc.scrollTop / scrollable) * 100;
    el.style.width = Math.min(100, Math.max(0, pct)) + "%";
  }

  function attach() {
    ensureBar();
    update();
    // Safe to call on every real page load without accumulating duplicate
    // listeners: each is a fresh document (navigation.instant is off --
    // see mkdocs.yml). document$ is still used, matching this project's
    // other javascripts/*.js, in case instant nav is ever re-enabled.
    window.addEventListener("scroll", update, { passive: true });
    window.addEventListener("resize", update);
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
