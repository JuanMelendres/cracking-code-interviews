// Injects a small L1-L4 mastery-level badge next to section headings that
// already name their own level, so a reader who lands mid-chapter (from
// search, or a direct link) can immediately tell whether they're looking
// at Foundation or Staff-depth material without reading surrounding prose.
//
// This does NOT touch any source content -- it pattern-matches two real,
// pre-existing heading conventions already used across every chapter
// (verified against the full syllabus/ tree, not assumed):
//   - the older template's "Level 1 -- Foundation" / "Level 2 -- Working
//     Knowledge" H2s (used in ~181 chapters, L1/L2 only -- this template
//     has no single heading that marks L3/L4 content, so no badge is
//     invented for headings that don't already state a level)
//   - the newer numbered template's "N. <Title> (L1)".."(L4)" H2s (used
//     in ~51 chapters, all four levels)
// A heading this script doesn't recognize is left completely alone.
(function () {
  var LEVEL_LABELS = {
    1: "Foundation",
    2: "Working Knowledge",
    3: "Senior",
    4: "Staff",
  };

  // Matches the older template's two explicitly-numbered headings.
  var OLD_TEMPLATE_RE = /^Level\s+([12])\s*[—-]\s*/;
  // Matches the newer template's "... (L1)".."(L4)" suffix, with or
  // without a leading "N. " ordinal (MkDocs strips the ordinal from the
  // rendered heading text in some builds, keeps it in others).
  var NEW_TEMPLATE_RE = /\(L([1-4])\)\s*$/;

  function badgeFor(level) {
    var span = document.createElement("span");
    span.className = "cci-level-badge cci-level-badge--" + level;
    span.textContent = "L" + level + " · " + LEVEL_LABELS[level];
    return span;
  }

  function attach() {
    var headings = document.querySelectorAll(
      ".md-content__inner h2, .md-content__inner h3"
    );
    headings.forEach(function (heading) {
      if (heading.dataset.levelBadgeChecked) {
        return;
      }
      heading.dataset.levelBadgeChecked = "true";

      // Read only the heading's own text node content, ignoring the
      // permalink anchor MkDocs appends, so matching isn't thrown off by
      // its markup.
      var text = heading.textContent.replace(/¶\s*$/, "").trim();

      var level = null;
      var oldMatch = text.match(OLD_TEMPLATE_RE);
      if (oldMatch) {
        level = oldMatch[1];
      } else {
        var newMatch = text.match(NEW_TEMPLATE_RE);
        if (newMatch) {
          level = newMatch[1];
        }
      }

      if (level) {
        heading.insertBefore(badgeFor(level), heading.firstChild);
      }
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
