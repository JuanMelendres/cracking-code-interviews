// Timed six-phase system-design practice timer: counts down through
// Clarify / Estimate / API / Data / Architecture / Bottlenecks using the
// exact per-phase minute ranges from system-design-method-and-estimation.md's
// own cheat-sheet table, auto-advancing (with a beep) when a phase's time
// runs out. No-op on every page except the one embedding
// #cci-whiteboard-timer. Runs on Material's document$ instant-nav swap
// when available, DOMContentLoaded otherwise -- same pattern as
// checklist-progress.js / design-canvas.js.
(function () {
  var PHASES = ["Clarify", "Estimate", "API", "Data", "Architecture", "Bottlenecks"];

  // [Clarify, Estimate, API, Data, Architecture, Bottlenecks] minutes.
  var PRESETS = {
    25: [2, 3, 2, 3, 10, 5],
    41: [3, 5, 3, 5, 15, 10],
  };

  function pad2(n) {
    return n < 10 ? "0" + n : String(n);
  }

  function formatClock(totalSeconds) {
    var s = Math.max(0, Math.round(totalSeconds));
    var m = Math.floor(s / 60);
    var rem = s % 60;
    return pad2(m) + ":" + pad2(rem);
  }

  function init(root) {
    var inputs = root.querySelectorAll("[data-phase-minutes]");
    var startBtn = root.querySelector("#cci-timer-start");
    var pauseBtn = root.querySelector("#cci-timer-pause");
    var resetBtn = root.querySelector("#cci-timer-reset");
    var presetBtns = root.querySelectorAll("[data-preset]");
    var clockEl = root.querySelector("#cci-timer-clock");
    var labelEl = root.querySelector("#cci-timer-phase-label");
    var progressEl = root.querySelector("#cci-timer-progress");
    if (!inputs.length || !startBtn || !clockEl || !labelEl || !progressEl) return;

    var segments = [];
    PHASES.forEach(function (name, i) {
      var seg = document.createElement("div");
      seg.className = "whiteboard-timer__segment";
      seg.setAttribute("data-index", String(i));
      seg.title = (i + 1) + ". " + name;
      progressEl.appendChild(seg);
      segments.push(seg);
    });

    var durations = [];
    var currentPhaseIndex = 0;
    var remainingSeconds = 0;
    var intervalId = null;
    var running = false;
    var started = false;
    var audioCtx = null;

    function readDurations() {
      var result = [];
      inputs.forEach(function (input) {
        var minutes = parseFloat(input.value);
        if (isNaN(minutes) || minutes <= 0) minutes = 1;
        result.push(Math.round(minutes * 60));
      });
      return result;
    }

    function updateSegments() {
      segments.forEach(function (seg, i) {
        seg.classList.remove("is-current", "is-done");
        if (started && i < currentPhaseIndex) seg.classList.add("is-done");
        if (started && i === currentPhaseIndex && currentPhaseIndex < PHASES.length) {
          seg.classList.add("is-current");
        }
        if (currentPhaseIndex >= PHASES.length) seg.classList.add("is-done");
      });
    }

    function updateDisplay() {
      if (!started) {
        labelEl.textContent = "Ready — Phase 1: " + PHASES[0];
        clockEl.textContent = formatClock(durations[0] != null ? durations[0] : readDurations()[0]);
      } else if (currentPhaseIndex >= PHASES.length) {
        labelEl.textContent = "Session complete — all six phases done";
        clockEl.textContent = "00:00";
      } else {
        labelEl.textContent = "Phase " + (currentPhaseIndex + 1) + ": " + PHASES[currentPhaseIndex];
        clockEl.textContent = formatClock(remainingSeconds);
      }
      updateSegments();
    }

    function setInputsDisabled(disabled) {
      inputs.forEach(function (input) {
        input.disabled = disabled;
      });
      presetBtns.forEach(function (btn) {
        btn.disabled = disabled;
      });
    }

    function playBeep() {
      try {
        if (!audioCtx) {
          var Ctor = window.AudioContext || window.webkitAudioContext;
          if (!Ctor) return;
          audioCtx = new Ctor();
        }
        var osc = audioCtx.createOscillator();
        var gain = audioCtx.createGain();
        osc.type = "sine";
        osc.frequency.value = 880;
        gain.gain.value = 0.15;
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.start();
        osc.stop(audioCtx.currentTime + 0.18);
      } catch (err) {
        // Audio unavailable (autoplay policy, unsupported browser) -- the
        // visual phase change is still authoritative.
      }
    }

    function stopInterval() {
      if (intervalId !== null) {
        window.clearInterval(intervalId);
        intervalId = null;
      }
    }

    function tick() {
      remainingSeconds -= 1;
      if (remainingSeconds <= 0) {
        playBeep();
        currentPhaseIndex += 1;
        if (currentPhaseIndex >= PHASES.length) {
          stopInterval();
          running = false;
          startBtn.disabled = true;
          pauseBtn.disabled = true;
          setInputsDisabled(false);
          updateDisplay();
          return;
        }
        remainingSeconds = durations[currentPhaseIndex];
      }
      updateDisplay();
    }

    function startTimer() {
      if (running) return;
      if (!started) {
        durations = readDurations();
        currentPhaseIndex = 0;
        remainingSeconds = durations[0];
        started = true;
      }
      running = true;
      setInputsDisabled(true);
      startBtn.disabled = true;
      pauseBtn.disabled = false;
      updateDisplay();
      intervalId = window.setInterval(tick, 1000);
    }

    function pauseTimer() {
      if (!running) return;
      running = false;
      stopInterval();
      startBtn.disabled = false;
      pauseBtn.disabled = true;
    }

    function resetTimer() {
      stopInterval();
      running = false;
      started = false;
      currentPhaseIndex = 0;
      durations = readDurations();
      remainingSeconds = durations[0];
      startBtn.disabled = false;
      pauseBtn.disabled = true;
      setInputsDisabled(false);
      updateDisplay();
    }

    startBtn.addEventListener("click", startTimer);
    pauseBtn.addEventListener("click", pauseTimer);
    resetBtn.addEventListener("click", resetTimer);

    presetBtns.forEach(function (btn) {
      btn.addEventListener("click", function () {
        var key = btn.getAttribute("data-preset");
        var values = PRESETS[key];
        if (!values) return;
        inputs.forEach(function (input, i) {
          input.value = String(values[i]);
        });
        resetTimer();
      });
    });

    durations = readDurations();
    remainingSeconds = durations[0];
    updateDisplay();

    root.setAttribute("data-cci-whiteboard-timer-ready", "true");
  }

  function initAll() {
    var roots = document.querySelectorAll("[data-cci-whiteboard-timer]");
    roots.forEach(function (root) {
      if (root.getAttribute("data-cci-whiteboard-timer-ready") === "true") return;
      init(root);
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(initAll);
  } else {
    document.addEventListener("DOMContentLoaded", initAll);
  }
})();
