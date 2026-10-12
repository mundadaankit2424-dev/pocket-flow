# PocketFlow Web MVP

This is a self-contained browser preview of the PocketFlow cash-flow tracker. It runs without a server, account, paid API, or build step. It stores the demo entries only in the current browser's local storage.

## Publish a test link with GitHub Pages

1. Extract this ZIP file on your computer.
2. In your `pocket-flow` GitHub repository, choose **Add file → Upload files**.
3. Upload `index.html` and this `README.md` to the repository root, then commit the changes to `main`.
4. In the repository, open **Settings → Pages**.
5. Under **Build and deployment**, choose **Deploy from a branch**, select `main` and `/(root)`, then save.
6. When GitHub Pages finishes publishing, open: https://mundadaankit2424-dev.github.io/pocket-flow/

GitHub says project sites use the `USERNAME.github.io/REPOSITORY` address. Publishing can take a few minutes after the first setup.

## Try it

Use **Salary**, **Fixed cost**, and **Expense** to change the sample summary. **Simulate bank alert** adds an item to the review list; use **Confirm** or **Ignore**. Refreshing the same browser keeps its entries. **Reset** browser site data to return to the sample state.

This is a web mockup for reviewing the screens and flow. Browsers cannot read Android banking notifications, so the transaction button is simulated. Real notification capture belongs in the native Android app.
