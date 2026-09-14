// Click-to-enlarge for Mermaid diagrams. Rendered SVGs are often too small
// to read the internals of a dense sequence/flow diagram at the article's
// fixed content width; this opens a click on `.mermaid` into a full-viewport
// overlay instead, closed by clicking it again, the backdrop, or Escape.
// Runs on every Material "instant navigation" content swap too, not just
// initial load, since re-running document$ is what re-attaches listeners
// to newly-swapped-in diagrams.
(function () {
  var OVERLAY_ID = "cci-diagram-zoom-overlay";

  function closeOverlay() {
    var overlay = document.getElementById(OVERLAY_ID);
    if (overlay) {
      overlay.remove();
      document.removeEventListener("keydown", onKeydown);
    }
  }

  function onKeydown(event) {
    if (event.key === "Escape") {
      closeOverlay();
    }
  }

  function openOverlay(mermaidEl) {
    var svg = mermaidEl.querySelector("svg");
    if (!svg) {
      return;
    }

    var overlay = document.createElement("div");
    overlay.id = OVERLAY_ID;
    overlay.addEventListener("click", closeOverlay);

    var clone = svg.cloneNode(true);
    clone.removeAttribute("height");
    clone.style.maxWidth = "95vw";
    clone.style.maxHeight = "95vh";
    clone.style.width = "auto";
    clone.style.height = "auto";
    overlay.appendChild(clone);

    document.body.appendChild(overlay);
    document.addEventListener("keydown", onKeydown);
  }

  function attach() {
    document.querySelectorAll(".mermaid").forEach(function (el) {
      if (el.dataset.zoomAttached) {
        return;
      }
      el.dataset.zoomAttached = "true";
      el.style.cursor = "zoom-in";
      el.title = "Click to enlarge";
      el.addEventListener("click", function () {
        openOverlay(el);
      });
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
