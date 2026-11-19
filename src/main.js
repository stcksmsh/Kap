import "./style.css";

const reduce = matchMedia("(prefers-reduced-motion: reduce)");
const $ = (id) => document.getElementById(id);
const PRIMARY = "#a9c7ff"; // primaryDark
const SECONDARY = "#81d5ca"; // secondaryDark

/* ---------- units, as in the app's model/AppSettings.kt ---------- */
const VOLUME = {
  MILLILITERS: [1, 0, "ml"],
  LITERS: [1000, 2, "L"],
  OUNCES: [29.5735295625, 1, "oz"],
  CUPS: [240, 1, "cups"],
  PINTS: [473.176473, 2, "pt"],
  GALLONS: [3785.411784, 2, "gal"],
};
const WEIGHT = { KGS: [1, 0, "kg"], POUNDS: [0.45359237, 0, "lbs"], STONE: [6.35029318, 1, "st"] };
// toUnitString: HALF_UP to `decimals`, trailing zeros stripped
const fmt = (v, dec) => {
  const s = (Math.round((v + Number.EPSILON) * 10 ** dec) / 10 ** dec).toFixed(dec);
  return (dec ? s.replace(/\.?0+$/, "") : s).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};
const vol = (ml, unit = vunit) => `${fmt(ml / VOLUME[unit][0], VOLUME[unit][1])} ${VOLUME[unit][2]}`;
let vunit = "MILLILITERS";

/* ---------- wave drawing, after drawWavyWater() ---------- */
function wave(g, W, H, level, offset, color, amp) {
  const top = H - (H * level + amp);
  g.beginPath();
  g.moveTo(0, H);
  g.lineTo(0, top);
  const f = (2 * Math.PI) / W;
  for (let x = 0; x <= W; x += 2) g.lineTo(x, top + amp * Math.sin(f * x + offset));
  g.lineTo(W, H);
  g.closePath();
  g.fillStyle = color;
  g.fill();
}

function fitCanvas(c) {
  const dpr = Math.min(devicePixelRatio || 1, 2);
  const r = c.getBoundingClientRect();
  c.width = Math.round(r.width * dpr);
  c.height = Math.round(r.height * dpr);
  const g = c.getContext("2d");
  g.setTransform(dpr, 0, 0, dpr, 0, 0);
  return { g, W: r.width, H: r.height };
}

/* ---------- hero: the startup animation, then calm water ---------- */
const sea = $("sea");
let seaCtx = fitCanvas(sea);
const REST = 0.22;
const FILL_MS = 2500; // SimpleWaterFillAnimationScreen's default duration
const t0 = performance.now();
let heroVisible = true;
new IntersectionObserver(([e]) => (heroVisible = e.isIntersecting)).observe(sea);
addEventListener("resize", () => (seaCtx = fitCanvas(sea)));

function drawSea(now) {
  const { g, W, H } = seaCtx;
  const el = now - t0;
  let level;
  let splash = 0;
  if (reduce.matches) level = REST;
  else if (el < FILL_MS) {
    level = el / FILL_MS; // linear, like the app
    const raw = Math.min(1, Math.max(0, (level - 0.88) / 0.12));
    splash = 1 - raw * raw * (3 - 2 * raw);
  } else {
    const k = Math.min(1, (el - FILL_MS - 250) / 1400);
    const e = k < 0 ? 0 : 1 - Math.pow(1 - k, 3);
    level = 1 - (1 - REST) * e;
  }
  const off = reduce.matches ? 0 : (el / FILL_MS) * 6 * Math.PI;
  g.clearRect(0, 0, W, H);
  wave(g, W, H, level * 0.97, off + 1.4, "rgba(129,213,202,0.35)", 14);
  wave(g, W, H, level, off, PRIMARY, 20);
  const sp = $("splash");
  sp.style.opacity = el < FILL_MS && !reduce.matches ? String(splash) : "0";
  sp.style.transform = `translate(-50%, calc(-50% + ${16 * (1 - splash)}px))`;
  document.body.classList.toggle("ready", reduce.matches || el > FILL_MS + 500);
}

/* ---------- widget + glass ---------- */
const QUICK = [100, 250, 500, 750, 1000]; // AppSettings.defaultQuickWaterAdditionVolumes
let goal = 3316;
let entries = [];
let shown = 0; // animated level 0..1+
const glass = $("glass");
let glassCtx = fitCanvas(glass);
addEventListener("resize", () => (glassCtx = fitCanvas(glass)));
let glassVisible = false;
new IntersectionObserver(([e]) => (glassVisible = e.isIntersecting)).observe(glass);

function total() {
  return entries.reduce((a, e) => a + e.ml, 0);
}
function renderWidget() {
  const t = total();
  const p = goal > 0 ? t / goal : 0;
  $("w-amount").textContent = `${vol(t)} / ${vol(goal)}`;
  $("w-fill").style.transform = `scaleX(${Math.min(1, p)})`;
  $("w-bar").setAttribute("aria-valuenow", String(Math.round(Math.min(1, p) * 100)));
  $("w-msg").textContent = p >= 1 ? "Great job! You've met your goal!" : "Stay hydrated! Keep it up!";
  $("pct").textContent = `${Math.round(p * 100)}%`;
  $("w-btns").replaceChildren(
    ...QUICK.map((ml) => {
      const b = document.createElement("button");
      b.type = "button";
      b.className = "w-btn";
      b.textContent = `+${vol(ml).replace(" ", "\n")}`;
      b.setAttribute("aria-label", `Add ${vol(ml)}`);
      b.addEventListener("click", () => add(ml));
      return b;
    }),
  );
  const log = $("log");
  if (!entries.length) {
    log.innerHTML = '<li class="empty">Nothing yet. Tap a button.</li>';
  } else {
    log.replaceChildren(
      ...entries
        .slice()
        .reverse()
        .map((e) => {
          const li = document.createElement("li");
          const tm = document.createElement("span");
          tm.className = "mono";
          tm.textContent = e.at.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
          const v = document.createElement("b");
          v.textContent = vol(e.ml);
          const d = document.createElement("button");
          d.type = "button";
          d.className = "del";
          d.textContent = "Delete";
          d.setAttribute("aria-label", `Delete ${vol(e.ml)} at ${tm.textContent}`);
          d.addEventListener("click", () => {
            entries = entries.filter((x) => x !== e);
            renderWidget();
          });
          li.append(tm, v, d);
          return li;
        }),
    );
  }
}
function add(ml) {
  entries.push({ ml, at: new Date() });
  renderWidget();
  drops(ml);
}
$("reset").addEventListener("click", () => {
  entries = [];
  renderWidget();
});

// a few drops fall into the glass on every tap
const falling = [];
function drops(ml) {
  if (reduce.matches) return;
  const n = Math.min(8, 2 + Math.round(ml / 200));
  for (let i = 0; i < n; i++) falling.push({ x: 0.3 + Math.random() * 0.4, y: -0.05 - i * 0.07, v: 0 });
}

// glass outline: a tumbler, narrower at the bottom
function glassPath(g, W, H) {
  const top = 10, bot = H - 8, inset = W * 0.12;
  g.beginPath();
  g.moveTo(8, top);
  g.lineTo(8 + inset, bot);
  g.quadraticCurveTo(8 + inset, bot + 4, 14 + inset, bot + 4);
  g.lineTo(W - 14 - inset, bot + 4);
  g.quadraticCurveTo(W - 8 - inset, bot + 4, W - 8 - inset, bot);
  g.lineTo(W - 8, top);
}
function drawGlass(now) {
  const { g, W, H } = glassCtx;
  const target = Math.min(1.04, goal > 0 ? total() / goal : 0);
  shown += (target - shown) * (reduce.matches ? 1 : 0.06);
  g.clearRect(0, 0, W, H);
  g.save();
  glassPath(g, W, H);
  g.closePath();
  g.clip();
  const off = reduce.matches ? 0 : now / 400;
  const lvl = Math.max(0, Math.min(1, shown)) * 0.9;
  if (lvl > 0.002) {
    wave(g, W, H, lvl * 0.98, off + 1.2, "rgba(129,213,202,0.45)", 4);
    wave(g, W, H, lvl, off, PRIMARY, 6);
  }
  for (let i = falling.length - 1; i >= 0; i--) {
    const d = falling[i];
    d.v += 0.0016;
    d.y += d.v;
    const surface = 1 - lvl - 0.02;
    if (d.y > surface) {
      falling.splice(i, 1);
      continue;
    }
    if (d.y > 0) {
      g.fillStyle = SECONDARY;
      g.beginPath();
      g.ellipse(d.x * W, d.y * H, 3, 5, 0, 0, Math.PI * 2);
      g.fill();
    }
  }
  g.restore();
  g.strokeStyle = "rgba(226,226,233,0.55)";
  g.lineWidth = 2;
  glassPath(g, W, H);
  g.stroke();
}

/* ---------- goal calculator: calculateOptimalWaterIntake() ---------- */
function calcGoal() {
  const age = +$("age").value;
  const w = +$("weight").value;
  const act = +$("act").value;
  const kg = w * WEIGHT[$("wunit").value][0];
  vunit = $("vunit").value;
  if (!(age > 0) || !(kg > 0) || kg > 500 || act < 0) {
    $("goal-out").textContent = "—";
    $("parts").replaceChildren();
    renderWidget();
    return null;
  }
  const base = kg * 0.033 * 1000;
  const activity = (act / 60) * 0.35 * 1000;
  const ageAdj = age < 18 ? 200 : age < 55 ? 0 : -200;
  const ml = base + activity + ageAdj;
  $("goal-out").textContent = vol(ml);
  const li = (t) => Object.assign(document.createElement("li"), { textContent: t });
  $("parts").replaceChildren(
    li(`${fmt(kg, 1)} kg × 33 ml = ${vol(base)}`),
    li(`${act} min of activity = ${vol(activity)}`),
    li(ageAdj ? `age ${age}: ${ageAdj > 0 ? "+" : "−"}${vol(Math.abs(ageAdj))}` : `age ${age}: no adjustment`),
  );
  renderWidget();
  return ml;
}
["age", "weight", "act", "vunit"].forEach((id) => $(id).addEventListener("input", calcGoal));
$("wunit").addEventListener("change", (e) => {
  // keep the same body weight when switching units, like the app's input
  const prev = e.target.dataset.prev || "KGS";
  const kg = +$("weight").value * WEIGHT[prev][0];
  $("weight").value = fmt(kg / WEIGHT[e.target.value][0], WEIGHT[e.target.value][1]).replace(/,/g, "");
  e.target.dataset.prev = e.target.value;
  calcGoal();
});
$("use-goal").addEventListener("click", () => {
  const ml = calcGoal();
  if (ml) {
    goal = ml;
    renderWidget();
    $("widget").scrollIntoView({ behavior: reduce.matches ? "auto" : "smooth" });
  }
});

/* ---------- reminders: NextTickCalculator.nextTriggerUtcMillis() ---------- */
const toMin = (v) => {
  const [h, m] = v.split(":").map(Number);
  return h * 60 + m;
};
const hhmm = (m) => `${String(Math.floor(m / 60) % 24).padStart(2, "0")}:${String(m % 60).padStart(2, "0")}`;
function nextTick(now, start, end, interval) {
  if (now < start) return { at: start, day: "today" };
  if (now >= end) return { at: start, day: "tomorrow" };
  const next = start + Math.ceil((now - start) / interval) * interval;
  return next <= end ? { at: next, day: "today" } : { at: start, day: "tomorrow" };
}
function renderSched() {
  const start = toMin($("r-start").value || "08:00");
  const end = toMin($("r-end").value || "22:00");
  const iv = +$("r-int").value;
  const now = +$("r-now").value;
  $("r-int-out").textContent = iv >= 60 ? `${Math.floor(iv / 60)} h${iv % 60 ? ` ${iv % 60} min` : ""}` : `${iv} min`;
  $("r-now-out").textContent = hhmm(now);
  const pct = (m) => (m / 1440) * 100 + "%";
  const band = $("day-band");
  band.style.left = pct(start);
  band.style.width = end > start ? pct(end - start) : "0%";
  const n = nextTick(now, start, end, iv);
  const ticks = [];
  if (end > start) for (let t = start; t <= end; t += iv) ticks.push(t);
  $("ticks").replaceChildren(
    ...ticks.map((t) => {
      const d = document.createElement("i");
      d.className = "tick" + (t === n.at && n.day === "today" ? " on" : "") + (t < now ? " past" : "");
      d.style.left = pct(t);
      return d;
    }),
  );
  $("now-mark").style.left = pct(now);
  $("next").innerHTML = "";
  if (end <= start) {
    $("next").textContent = "The end of the day has to come after the start.";
    return;
  }
  const b = document.createElement("b");
  b.textContent = `${hhmm(n.at)} ${n.day}`;
  $("next").append(`It's ${hhmm(now)}. Next reminder: `, b, ` (anywhere up to five minutes after). ${ticks.length} reminder${ticks.length === 1 ? "" : "s"} a day.`);
}
["r-start", "r-end", "r-int", "r-now"].forEach((id) => $(id).addEventListener("input", renderSched));
const nowD = new Date();
$("r-now").value = String(nowD.getHours() * 60 + nowD.getMinutes());

/* ---------- loop ---------- */
function frame(now) {
  if (heroVisible || now - t0 < FILL_MS + 2000) drawSea(now);
  if (glassVisible) drawGlass(now);
  if (!reduce.matches) requestAnimationFrame(frame);
}
calcGoal();
goal = calcGoal() ?? goal;
renderWidget();
renderSched();
if (reduce.matches) {
  drawSea(performance.now());
  // redraw the static scenes when something changes
  const redraw = () => requestAnimationFrame((t) => (drawSea(t), drawGlass(t)));
  addEventListener("resize", redraw);
  document.addEventListener("click", () => setTimeout(redraw, 0));
  document.addEventListener("input", () => setTimeout(redraw, 0));
  redraw();
} else requestAnimationFrame(frame);

/* ---------- reveal ---------- */
if (!reduce.matches && "IntersectionObserver" in window) {
  const io = new IntersectionObserver(
    (es) => es.forEach((en) => en.isIntersecting && (en.target.classList.add("in"), io.unobserve(en.target))),
    { threshold: 0.1 },
  );
  document.querySelectorAll(".head, .features li, .story li, .calc, .result, .sched").forEach((el) => (el.classList.add("rv"), io.observe(el)));
}
