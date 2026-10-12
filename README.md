# PocketFlow Web MVP

Self-contained browser preview of the PocketFlow cash-flow tracker. No build step, backend, account, paid API, or external library is needed. Demo data is saved in local storage in your browser.

## Publish with GitHub Pages

1. Extract the ZIP on your computer.
2. In your `pocket-flow` repository, choose **Add file → Upload files**.
3. Upload `index.html` and this `README.md` to the repository root; commit to `main`.
4. Open **Settings → Pages**. Under **Build and deployment**, select **Deploy from a branch**, choose `main` and `/(root)`, then save.
5. After publishing, open: https://mundadaankit2424-dev.github.io/pocket-flow/

GitHub project pages use the `USERNAME.github.io/REPOSITORY` address, and first publication may take a few minutes. See [GitHub Pages quickstart](https://docs.github.com/en/pages/quickstart?library=true).

## Preview features

- **Home:** salary, monthly planned costs, confirmed daily spending, and remaining balance.
- **Activity:** confirm or ignore simulated bank transaction alerts; add manual expenses.
- **Plan:** add monthly recurring costs, set a due day, and see the total planned for the month. Remove costs with ×.
- **Spending calculator:** enter a possible purchase and a savings amount to see the projected balance and average safe amount per day for the rest of the month.
- **Calculator:** use a separate everyday calculator for addition, subtraction, multiplication, division, percentages and sign changes.
- **Settings:** change your display name, salary, day-to-day budget, and currency. Reset demo data stored by this browser.

This is a web preview. Transaction alerts are simulated; only the native Android app can request Android notification access. The web preview does not read SMS or Android notifications.


