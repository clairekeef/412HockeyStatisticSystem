// ── Supabase config ──────────────────────────────────────────
const SUPABASE_URL = 'https://obgfwjsuexlqhsmrbpju.supabase.co';
const SUPABASE_KEY = 'sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd';

// ── Gender state (persisted across pages) ────────────────────
function getGender() {
  return sessionStorage.getItem('gender') || 'M';
}
function setGender(g) {
  sessionStorage.setItem('gender', g);
}
function getPlayersEndpoint() {
  return getGender() === 'W' ? '/players/women' : '/players';
}

// ── Auth guard ───────────────────────────────────────────────
async function requireAuth() {
  const token = sessionStorage.getItem('sb_token');
  const email = sessionStorage.getItem('sb_email');
  if (!token) { window.location.href = '/login.html'; return null; }
  const el = document.getElementById('nav-user-email');
  if (el) el.textContent = email || 'User';
  return token;
}

// ── Logout ───────────────────────────────────────────────────
async function logout() {
  const token = sessionStorage.getItem('sb_token');
  if (token) {
    await fetch(`${SUPABASE_URL}/auth/v1/logout`, {
      method: 'POST',
      headers: { 'apikey': SUPABASE_KEY, 'Authorization': `Bearer ${token}` }
    });
  }
  sessionStorage.clear();
  window.location.href = '/login.html';
}

// ── Supabase REST helper ─────────────────────────────────────
async function supabaseQuery(table, params = '') {
  const token = sessionStorage.getItem('sb_token');
  const res = await fetch(`${SUPABASE_URL}/rest/v1/${table}?${params}`, {
    headers: {
      'apikey': SUPABASE_KEY,
      'Authorization': `Bearer ${token || SUPABASE_KEY}`,
      'Content-Type': 'application/json'
    }
  });
  if (!res.ok) throw new Error(`Supabase error: ${res.status}`);
  return res.json();
}

// ── Inject gender toggle into nav ────────────────────────────
function injectGenderToggle() {
  const nav = document.querySelector('.nav');
  if (!nav) return;

  const isMen = getGender() === 'M';
  const wrap = document.createElement('div');
  wrap.style.cssText = 'padding: 12px 24px; border-bottom: 1px solid rgba(255,255,255,0.08); margin-bottom: 8px;';
  wrap.innerHTML = `
    <div style="font-size:0.6rem;letter-spacing:2px;text-transform:uppercase;color:rgba(232,240,248,0.4);margin-bottom:8px;">Tournament</div>
    <div style="display:flex;background:rgba(255,255,255,0.06);border-radius:8px;padding:3px;gap:3px;">
      <button id="toggle-men" onclick="switchGender('M')" style="flex:1;padding:7px 0;border:none;border-radius:6px;font-family:'Syne',sans-serif;font-size:0.78rem;font-weight:600;cursor:pointer;transition:all 0.2s;background:${isMen ? '#00c2ff' : 'transparent'};color:${isMen ? '#000' : 'rgba(232,240,248,0.5)'};">Men</button>
      <button id="toggle-women" onclick="switchGender('W')" style="flex:1;padding:7px 0;border:none;border-radius:6px;font-family:'Syne',sans-serif;font-size:0.78rem;font-weight:600;cursor:pointer;transition:all 0.2s;background:${!isMen ? '#00c2ff' : 'transparent'};color:${!isMen ? '#000' : 'rgba(232,240,248,0.5)'};">Women</button>
    </div>
  `;

  // Insert after logo
  const logo = nav.querySelector('.nav-logo');
  if (logo && logo.nextSibling) {
    nav.insertBefore(wrap, logo.nextSibling);
  } else {
    nav.appendChild(wrap);
  }
}

function switchGender(g) {
  setGender(g);
  // Reload the current page to refresh data
  window.location.reload();
}

// ── Nav active link + toggle injection ───────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const path = window.location.pathname.split('/').pop();
  document.querySelectorAll('.nav-link').forEach(a => {
    if (a.getAttribute('href') === path) a.classList.add('active');
  });
  injectGenderToggle();
});