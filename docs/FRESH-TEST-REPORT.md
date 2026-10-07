# Fresh website test — 7 October 2026

Correct target: https://nawell.up.railway.app/ (the previous nawall domain returns Railway 404).

## Fresh checks
- Production build and all 15 frontend tests pass. All 135 client method/path contracts match the 127 backend endpoints.
- Frontend `/healthz` and proxied `/api/v1/health` return 200; public catalog and available offers return real, empty JSON lists.
- Guest profile and account administration endpoints return 401.
- Newly registered Nawell Fresh QA Test account succeeds. Duplicate phone is rejected with the backend message. Incorrect password is rejected; correct login succeeds.
- Session survives refresh. Profile bio saves, and a file-selected PNG uploads and displays with its actual 32px image dimensions.
- Dashboard, skills, offers, requests, exchanges, sessions, wallet, and AI studio load. Empty states and unavailable offer/redemption controls match the account's data. Wallet has three initial tokens and zero reserved tokens.
- Logout succeeds using keyboard activation. Automated pointer interaction with the logout button returned a browser targeting error; this does not establish a physical-device pointer failure.
- Browser error/warning log is empty at the checked point.
- Arabic company registration fits 320px, uses RTL, includes a real logo file selector, and form inputs use 16px text.
- Live decorative icons are SVGs. Physical iPhone/Safari testing is unavailable.

## Defects found and corrected
The 320px Arabic account layout exposed intrinsic grid sizing overflow. The mobile grid now uses `minmax(0, 1fr)` and wallet controls can wrap. A second overflow source was the account header: compact branding and spacing now keep its controls within small phone widths. Both fixes are published to GitHub.

Final deployment verified: the live site serves `index-DaZbYc2j.css`. After signing in again, the Arabic wallet at 320px reports document scroll width 305px (remaining width is the scrollbar), with the menu at x=10px and working. The backend deployment cleared its in-memory session; Retry redirected to login and signing in recovered successfully.

## Boundaries
No emails or external provider calls were made. The catalog is empty and the fresh account is unverified, so assessments, offer creation, negotiations, completed exchanges, reviews, live AI responses, WhatsApp, and Zoom have not passed full live workflow testing. No existing user records were edited. The clearly labeled QA account remains for administrator cleanup.
