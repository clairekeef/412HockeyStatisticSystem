let gcData = null;
let h2hPeriodChart = null;

const CHART_DEFAULTS = {
  color: '#fff',
  borderColor: 'rgba(255,255,255,0.08)',
  tickColor: 'rgba(232,240,248,0.35)',
};

function chartBase(extra = {}) {
  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      x: { ticks: { color: CHART_DEFAULTS.tickColor, font: { size: 11 } }, grid: { color: CHART_DEFAULTS.borderColor } },
      y: { ticks: { color: CHART_DEFAULTS.tickColor, font: { size: 11 } }, grid: { color: CHART_DEFAULTS.borderColor }, beginAtZero: true },
    },
    ...extra,
  };
}

requireCoach().then(async token => {
  if (!token) return;
  try {
    const res = await fetch('/game-compare/sample');
    gcData = await res.json();
    renderAll();
  } catch (e) {
    document.querySelector('.summary-grid').innerHTML =
      '<div class="error" style="grid-column:1/-1">Failed to load game data.</div>';
  }
});

function renderAll() {
  renderSummaryCards();
  populateH2HSelectors();
  renderTrendCharts();
  renderPeriodChart();
  renderShotBattleChart();
  renderRadarChart();
  renderInsights();
}

// ── Summary cards ──────────────────────────────────────────────────────────

function renderSummaryCards() {
  const container = document.getElementById('summary-cards');
  container.innerHTML = gcData.games.map(g => {
    const wl = g.isWin ? 'win' : 'loss';
    return `
      <div class="game-card ${wl}">
        <div class="opp">vs ${g.opponent}</div>
        <div class="score ${wl}">USA ${g.usaScore}–${g.oppScore}</div>
        <div class="result-badge ${wl}">${g.isWin ? 'WIN' : 'LOSS'}</div>
        <div class="shots-sub">${g.usaShots} shots · ${g.usaPpPct}% PP</div>
      </div>`;
  }).join('');
}

// ── Head-to-head ────────────────────────────────────────────────────────────

function populateH2HSelectors() {
  const opts = gcData.games.map((g, i) =>
    `<option value="${i}">Game ${i + 1} — USA vs ${g.opponent} (${g.usaScore}–${g.oppScore})</option>`
  ).join('');
  document.getElementById('h2h-game1').innerHTML = opts;
  document.getElementById('h2h-game2').innerHTML = opts;
  document.getElementById('h2h-game2').selectedIndex = 1;
  renderH2H();
}

function renderH2H() {
  const i1 = parseInt(document.getElementById('h2h-game1').value);
  const i2 = parseInt(document.getElementById('h2h-game2').value);
  const g1 = gcData.games[i1];
  const g2 = gcData.games[i2];

  document.getElementById('h2h-name1').textContent = `vs ${g1.opponent}`;
  document.getElementById('h2h-name2').textContent = `vs ${g2.opponent}`;
  document.getElementById('h2h-result').style.display = 'block';

  const stats = [
    { label: 'Score',    v1: g1.usaScore,          v2: g2.usaScore,          higherWins: true },
    { label: 'Shots',    v1: g1.usaShots,           v2: g2.usaShots,          higherWins: true },
    { label: 'PP%',      v1: g1.usaPpPct,           v2: g2.usaPpPct,          higherWins: true },
    { label: 'FO%',      v1: g1.usaFaceoffPct,      v2: g2.usaFaceoffPct,     higherWins: true },
    { label: 'Hits',     v1: g1.usaHits,            v2: g2.usaHits,           higherWins: true },
    { label: 'Blocks',   v1: g1.usaBlockedShots,    v2: g2.usaBlockedShots,   higherWins: true },
    { label: 'PIM',      v1: g1.usaPenaltyMinutes,  v2: g2.usaPenaltyMinutes, higherWins: false },
  ];

  document.getElementById('h2h-bars').innerHTML = stats.map(s => {
    const max = Math.max(s.v1, s.v2, 1);
    const w1 = (s.v1 / max * 100).toFixed(1);
    const w2 = (s.v2 / max * 100).toFixed(1);
    const c1 = s.v1 === s.v2 ? '' : (s.higherWins ? (s.v1 > s.v2 ? 'win' : 'lose') : (s.v1 < s.v2 ? 'win' : 'lose'));
    const c2 = s.v1 === s.v2 ? '' : (s.higherWins ? (s.v2 > s.v1 ? 'win' : 'lose') : (s.v2 < s.v1 ? 'win' : 'lose'));
    return `
      <div class="stat-bar-row">
        <div class="sval left ${c1}">${s.v1}</div>
        <div class="sbar-wrap"><div class="sbar-fill" style="width:${w1}%"></div></div>
        <div class="sstat-label">${s.label}</div>
        <div class="sbar-wrap"><div class="sbar-fill right-bar" style="width:${w2}%"></div></div>
        <div class="sval right ${c2}">${s.v2}</div>
      </div>`;
  }).join('');

  // Period chart
  const labels = ['P1', 'P2', 'P3', 'OT'];
  if (h2hPeriodChart) h2hPeriodChart.destroy();
  h2hPeriodChart = new Chart(document.getElementById('h2h-period-chart'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        { label: `vs ${g1.opponent}`, data: g1.usaGoalsByPeriod, backgroundColor: 'rgba(0,194,255,0.7)', borderRadius: 4 },
        { label: `vs ${g2.opponent}`, data: g2.usaGoalsByPeriod, backgroundColor: 'rgba(255,186,0,0.6)',  borderRadius: 4 },
      ],
    },
    options: {
      ...chartBase(),
      plugins: {
        legend: { display: true, labels: { color: CHART_DEFAULTS.tickColor, font: { size: 11 } } },
      },
    },
  });
}

// ── Trend line charts ────────────────────────────────────────────────────────

function makeTrendChart(id, label, data, color) {
  const labels = gcData.games.map((g, i) => `G${i + 1} ${g.opponent}`);
  new Chart(document.getElementById(id), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label,
        data,
        borderColor: color,
        backgroundColor: color.replace(')', ', 0.08)').replace('rgb', 'rgba'),
        borderWidth: 2.5,
        tension: 0.35,
        pointRadius: 5,
        pointBackgroundColor: color,
        fill: true,
      }],
    },
    options: chartBase(),
  });
}

function renderTrendCharts() {
  const labels = gcData.games.map((g, i) => `G${i + 1} ${g.opponent}`);
  makeTrendChart('chart-shots', 'Shots on Goal', gcData.shotTrend,  'rgb(0,194,255)');
  makeTrendChart('chart-pp',    'PP %',          gcData.ppTrend,    'rgb(255,186,0)');
  makeTrendChart('chart-goals', 'Goals',         gcData.scoreTrend, 'rgb(0,200,100)');
  makeTrendChart('chart-pim',   'PIM',           gcData.pimTrend,   'rgb(255,77,109)');
}

// ── Period scoring pattern ────────────────────────────────────────────────────

function renderPeriodChart() {
  const labels = ['Period 1', 'Period 2', 'Period 3', 'Overtime'];
  new Chart(document.getElementById('chart-periods'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        { label: 'USA', data: gcData.totalUsaGoalsByPeriod, backgroundColor: 'rgba(0,194,255,0.75)', borderRadius: 5 },
        { label: 'Opp', data: gcData.totalOppGoalsByPeriod, backgroundColor: 'rgba(255,255,255,0.18)', borderRadius: 5 },
      ],
    },
    options: {
      ...chartBase(),
      plugins: {
        legend: { display: true, labels: { color: CHART_DEFAULTS.tickColor, font: { size: 11 } } },
      },
    },
  });
}

// ── Shot battle ───────────────────────────────────────────────────────────────

function renderShotBattleChart() {
  const labels = gcData.games.map(g => g.opponent);
  new Chart(document.getElementById('chart-shots-battle'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        { label: 'USA Shots',  data: gcData.games.map(g => g.usaShots),  backgroundColor: 'rgba(0,194,255,0.75)', borderRadius: 4 },
        { label: 'Opp Shots',  data: gcData.games.map(g => g.oppShots),  backgroundColor: 'rgba(255,255,255,0.18)', borderRadius: 4 },
      ],
    },
    options: {
      ...chartBase(),
      plugins: {
        legend: { display: true, labels: { color: CHART_DEFAULTS.tickColor, font: { size: 11 } } },
      },
    },
  });
}

// ── Radar ──────────────────────────────────────────────────────────────────────

function renderRadarChart() {
  const labels = ['Shots', 'Hits', 'Faceoff%', 'PP%', 'Blocks'];
  const colors = [
    ['rgba(0,194,255,0.7)',   'rgba(0,194,255,0.12)'],
    ['rgba(255,186,0,0.7)',   'rgba(255,186,0,0.08)'],
    ['rgba(0,200,100,0.7)',   'rgba(0,200,100,0.08)'],
    ['rgba(255,77,109,0.7)',  'rgba(255,77,109,0.08)'],
  ];

  const shotMax  = Math.max(...gcData.games.map(g => g.usaShots), 1);
  const hitMax   = Math.max(...gcData.games.map(g => g.usaHits), 1);
  const blockMax = Math.max(...gcData.games.map(g => g.usaBlockedShots), 1);

  const datasets = gcData.games.map((g, i) => ({
    label: `G${i + 1} vs ${g.opponent}`,
    data: [
      (g.usaShots / shotMax * 100).toFixed(1),
      (g.usaHits / hitMax * 100).toFixed(1),
      g.usaFaceoffPct,
      g.usaPpPct,
      (g.usaBlockedShots / blockMax * 100).toFixed(1),
    ],
    borderColor: colors[i][0],
    backgroundColor: colors[i][1],
    borderWidth: 2,
    pointBackgroundColor: colors[i][0],
    pointRadius: 4,
  }));

  new Chart(document.getElementById('chart-radar'), {
    type: 'radar',
    data: { labels, datasets },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: true, labels: { color: CHART_DEFAULTS.tickColor, font: { size: 11 }, boxWidth: 12 } },
      },
      scales: {
        r: {
          min: 0,
          max: 100,
          ticks: { display: false },
          grid: { color: 'rgba(255,255,255,0.08)' },
          angleLines: { color: 'rgba(255,255,255,0.08)' },
          pointLabels: { color: 'rgba(232,240,248,0.6)', font: { size: 12 } },
        },
      },
    },
  });
}

// ── Insights ───────────────────────────────────────────────────────────────────

function renderInsights() {
  const list = document.getElementById('insights-list');
  list.innerHTML = gcData.insights.map(s => `<li>${s}</li>`).join('');
}
