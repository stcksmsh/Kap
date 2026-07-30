---
title: WorkManager was never going to give me exact timing
date: 2025-08-10
tags: [devlog, kap, android, notifications]
---

Long gap since the last post — this is everything interesting since then, which is really one thing: I [redid how reminders get scheduled](https://github.com/stcksmsh/Kap/commit/11bac7e9fcdba5724a844649e55f67ab971ef773).

They were running on WorkManager, a `OneTimeWorkRequest` that re-enqueues itself. Worked in testing, then I left the phone alone for a while and reminders started landing whenever, not on the interval I'd set. Turns out that's just what WorkManager is — "run this eventually, respecting Doze/battery constraints," not "run this at 2:00pm." `setExpedited` doesn't fix that.

Switched to `AlarmManager.setWindow()` plus a plain broadcast receiver instead. One alarm at a time, five-minute flex, and I compute the next trigger time myself rather than trusting a work chain — before the window, target start time; after it, roll to tomorrow; inside it, round up to the next interval and clamp to the end. Had to actually think through those edge cases instead of hand-waving them.

The receiver shows the notification if we're still inside today's window when it fires, then immediately schedules the next one. Also added a `BootReceiver`, since alarms don't survive a reboot the way a WorkManager chain sort of does for free — now I own that instead.

Trade-off's real, but reminders land within ~5 minutes of when they should now instead of "eventually." Health Connect sync went in right after this too (just permissions/plumbing, nothing syncing yet) — probably the next one.
