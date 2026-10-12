# PocketFlow

Free, offline Android personal cash-flow tracker. No ads, subscription, paid API, account, analytics SDK, cloud sync, or bank login. Money and transaction data stay in app-private storage on the device.

## Features

- Enter monthly salary and recurring fixed costs such as rent.
- Record daily expenses and choose a category.
- See this month's salary, fixed costs, confirmed spending, and remaining balance.
- Optionally enable Android notification access. Likely transaction alerts are held for review; choose a category and confirm, or ignore them. OTP-like alerts are skipped.
- Remove an entry from recent activity.

## Build and install

Open this folder in Android Studio and run the app on your Android phone. Android Studio and the Android SDK are available from Google's Android developer site. The app does not require a server or paid service. Enable notification access from the button in the app only if you want automatic capture.

This project currently contains source code, not a prebuilt APK. To install it without publishing, build a debug APK in Android Studio and install it on your own phone. Publishing on Google Play may require a paid developer account; direct personal installation avoids that distribution fee.

## Limits

Bank and UPI notification wording differs. Detection is heuristic and may miss an alert, extract the wrong amount, or suggest the wrong merchant/category, so review each captured alert. This app does not read bank SMS, connect to accounts, or provide banking advice. Data is local to this installation; uninstalling the app may erase it. Back up your device if you need to preserve it.
