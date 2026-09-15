// Shows a small "Scheduled in" box on a syllabus chapter page naming every
// study-pack week that actually links to it. Deliberately NOT a computed
// "next chapter in your path" suggestion -- see
// scripts/generate_pack_schedule_index.py's own module docstring for why
// that would risk inventing a sequence this repository's source material
// doesn't define. This only ever states a real, already-authored link.
(function () {
  var DATA_CACHE = null;
  var DATA_PROMISE = null;

  function baseUrl() {
    var script = document.currentScript;
    if (!script || !script.src) {
      // document.currentScript is null when re-invoked via document$
      // (Material's instant-nav hook) after the initial parse -- fall back
      // to locating our own <script> tag by src suffix.
      var scripts = document.querySelectorAll(
        'script[src$="javascripts/chapter-context.js"]'
      );
      script = scripts[scripts.length - 1];
    }
    if (!script) {
      return "/";
    }
    return script.src.replace(/javascripts\/chapter-context\.js.*$/, "");
  }

  function loadData() {
    if (DATA_PROMISE) {
      return DATA_PROMISE;
    }
    DATA_PROMISE = fetch(baseUrl() + "assets/pack-schedule-index.json")
      .then(function (res) {
        return res.ok ? res.json() : {};
      })
      .catch(function () {
        return {};
      })
      .then(function (data) {
        DATA_CACHE = data;
        return data;
      });
    return DATA_PROMISE;
  }

  function chapterKeyFromPath() {
    var path = window.location.pathname;
    var marker = "/syllabus/";
    var idx = path.indexOf(marker);
    if (idx === -1) {
      return null;
    }
    var rest = path.slice(idx + 1).replace(/\/$/, "");
    if (!rest) {
      return null;
    }
    return rest + ".md";
  }

  function renderBox(entries) {
    var box = document.createElement("div");
    box.className = "cci-scheduled-in";

    var label = document.createElement("span");
    label.className = "cci-scheduled-in__label";
    label.textContent = "Scheduled in: ";
    box.appendChild(label);

    var base = baseUrl();
    entries.forEach(function (entry, i) {
      if (i > 0) {
        box.appendChild(document.createTextNode(" · "));
      }
      var link = document.createElement("a");
      // entry.url is site-base-relative (no leading slash) so this works
      // whether the site is deployed at the domain root or a subpath.
      link.href = base + entry.url;
      link.textContent =
        entry.pack + (entry.week ? " (Week " + entry.week + ")" : "");
      box.appendChild(link);
    });

    return box;
  }

  function attach() {
    var key = chapterKeyFromPath();
    if (!key) {
      return;
    }
    var content = document.querySelector(".md-content__inner");
    if (!content || content.dataset.scheduledInChecked === key) {
      return;
    }
    content.dataset.scheduledInChecked = key;

    loadData().then(function (data) {
      var entries = data[key];
      if (!entries || !entries.length) {
        return;
      }
      var existing = content.querySelector(".cci-scheduled-in");
      if (existing) {
        existing.remove();
      }
      var box = renderBox(entries);
      // Sits below the reading-time line when one is present (server-
      // rendered by overrides/partials/content.html, so already in the DOM
      // by the time this runs), otherwise directly under the title.
      var readingTime = content.querySelector(".cci-reading-time");
      var h1 = content.querySelector("h1");
      var anchor = readingTime || h1;
      if (anchor && anchor.nextSibling) {
        anchor.parentNode.insertBefore(box, anchor.nextSibling);
      } else if (anchor) {
        anchor.parentNode.appendChild(box);
      } else {
        content.insertBefore(box, content.firstChild);
      }
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
