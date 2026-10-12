# PocketFlow

PocketFlow is a free, offline Android cash-flow tracker for personal use. It has no ads, subscriptions, paid APIs, account, analytics SDK, cloud sync, or bank login. Financial entries are stored locally on the phone.

## Upload this project to GitHub

1. Download and extract this ZIP.
2. In your `pocket-flow` repository, choose **Add file → Upload files**.
3. Upload the extracted project's contents to the repository root, keeping the folders in place. The root should contain `app`, `.github`, `build.gradle.kts`, and `settings.gradle.kts`.
4. Commit the upload. In particular, check that GitHub shows `.github/workflows/build-apk.yml` at exactly that path. If the upload page skips the hidden `.github` folder, create or replace that file in GitHub at that exact path using the included workflow file.
5. Open **Actions → Build PocketFlow APK** and wait for the run to finish successfully.
6. When the build is green, install the APK from the repository's **Releases → PocketFlow MVP** page. Android may ask you to allow installs from your browser or file manager.

The ZIP itself is source code and cannot be installed on Android. The generated `.apk` is the installable app.

## Features

- Add monthly salary and fixed recurring expenses such as rent.
- Record daily expenses and choose a category.
- See this month's salary, fixed costs, spending, and remaining balance.
- Optionally detect transaction-like Android notifications, then review, categorize, confirm, or ignore them.
- Use the built-in expense calculator.

## Limitations

Bank and UPI notification wording differs. Detection may miss alerts or suggest an incorrect amount, merchant, or category; review captured alerts. The app reads Android notifications only after notification access is enabled. It does not read bank SMS, connect to bank accounts, or provide banking advice. Data stays on the device and may be removed if the app is uninstalled.
