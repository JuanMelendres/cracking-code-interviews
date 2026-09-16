// Shows two small boxes on a syllabus chapter page, both built ONLY from
// real, already-authored source data -- never a computed/inferred
// sequence:
//
// 1. "Scheduled in" -- every study-pack week that actually links to this
//    chapter (scripts/generate_pack_schedule_index.py).
// 2. "According to <path>, next" -- ONLY shown when this chapter is one
//    of a learning path's explicitly named, ordered priority topics, and
//    only ever states the next topic that SAME path names next --
//    real author-written order (a numbered table row, or a
//    semicolon-separated ordered list), never a guessed sequence from a
//    domain's full topic list. See
//    scripts/generate_learning_path_next_index.py's own module
//    docstring for exactly which source structures this reads and why
//    that reading is safe. Most chapters aren't named in any path's
//    priority-topic list, so most pages show nothing here -- that's
//    correct, not a bug.
(function () {
  var SCHEDULE_CACHE = null;
  var SCHEDULE_PROMISE = null;
  var NEXT_CACHE = null;
  var NEXT_PROMISE = null;

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

  function loadJson(cacheKey, path) {
    return function () {
      var promiseVar = cacheKey + "_PROMISE";
      if (cacheKey === "schedule" && SCHEDULE_PROMISE) {
        return SCHEDULE_PROMISE;
      }
      if (cacheKey === "next" && NEXT_PROMISE) {
        return NEXT_PROMISE;
      }
      var promise = fetch(baseUrl() + path)
        .then(function (res) {
          return res.ok ? res.json() : {};
        })
        .catch(function () {
          return {};
        })
        .then(function (data) {
          if (cacheKey === "schedule") {
            SCHEDULE_CACHE = data;
          } else {
            NEXT_CACHE = data;
          }
          return data;
        });
      if (cacheKey === "schedule") {
        SCHEDULE_PROMISE = promise;
      } else {
        NEXT_PROMISE = promise;
      }
      return promise;
    };
  }

  var loadSchedule = loadJson("schedule", "assets/pack-schedule-index.json");
  var loadNextIndex = loadJson("next", "assets/learning-path-next-index.json");

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

  function renderScheduledBox(entries) {
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

  function keyToUrl(key) {
    // "syllabus/06-databases/views-and-materialized-views.md" ->
    // "syllabus/06-databases/views-and-materialized-views/"
    return key.replace(/\.md$/, "") + "/";
  }

  function renderNextBox(entries) {
    var box = document.createElement("div");
    box.className = "cci-next-in-path";
    var base = baseUrl();

    entries.forEach(function (entry) {
      var line = document.createElement("div");
      line.className = "cci-next-in-path__line";

      var label = document.createElement("span");
      label.className = "cci-next-in-path__label";
      label.textContent = "According to " + entry.path + ", next: ";
      line.appendChild(label);

      if (entry.path_complete) {
        if (entry.next_key === null && entry.next_title) {
          // next_title here is the display text of the path's own
          // "## Next" link -- a real link to the NEXT PATH, not a chapter.
          line.appendChild(
            document.createTextNode(
              "you've completed this path's named sequence — continue with " +
                entry.next_title
            )
          );
        } else {
          line.appendChild(
            document.createTextNode("you've completed this path's named sequence")
          );
        }
      } else {
        var link = document.createElement("a");
        link.href = base + keyToUrl(entry.next_key);
        link.textContent = entry.next_title;
        line.appendChild(link);
      }

      box.appendChild(line);
    });

    return box;
  }

  function attach() {
    var key = chapterKeyFromPath();
    if (!key) {
      return;
    }
    var content = document.querySelector(".md-content__inner");
    if (!content || content.dataset.chapterContextChecked === key) {
      return;
    }
    content.dataset.chapterContextChecked = key;

    Promise.all([loadSchedule(), loadNextIndex()]).then(function (results) {
      var scheduleData = results[0];
      var nextData = results[1];

      var existingScheduled = content.querySelector(".cci-scheduled-in");
      if (existingScheduled) {
        existingScheduled.remove();
      }
      var existingNext = content.querySelector(".cci-next-in-path");
      if (existingNext) {
        existingNext.remove();
      }

      var readingTime = content.querySelector(".cci-reading-time");
      var h1 = content.querySelector("h1");
      var anchor = readingTime || h1;
      if (!anchor) {
        return;
      }

      // Both boxes insert right after `anchor`, in order: Scheduled in
      // first, then Next-in-path -- inserting each one moves `anchor`
      // forward so the second insert lands after the first box.
      var scheduleEntries = scheduleData[key];
      if (scheduleEntries && scheduleEntries.length) {
        var scheduledBox = renderScheduledBox(scheduleEntries);
        if (anchor.nextSibling) {
          anchor.parentNode.insertBefore(scheduledBox, anchor.nextSibling);
        } else {
          anchor.parentNode.appendChild(scheduledBox);
        }
        anchor = scheduledBox;
      }

      var nextEntries = nextData[key];
      if (nextEntries && nextEntries.length) {
        var nextBox = renderNextBox(nextEntries);
        if (anchor.nextSibling) {
          anchor.parentNode.insertBefore(nextBox, anchor.nextSibling);
        } else {
          anchor.parentNode.appendChild(nextBox);
        }
      }
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(attach);
  } else {
    document.addEventListener("DOMContentLoaded", attach);
  }
})();
