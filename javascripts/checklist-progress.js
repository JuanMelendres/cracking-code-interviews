// Persists Mastery Checklist / Completion Criteria checkbox state per
// reader in localStorage, keyed by page path + position. Runs on every
// Material "instant navigation" content swap (document$), not just the
// initial load, since those swap the DOM without a full page reload.
(function () {
  function storageKey() {
    return "cci-checklist:" + window.location.pathname;
  }

  function restore() {
    var boxes = document.querySelectorAll(".task-list-item input[type=checkbox]");
    if (!boxes.length) {
      return;
    }

    var saved;
    try {
      saved = JSON.parse(window.localStorage.getItem(storageKey()) || "[]");
    } catch (err) {
      saved = [];
    }

    boxes.forEach(function (box, index) {
      box.checked = Boolean(saved[index]);
      box.addEventListener("change", function () {
        var state = Array.prototype.map.call(boxes, function (b) {
          return b.checked;
        });
        try {
          window.localStorage.setItem(storageKey(), JSON.stringify(state));
        } catch (err) {
          // Private browsing / storage disabled -- checkbox still toggles
          // for the current view, it just won't be remembered.
        }
      });
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(restore);
  } else {
    document.addEventListener("DOMContentLoaded", restore);
  }
})();
