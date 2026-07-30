---
title: The widget took four tries
date: 2024-11-26
tags: [devlog, kap, android, widget]
---

Spent two weeks fighting the widget. First pass was a classic [XML `AppWidgetProvider`](https://github.com/stcksmsh/Kap/commit/46bc0731d62a75c3455b6c16a83b33b624c8720f) — `RemoteViews`, the whole deal. Got it on the home screen, clicking it opened the app, fine. Then I wanted the click to work even when the app wasn't already running and the XML approach turned into a mess of edge cases, so I [ripped it out](https://github.com/stcksmsh/Kap/commit/475d731f332374dacff6de0b8e5b0a03fe29ec09) (`XMLWaterIntakeWidgetProvider` gone) and just... [didn't have a widget for a few days](https://github.com/stcksmsh/Kap/commit/3549e36adc60e94c16a749f8e5d52b98063fc5cd) while I cleaned up everything else — removed a bunch of `Context` passing I didn't need, redid the color scheme, let auto-refactor loose on the whole codebase.

In the middle of that I also got the graph working (past week only for now), paging on the intake list, and [notifications actually firing](https://github.com/stcksmsh/Kap/commit/7f03f0422ee1523af01ed203970aa6a1521b7145) for the first time ("Notifications WORK!!!" — commit message not lying, that was a good moment).

Widget came back a few days later as a [Jetpack Glance widget](https://github.com/stcksmsh/Kap/commit/4ace2d4566551bcee66c115a7ae99ba158365932) instead of raw RemoteViews — much less fighting the platform, though my first version of it was, in my own words at the time, "ugly as hell." Took one more pass after that to [make it look decent](https://github.com/stcksmsh/Kap/commit/0c66ed13361e0300c11872a4b87c6960a9816b99). So: three different widget implementations before landing on the one that's still in the app now.
