# PocketFlow

PocketFlow is a free personal Android cash-flow tracker. There are no ads, subscriptions, paid APIs, accounts, analytics SDKs, cloud sync, or bank login. Salary, monthly costs, and spending entries are stored on this phone.

## Features

- Set monthly salary and recurring costs such as rent and bills.
- Track daily expenses, use the built-in spending calculator and regular calculator.
- Optionally capture likely debit alerts from bank apps and SMS alerts displayed by your Messages app.
- Review each detected alert before it changes your tracked spending. OTP and obvious incoming credit/refund alerts are skipped.

## Notification access and SMS

To capture new bank alerts, open PocketFlow Settings, tap Enable, then enable PocketFlow under Android's Notification access settings. Android sends the app notification content from apps you allow through this system setting. SMS alerts can be captured when your Messages app displays them as notifications.

PocketFlow does not request permission to read the SMS inbox and does not import old messages. It does not save the full notification text; it keeps a suggested amount, merchant text, category, and date on this device for your review. Detection depends on the wording used by your bank and may miss an alert or suggest the wrong amount/category. Always review before confirming.

## Build and install from GitHub

1. Extract this ZIP on your computer.
2. Upload the contents inside the extracted PocketFlow folder to the root of your GitHub repository. Keep folder paths intact. In particular, confirm that .github/workflows/build-apk.yml exists in the repository; GitHub may hide .github in some upload screens.
3. On GitHub, open Actions and select Build PocketFlow APK. The workflow starts after the files are pushed to the main branch; you can also run it with Run workflow.
4. When the workflow succeeds, open the repository's Releases page and download app-debug.apk from PocketFlow MVP.
5. Open the APK on your Android phone and allow installation when Android asks.

The workflow also attaches the APK to the run under Artifacts. This project contains source files, not a prebuilt APK. GitHub Actions builds the installable APK using free hosted tooling; Android Studio can also open this folder and build it locally.

## Limits

This is a personal-use MVP, not a bank-connected product or financial advice. Data stays on this installation and may be erased if the app is uninstalled. Android's notification access is optional and can be turned off in system settings.
