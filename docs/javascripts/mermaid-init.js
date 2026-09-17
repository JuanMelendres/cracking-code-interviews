// mermaid@10 (extra_javascript) auto-initializes with its default light
// theme regardless of the site's own light/dark palette -- nothing in this
// repo told it about dark mode. Real bug (screenshot 2026-09-16): in dark
// mode, default-theme diagram text/edges render in colors barely visible
// against Material's dark "slate" background. Fix: re-initialize mermaid
// with theme "dark" or "default" matching the current
// data-md-color-scheme, both on first load and whenever the reader toggles
// light/dark (that toggle is instant, no page reload -- so already-rendered
// SVGs must be re-rendered from source, not just re-themed in place).
(function () {
  function currentTheme() {
    return document.body.getAttribute("data-md-color-scheme") === "slate"
      ? "dark"
      : "default";
  }

  // mermaid.min.js (loaded just before this script) auto-starts on its own
  // DOMContentLoaded listener with startOnLoad:true by default -- that
  // listener was already registered by the time this script runs, and it
  // fires before any listener this script adds below, racing our themed
  // re-render and leaving diagrams empty. Mermaid reads startOnLoad lazily
  // at fire time, not registration time, so calling initialize() here --
  // synchronously, right after mermaid.min.js executes, well before
  // DOMContentLoaded fires -- turns that default auto-start off in time.
  if (window.mermaid) {
    mermaid.initialize({ startOnLoad: false, theme: currentTheme() });
  }

  function renderDiagrams() {
    var diagrams = document.querySelectorAll(".mermaid");
    if (diagrams.length === 0) {
      return;
    }
    diagrams.forEach(function (node) {
      if (!node.hasAttribute("data-mermaid-src")) {
        node.setAttribute("data-mermaid-src", node.textContent);
      }
      node.removeAttribute("data-processed");
      node.innerHTML = node.getAttribute("data-mermaid-src");
    });
    mermaid.initialize({ startOnLoad: false, theme: currentTheme() });
    mermaid.init(undefined, diagrams);
  }

  document.addEventListener("DOMContentLoaded", renderDiagrams);

  new MutationObserver(renderDiagrams).observe(document.body, {
    attributes: true,
    attributeFilter: ["data-md-color-scheme"],
  });
})();
