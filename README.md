# PocketFlow Android launcher icon update

Copy the files into the matching locations in your Android project. These are native Android adaptive launcher icons for Android 8.0 and later.

- Replace `app/src/main/AndroidManifest.xml` with the included manifest.
- Copy `app/src/main/res/drawable/ic_pocketflow_foreground.xml`.
- Copy both files in `app/src/main/res/mipmap-anydpi-v26/`.
- Copy `app/src/main/res/values/colors.xml` (merge the color entry if that file already exists).
- `pocketflow-app-icon.svg` is a standalone preview of the mark.

The manifest points the launcher at the new adaptive icon. Build and reinstall the app to see it on your phone.
