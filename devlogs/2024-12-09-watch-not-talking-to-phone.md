---
title: Tried to get the watch talking to the phone
date: 2024-12-09
tags: [devlog, kap, android, wear-os]
---

[Made a Wear OS module.](https://github.com/stcksmsh/Kap/commit/80407b6e4d0dc05efede2c83d2ebbaf7fc8770d4) It does not talk to the phone yet. [Set up a `WearableListenerService`](https://github.com/stcksmsh/Kap/commit/1979d1c12e2b968fefbe3e24353cceeabc06812a) and a message client on the phone side to push intake updates over — `Wearable.getNodeClient(...).connectedNodes`, straightforward on paper — but the actual round trip never worked in this pass. Leaving the plumbing in since the shape is probably right, just didn't get far enough to prove it.

Also, unrelated but landed in the same window: the whole app is translatable now (English + Serbian Latin), and the widget click opens the app directly instead of going through the animation first.
