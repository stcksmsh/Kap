# Kap — website branch

The source of Kap's website, <https://kostavukicevic.com/Kap/>. This is an orphan branch: it
shares no history with `master` and holds only the site.

- Plain Vite, no framework, one dev dependency. `npm install && npm run dev` to work on it,
  `npm run build` for `dist/`.
- `.github/workflows/deploy-website.yml` builds and deploys it to this repository's GitHub
  Pages on every push to `website` (Settings → Pages → Source: "GitHub Actions").
- Base path: `/Kap/` by default (`vite.config.js`). For a custom domain, set the repository
  variable `BASE_PATH` to `/`, add `public/CNAME`, and update `url` in `publication.json`.
- `publication.json` is the only file stcksmsh.github.io reads from this branch.
- Kap has no README, so the content comes from the code and the `io-page` devlogs. The
  interactive parts port the app's own logic: the hero is `SimpleWaterFillAnimationScreen` /
  `drawWavyWater`, the goal calculator is `calculateOptimalWaterIntake()` with the units from
  `model/AppSettings.kt`, the reminder planner is `NextTickCalculator`, and the widget copies
  `SimpleWidget` (default quick-add volumes, messages, colours from `ui/theme/Color.kt`).
  If those change on `master`, update them here.
- `public/media/icon.png` and `widget_preview.png` are copies from `master`'s `app/src/main/res/`.
