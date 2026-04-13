const SUPABASE_URL = 'https://obgfwjsuexlqhsmrbpju.supabase.co';
const SUPABASE_KEY = 'sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd';

function getGender() { return sessionStorage.getItem('gender') || 'M'; }
function setGender(g) { sessionStorage.setItem('gender', g); }
function getPlayersEndpoint() { return getGender() === 'W' ? '/players/women' : '/players'; }
function getRole() { return sessionStorage.getItem('sb_role') || 'fan'; }
function isCoach() { return ['coach','admin'].includes(getRole()); }
function isAdmin() { return getRole() === 'admin'; }

async function requireAuth() {
  const token = sessionStorage.getItem('sb_token');
  if (!token) { window.location.href = '/login.html'; return null; }
  const el = document.getElementById('nav-user-email');
  if (el) {
    const role = getRole();
    const badge = role === 'admin' ? '🔑' : role === 'coach' ? '🏒' : '👤';
    el.textContent = badge + ' ' + (sessionStorage.getItem('sb_email') || 'User');
  }
  return token;
}

async function requireCoach() {
  const token = await requireAuth();
  if (!token) return null;
  if (!isCoach()) {
    alert('This page requires Coach or Admin access.');
    window.location.href = 'index.html';
    return null;
  }
  return token;
}

async function requireAdmin() {
  const token = await requireAuth();
  if (!token) return null;
  if (!isAdmin()) {
    alert('This page requires Admin access.');
    window.location.href = 'index.html';
    return null;
  }
  return token;
}

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

async function deleteAccount() {
  if (!confirm('Are you sure you want to delete your account? This cannot be undone.')) return;
  const token = sessionStorage.getItem('sb_token');
  try {
    const res = await fetch('/delete-account', {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await res.json();
    if (data.success) {
      sessionStorage.clear();
      alert('Your account has been deleted.');
      window.location.href = '/login.html';
    } else {
      alert('Failed to delete account: ' + data.error);
    }
  } catch(e) {
    alert('Failed to delete account: ' + e.message);
  }
}

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

async function supabaseUpdate(table, id, updates) {
  const token = sessionStorage.getItem('sb_token');
  const res = await fetch(`${SUPABASE_URL}/rest/v1/${table}?id=eq.${id}`, {
    method: 'PATCH',
    headers: {
      'apikey': SUPABASE_KEY,
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      'Prefer': 'return=representation'
    },
    body: JSON.stringify(updates)
  });
  if (!res.ok) throw new Error(`Update failed: ${res.status}`);
  return res.json();
}

function injectGenderToggle() {
  const nav = document.querySelector('.nav');
  if (!nav) return;
  const isMen = getGender() === 'M';
  const wrap = document.createElement('div');
  wrap.style.cssText = 'padding: 12px 24px; border-bottom: 1px solid rgba(255,255,255,0.08); margin-bottom: 8px;';
  wrap.innerHTML = `
    <div style="font-size:0.6rem;letter-spacing:2px;text-transform:uppercase;color:rgba(232,240,248,0.4);margin-bottom:8px;">Tournament</div>
    <div style="display:flex;background:rgba(255,255,255,0.06);border-radius:8px;padding:3px;gap:3px;">
      <button onclick="switchGender('M')" style="flex:1;padding:7px 0;border:none;border-radius:6px;font-family:'Syne',sans-serif;font-size:0.78rem;font-weight:600;cursor:pointer;transition:all 0.2s;background:${isMen ? '#00c2ff' : 'transparent'};color:${isMen ? '#000' : 'rgba(232,240,248,0.5)'};">Men</button>
      <button onclick="switchGender('W')" style="flex:1;padding:7px 0;border:none;border-radius:6px;font-family:'Syne',sans-serif;font-size:0.78rem;font-weight:600;cursor:pointer;transition:all 0.2s;background:${!isMen ? '#00c2ff' : 'transparent'};color:${!isMen ? '#000' : 'rgba(232,240,248,0.5)'};">Women</button>
    </div>
  `;
  const logo = nav.querySelector('.nav-logo');
  if (logo && logo.nextSibling) nav.insertBefore(wrap, logo.nextSibling);
  else nav.appendChild(wrap);
}

function injectCoachNavLinks() {
  if (!isCoach()) return;
  const nav = document.querySelector('.nav');
  if (!nav) return;
  const bottom = nav.querySelector('.nav-bottom');
  const coachLinks = document.createElement('div');
  coachLinks.innerHTML = `
    <a href="lineups.html" class="nav-link" style="color:var(--gold)">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>
      Lineups
    </a>
    <a href="game-compare.html" class="nav-link" style="color:var(--gold)">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 19v-6a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2zm0 0V9a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v10m-6 0a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2m0 0V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v14a2 2 0 0 0-2 2h-2a2 2 0 0 0-2-2z"/></svg>
      Game Compare
    </a>
    ${isAdmin() ? `<a href="admin.html" class="nav-link" style="color:var(--gold)">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
      Admin
    </a>` : ''}
  `;
  nav.insertBefore(coachLinks, bottom);
}

function injectDeleteAccount() {
  const navBottom = document.querySelector('.nav-bottom');
  if (!navBottom) return;
  const btn = document.createElement('button');
  btn.className = 'btn-logout';
  btn.style.cssText = 'margin-top:8px;border-color:rgba(255,77,109,0.3);color:var(--danger);';
  btn.textContent = 'Delete Account';
  btn.onclick = deleteAccount;
  navBottom.appendChild(btn);
}

function switchGender(g) {
  setGender(g);
  window.location.reload();
}

document.addEventListener('DOMContentLoaded', () => {
  const path = window.location.pathname.split('/').pop();
  document.querySelectorAll('.nav-link').forEach(a => {
    if (a.getAttribute('href') === path) a.classList.add('active');
  });
  injectGenderToggle();
  injectCoachNavLinks();
  injectDeleteAccount();
});