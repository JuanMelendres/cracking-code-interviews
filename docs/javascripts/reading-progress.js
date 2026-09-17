// Bar showing how far the reader has scrolled down the current page (0% at
// the top, 100% at the bottom) -- real request, 2026-09-17: a quick visual
// sense of "how much is left" on long chapters. Hidden entirely on pages
// short enough not to scroll (the homepage, short index pages), rather than
// sitting frozen at 0%/100%.
//
// On desktop (where Material renders the secondary "Table of contents"
// sidebar, >= 60em -- the same breakpoint Material's own CSS uses), the bar
// lives INSIDE that sidebar, pinned above the "Table of contents" label --
// real follow-up request, 2026-09-17, same day: the reader wants it visually
// attached to the TOC it's tracking progress through, not a generic
// full-width strip. Below that breakpoint (no secondary sidebar exists at
// all -- it moves into the hamburger drawer), the bar falls back to a
// full-width strip fixed to the true top of the viewport, above the header,
// so mobile/tablet readers still get it.
(function () {
  var bar = null;
  var desktopQuery = window.matchMedia("(min-width: 60em)");

  function ensureBar() {
    if (!bar || !document.body.contains(bar)) {
      bar = document.getElementById("cci-reading-progress");
      if (!bar) {
        bar = document.createElement("div");
        bar.id = "cci-reading-progress";
      }
    }
    place();
    return bar;
  }

  // Moves the bar between the secondary sidebar (desktop) and <body>
  // (mobile/tablet, or any page with no TOC sidebar at all) to match the
  // current viewport. A no-op if it's already in the right place.
  function place() {
    var sidebarInner = document.querySelector(
      ".md-sidebar--secondary .md-sidebar__inner"
    );
    if (desktopQuery.matches && sidebarInner) {
      if (bar.parentElement !== sidebarInner) {
        sidebarInner.insertBefore(bar, sidebarInner.firstChild);
        bar.classList.add("cci-reading-progress--in-toc");
      }
    } else if (bar.parentElement !== document.body) {
      document.body.appendChild(bar);
      bar.classList.remove("cci-reading-progress--in-toc");
    }
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
    if (typeof desktopQuery.addEventListener === "function") {
      desktopQuery.addEventListener("change", place);
    }
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
