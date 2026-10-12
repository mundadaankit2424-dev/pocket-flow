# PocketFlow

A free, offline Android personal cash-flow tracker. No ads, subscriptions, paid APIs, account, analytics SDK, cloud sync, or bank login. Money and transaction data stay in app-private storage on the device.

## MVP features

- Enter monthly salary and recurring fixed costs such as rent.
- Record daily expenses and choose a category.
- See this month's salary, fixed costs, confirmed spending, and remaining balance.
- Optionally detect transaction-like Android notifications, then review, categorize, confirm, or ignore them.
- Remove entries from recent activity.

## Try the app

Download the latest APK from the [Releases page](https://github.com/mundadaankit2424-dev/pocket-flow/releases/latest). On Android, open the downloaded APK and allow installation from your browser or file manager if prompted. This is a debug build for personal testing; it is not listed on Google Play.

For a new build, use the **Build PocketFlow APK** workflow in the Actions tab. Each run publishes an APK artifact. A version tag such as `v0.1.0` publishes a stable APK under Releases.

## Build locally

Open this folder in Android Studio and run the app on your Android phone. The app does not require a server or paid service. Enable notification access from the button in the app only if you want automatic capture.

## Limits

Bank and UPI notification wording differs. Detection is heuristic and may miss an alert, extract the wrong amount, or suggest the wrong merchant/category, so review each captured alert. The app does not read bank SMS, connect to accounts, or provide banking advice. Data is local to this installation; uninstalling the app may erase it. Back up your device if you need to preserve it.
