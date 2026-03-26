// ── Supabase config ──────────────────────────────────────────
const SUPABASE_URL = 'https://obgfwjsuexlqhsmrbpju.supabase.co';
const SUPABASE_KEY = 'sb_publishable_T9Bp76-AFViD1OFaR-bdhQ_QfotgWyd';

// ── Auth guard: redirect to login if not authenticated ───────
async function requireAuth() {
  const token = sessionStorage.getItem('sb_token');
  const email = sessionStorage.getItem('sb_email');
  if (!token) {
    window.location.href = '/login.html';
    return null;
  }
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

// ── Nav active link ──────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const path = window.location.pathname.split('/').pop();
  document.querySelectorAll('.nav-link').forEach(a => {
    if (a.getAttribute('href') === path) a.classList.add('active');
  });
});