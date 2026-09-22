// Interactive system-design practice canvas: drag boxes representing
// system components onto a surface, connect them, export a PNG. No-op on
// every page except the one embedding #cci-canvas-surface. Layout persists
// per reader in localStorage, keyed by page path (same pattern as
// checklist-progress.js). Runs on Material's document$ instant-nav swap
// when available, DOMContentLoaded otherwise.
(function () {
  var NODE_WIDTH = 150;
  var NODE_HEIGHT = 48;
  var CLICK_MOVE_THRESHOLD = 5;

  function storageKey() {
    return "cci-design-canvas:" + window.location.pathname;
  }

  function loadState() {
    try {
      var raw = window.localStorage.getItem(storageKey());
      if (!raw) return { nodes: [], edges: [], nextId: 1 };
      var parsed = JSON.parse(raw);
      return {
        nodes: Array.isArray(parsed.nodes) ? parsed.nodes : [],
        edges: Array.isArray(parsed.edges) ? parsed.edges : [],
        nextId: typeof parsed.nextId === "number" ? parsed.nextId : 1,
      };
    } catch (err) {
      return { nodes: [], edges: [], nextId: 1 };
    }
  }

  function saveState(state) {
    try {
      window.localStorage.setItem(storageKey(), JSON.stringify(state));
    } catch (err) {
      // Private browsing / storage disabled -- canvas still works for the
      // current view, it just won't be remembered across reloads.
    }
  }

  function init(root) {
    var surface = root.querySelector("#cci-canvas-surface");
    var svg = root.querySelector("#cci-canvas-lines");
    var connectBtn = root.querySelector("#cci-canvas-connect");
    var exportBtn = root.querySelector("#cci-canvas-export");
    var clearBtn = root.querySelector("#cci-canvas-clear");
    var addButtons = root.querySelectorAll("[data-add]");
    if (!surface || !svg) return;

    var state = loadState();
    var connectMode = false;
    var pendingConnectId = null;
    var nodeEls = {};

    function clampX(x) {
      var max = Math.max(0, surface.clientWidth - NODE_WIDTH);
      return Math.min(Math.max(0, x), max);
    }

    function clampY(y) {
      var max = Math.max(0, surface.clientHeight - NODE_HEIGHT);
      return Math.min(Math.max(0, y), max);
    }

    function findNode(id) {
      for (var i = 0; i < state.nodes.length; i++) {
        if (state.nodes[i].id === id) return state.nodes[i];
      }
      return null;
    }

    function renderEdges() {
      while (svg.firstChild) svg.removeChild(svg.firstChild);
      state.edges.forEach(function (edge) {
        var from = findNode(edge.from);
        var to = findNode(edge.to);
        if (!from || !to) return;
        var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", from.x + NODE_WIDTH / 2);
        line.setAttribute("y1", from.y + NODE_HEIGHT / 2);
        line.setAttribute("x2", to.x + NODE_WIDTH / 2);
        line.setAttribute("y2", to.y + NODE_HEIGHT / 2);
        line.setAttribute("class", "design-canvas__edge");
        svg.appendChild(line);
      });
    }

    function removeNode(id) {
      state.nodes = state.nodes.filter(function (n) {
        return n.id !== id;
      });
      state.edges = state.edges.filter(function (e) {
        return e.from !== id && e.to !== id;
      });
      var el = nodeEls[id];
      if (el && el.parentNode) el.parentNode.removeChild(el);
      delete nodeEls[id];
      renderEdges();
      saveState(state);
    }

    function setConnectMode(on) {
      connectMode = on;
      pendingConnectId = null;
      Object.keys(nodeEls).forEach(function (id) {
        nodeEls[id].classList.remove("design-canvas__node--pending");
      });
      connectBtn.textContent = "Connect mode: " + (on ? "on" : "off");
      connectBtn.setAttribute("aria-pressed", on ? "true" : "false");
    }

    function handleNodeClick(node) {
      if (!connectMode) return;
      if (pendingConnectId === null) {
        pendingConnectId = node.id;
        nodeEls[node.id].classList.add("design-canvas__node--pending");
        return;
      }
      if (pendingConnectId === node.id) {
        nodeEls[node.id].classList.remove("design-canvas__node--pending");
        pendingConnectId = null;
        return;
      }
      var exists = state.edges.some(function (e) {
        return (
          (e.from === pendingConnectId && e.to === node.id) ||
          (e.from === node.id && e.to === pendingConnectId)
        );
      });
      if (!exists) {
        state.edges.push({ from: pendingConnectId, to: node.id });
        renderEdges();
        saveState(state);
      }
      nodeEls[pendingConnectId].classList.remove("design-canvas__node--pending");
      pendingConnectId = null;
    }

    function makeNodeEl(node) {
      var el = document.createElement("div");
      el.className = "design-canvas__node";
      el.style.left = node.x + "px";
      el.style.top = node.y + "px";
      el.setAttribute("data-node-id", String(node.id));

      var label = document.createElement("span");
      label.className = "design-canvas__node-label";
      label.textContent = node.label;
      el.appendChild(label);

      var del = document.createElement("button");
      del.type = "button";
      del.className = "design-canvas__node-delete";
      del.setAttribute("aria-label", "Delete " + node.label);
      del.textContent = "×";
      del.addEventListener("click", function (evt) {
        evt.stopPropagation();
        removeNode(node.id);
      });
      el.appendChild(del);

      el.addEventListener("dblclick", function (evt) {
        evt.stopPropagation();
        var next = window.prompt("Rename component:", node.label);
        if (next && next.trim()) {
          node.label = next.trim();
          label.textContent = node.label;
          saveState(state);
        }
      });

      var dragging = false;
      var moved = false;
      var startX = 0;
      var startY = 0;
      var origX = 0;
      var origY = 0;

      el.addEventListener("pointerdown", function (evt) {
        if (evt.target === del) return;
        dragging = true;
        moved = false;
        startX = evt.clientX;
        startY = evt.clientY;
        origX = node.x;
        origY = node.y;
        el.setPointerCapture(evt.pointerId);
      });

      el.addEventListener("pointermove", function (evt) {
        if (!dragging) return;
        var dx = evt.clientX - startX;
        var dy = evt.clientY - startY;
        if (Math.abs(dx) > CLICK_MOVE_THRESHOLD || Math.abs(dy) > CLICK_MOVE_THRESHOLD) {
          moved = true;
        }
        node.x = clampX(origX + dx);
        node.y = clampY(origY + dy);
        el.style.left = node.x + "px";
        el.style.top = node.y + "px";
        renderEdges();
      });

      el.addEventListener("pointerup", function (evt) {
        if (!dragging) return;
        dragging = false;
        el.releasePointerCapture(evt.pointerId);
        if (moved) {
          saveState(state);
        } else {
          handleNodeClick(node);
        }
      });

      return el;
    }

    function renderNode(node) {
      var el = makeNodeEl(node);
      nodeEls[node.id] = el;
      surface.appendChild(el);
    }

    function addComponent(label) {
      var count = state.nodes.length;
      var node = {
        id: state.nextId++,
        label: label,
        x: clampX(20 + (count % 4) * (NODE_WIDTH + 20)),
        y: clampY(20 + Math.floor(count / 4) * (NODE_HEIGHT + 24)),
      };
      state.nodes.push(node);
      renderNode(node);
      saveState(state);
    }

    function drawExportCanvas() {
      var width = Math.max(surface.clientWidth, 320);
      var height = Math.max(surface.clientHeight, 240);
      var canvas = document.createElement("canvas");
      canvas.width = width;
      canvas.height = height;
      var ctx = canvas.getContext("2d");

      ctx.fillStyle = "#ffffff";
      ctx.fillRect(0, 0, width, height);

      ctx.strokeStyle = "#94a3b8";
      ctx.lineWidth = 2;
      state.edges.forEach(function (edge) {
        var from = findNode(edge.from);
        var to = findNode(edge.to);
        if (!from || !to) return;
        ctx.beginPath();
        ctx.moveTo(from.x + NODE_WIDTH / 2, from.y + NODE_HEIGHT / 2);
        ctx.lineTo(to.x + NODE_WIDTH / 2, to.y + NODE_HEIGHT / 2);
        ctx.stroke();
      });

      ctx.font = "14px sans-serif";
      ctx.textAlign = "center";
      ctx.textBaseline = "middle";
      state.nodes.forEach(function (node) {
        ctx.fillStyle = "#eef2ff";
        ctx.strokeStyle = "#4338ca";
        ctx.lineWidth = 1.5;
        var x = node.x;
        var y = node.y;
        var r = 6;
        ctx.beginPath();
        ctx.moveTo(x + r, y);
        ctx.arcTo(x + NODE_WIDTH, y, x + NODE_WIDTH, y + NODE_HEIGHT, r);
        ctx.arcTo(x + NODE_WIDTH, y + NODE_HEIGHT, x, y + NODE_HEIGHT, r);
        ctx.arcTo(x, y + NODE_HEIGHT, x, y, r);
        ctx.arcTo(x, y, x + NODE_WIDTH, y, r);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        ctx.fillStyle = "#1e1b4b";
        ctx.fillText(node.label, x + NODE_WIDTH / 2, y + NODE_HEIGHT / 2, NODE_WIDTH - 12);
      });

      return canvas;
    }

    function exportPng() {
      var canvas = drawExportCanvas();
      var dataUrl = canvas.toDataURL("image/png");
      var link = document.createElement("a");
      link.href = dataUrl;
      link.download = "system-design-canvas.png";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      root.setAttribute("data-last-export", dataUrl.slice(0, 32));
      return dataUrl;
    }

    addButtons.forEach(function (btn) {
      btn.addEventListener("click", function () {
        addComponent(btn.getAttribute("data-add"));
      });
    });

    connectBtn.addEventListener("click", function () {
      setConnectMode(!connectMode);
    });

    exportBtn.addEventListener("click", exportPng);

    clearBtn.addEventListener("click", function () {
      if (state.nodes.length === 0 && state.edges.length === 0) return;
      if (!window.confirm("Clear the canvas? This can't be undone.")) return;
      state = { nodes: [], edges: [], nextId: 1 };
      Object.keys(nodeEls).forEach(function (id) {
        var el = nodeEls[id];
        if (el && el.parentNode) el.parentNode.removeChild(el);
      });
      nodeEls = {};
      renderEdges();
      saveState(state);
    });

    state.nodes.forEach(renderNode);
    renderEdges();

    root.setAttribute("data-cci-design-canvas-ready", "true");
  }

  function initAll() {
    var roots = document.querySelectorAll("[data-cci-design-canvas]");
    roots.forEach(function (root) {
      if (root.getAttribute("data-cci-design-canvas-ready") === "true") return;
      init(root);
    });
  }

  if (window.document$ && typeof window.document$.subscribe === "function") {
    window.document$.subscribe(initAll);
  } else {
    document.addEventListener("DOMContentLoaded", initAll);
  }
})();
