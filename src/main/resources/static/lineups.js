// ── State ─────────────────────────────────────────────────────────────────────
const lineupSlots = {};       // slotId → player object
const usedPlayers = new Set(); // player Names currently placed
let allPlayers    = [];

// ── Boot ──────────────────────────────────────────────────────────────────────
requireCoach().then(token => {
  if (!token) return;
  buildSlots();
  loadRoster();
});

// ── Build all slots ───────────────────────────────────────────────────────────
function buildSlots() {
  // Forward lines 1–4 (LW / C / RW)
  const fLines = document.getElementById('forward-lines');
  let fHtml = '';
  for (let i = 1; i <= 4; i++) {
    fHtml += `
      <div class="line-row forward-row">
        <div class="line-label">Line ${i}</div>
        <div class="slot" id="f${i}-lw" data-slot="f${i}-lw" data-pos="F" data-label="LW"></div>
        <div class="slot" id="f${i}-c"  data-slot="f${i}-c"  data-pos="F" data-label="C"></div>
        <div class="slot" id="f${i}-rw" data-slot="f${i}-rw" data-pos="F" data-label="RW"></div>
      </div>`;
  }
  fLines.innerHTML = fHtml;

  // Defense pairs 1–3 (LD / RD)
  const dPairs = document.getElementById('defense-pairs');
  let dHtml = '';
  for (let i = 1; i <= 3; i++) {
    dHtml += `
      <div class="line-row defense-row">
        <div class="line-label">Pair ${i}</div>
        <div class="slot" id="d${i}-ld" data-slot="d${i}-ld" data-pos="D" data-label="LD"></div>
        <div class="slot" id="d${i}-rd" data-slot="d${i}-rd" data-pos="D" data-label="RD"></div>
      </div>`;
  }
  dPairs.innerHTML = dHtml;

  // Goalies (Starter / Backup)
  document.getElementById('goalies').innerHTML = `
    <div class="line-row goalie-row">
      <div class="slot" id="g-starter" data-slot="g-starter" data-pos="GK" data-label="Starter"></div>
      <div class="slot" id="g-backup"  data-slot="g-backup"  data-pos="GK" data-label="Backup"></div>
    </div>`;

  // Render empty state + wire events for every slot
  document.querySelectorAll('.slot').forEach(el => {
    renderSlot(el);
    wireSlot(el);
  });
}

// ── Fetch roster ──────────────────────────────────────────────────────────────
async function loadRoster() {
  try {
    const res = await fetch('/players');
    if (!res.ok) throw new Error('Server returned ' + res.status);
    const data = await res.json();
    allPlayers = data.filter(p => p.country === 'United States');
    renderRoster();
  } catch (e) {
    document.getElementById('roster-panel').innerHTML =
      `<div style="color:var(--danger);font-size:0.82rem;">Failed to load roster: ${e.message}</div>`;
  }
}

function renderRoster() {
  const forwards = allPlayers.filter(p => p.position === 'F');
  const defense  = allPlayers.filter(p => p.position === 'D');
  const goalies  = allPlayers.filter(p => p.position === 'GK');

  document.getElementById('roster-panel').innerHTML =
    rosterSection('Forwards', forwards) +
    rosterSection('Defense',  defense)  +
    rosterSection('Goalies',  goalies);

  document.querySelectorAll('.player-card').forEach(card => {
    card.addEventListener('dragstart', onDragStart);
    card.addEventListener('dragend',   onDragEnd);
  });
}

function rosterSection(title, players) {
  if (!players.length) return '';
  return `
    <div class="roster-section-title">${title}</div>
    ${players.map(p => `
      <div class="player-card ${usedPlayers.has(p.name) ? 'used' : ''}"
           draggable="true"
           data-player='${JSON.stringify(p).replace(/'/g, '&#39;')}'>
        <span class="player-name">${p.name}</span>
        <div class="player-stats">
          <span class="pstat">G <span>${p.G ?? 0}</span></span>
          <span class="pstat">A <span>${p.A ?? 0}</span></span>
          <span class="pstat">P <span>${p.PTS ?? 0}</span></span>
        </div>
      </div>`).join('')}`;
}

// ── Drag & Drop ───────────────────────────────────────────────────────────────
function onDragStart(e) {
  if (e.currentTarget.classList.contains('used')) { e.preventDefault(); return; }
  e.currentTarget.classList.add('dragging');
  e.dataTransfer.effectAllowed = 'move';
  e.dataTransfer.setData('text/plain', e.currentTarget.dataset.player);
}

function onDragEnd(e) {
  e.currentTarget.classList.remove('dragging');
}

function wireSlot(el) {
  el.addEventListener('dragover',  e => { e.preventDefault(); el.classList.add('drag-over'); });
  el.addEventListener('dragleave', ()  => el.classList.remove('drag-over'));
  el.addEventListener('drop',      onDrop);
  el.addEventListener('click',     onSlotClick);
}

function onDrop(e) {
  e.preventDefault();
  const slotEl = e.currentTarget;
  slotEl.classList.remove('drag-over');

  let player;
  try { player = JSON.parse(e.dataTransfer.getData('text/plain')); }
  catch { return; }

  if (player.position !== slotEl.dataset.pos) {
    showStatus(`${player.name} is a ${posLabel(player.position)} and cannot fill a ${slotEl.dataset.label} slot.`, true);
    return;
  }

  // Free previous occupant of this slot
  const prev = lineupSlots[slotEl.dataset.slot];
  if (prev) usedPlayers.delete(prev.name);

  lineupSlots[slotEl.dataset.slot] = player;
  usedPlayers.add(player.name);

  renderSlot(slotEl);
  renderRoster();
  showStatus('');
}

function onSlotClick(e) {
  const slotEl = e.currentTarget;
  const player = lineupSlots[slotEl.dataset.slot];
  if (!player) return;
  delete lineupSlots[slotEl.dataset.slot];
  usedPlayers.delete(player.name);
  renderSlot(slotEl);
  renderRoster();
}

function renderSlot(el) {
  const player = lineupSlots[el.dataset.slot];
  const label  = el.dataset.label || '';
  if (player) {
    el.classList.add('filled');
    el.innerHTML = `
      <div class="slot-label">${label}</div>
      <div class="slot-player-name">${player.name}</div>
      <div class="slot-player-stats">G ${player.G ?? 0} · A ${player.A ?? 0} · P ${player.PTS ?? 0}</div>`;
  } else {
    el.classList.remove('filled');
    el.innerHTML = `
      <div class="slot-label">${label}</div>
      <div class="slot-empty-hint">Drop player here</div>`;
  }
}

// ── Clear ─────────────────────────────────────────────────────────────────────
function clearLineup() {
  Object.keys(lineupSlots).forEach(k => delete lineupSlots[k]);
  usedPlayers.clear();
  document.querySelectorAll('.slot').forEach(renderSlot);
  renderRoster();
  showStatus('Lineup cleared.');
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function posLabel(pos) {
  return pos === 'GK' ? 'Goalie' : pos === 'D' ? 'Defenseman' : 'Forward';
}

function showStatus(msg, isError = false) {
  const el = document.getElementById('status-bar');
  el.textContent = msg;
  el.style.color = isError ? 'var(--danger)' : 'var(--success)';
}
