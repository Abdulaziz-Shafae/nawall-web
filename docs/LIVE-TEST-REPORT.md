# Live website checks — 7 October 2026

Target: https://nawall.up.railway.app/

## Passed
- Frontend and backend health endpoints return HTTP 200.
- Public skills and available-offers endpoints return real JSON; both currently contain empty lists.
- Guest profile and admin account requests return HTTP 401.
- Authorized, clearly labeled Nawall QA Test account registration and login work. Reload preserves the session.
- Profile editing saves successfully.
- Selecting a non-personal PNG fixture uploads through the real media endpoint, saves to the profile, and displays the returned image successfully.
- Wallet loads the initial three tokens and empty history; redemption is correctly unavailable at that balance.
- Mobile menu navigation works. Live English dashboard/AI and Arabic company registration fit a 390px viewport without horizontal overflow.
- Updated local landing page fits a 320px viewport without horizontal overflow.
- Frontend build, 15 unit tests, and API contract checks pass after the mobile changes.

## Mobile changes
Decorative Unicode symbols and rating stars now use SVG icons, preventing iOS emoji substitution. Small-screen forms use 16px text, key controls have 44px minimum heights, dialogs use dynamic viewport height, and long text can wrap. Arabic image-selector labels now describe file selection.

## Limits
Viewport checks used Chromium, not a physical iPhone or Safari. No verification emails, WhatsApp messages, Zoom meetings, or AI provider calls were sent. Full exchange/session workflows require real catalog skills, offers, and appropriate verified accounts; the live catalog is currently empty. The QA account remains labeled for administrator cleanup; no password is stored in this report.
