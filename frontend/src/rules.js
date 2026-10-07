export const riyadhDate = value => new Date(value.replace(' ', 'T') + (/[zZ]|[+-]\d\d:\d\d$/.test(value) ? '' : '+03:00'));
export const isFuture = value => value && riyadhDate(value).getTime() > Date.now();
export const can = {
 requestOffer: (user, offer) => !!user?.emailVerified && user.accountId !== offer.providerAccountId && offer.status === 'ACTIVE' && user.tokenBalance >= offer.tokenCost,
 respond: (user, req) => !!user?.emailVerified && req.status === 'OPEN' && !req.neededBy && [req.requesterAccountId, req.providerAccountId].includes(user.accountId),
 proposeDate: (user, req) => user.accountId === req.providerAccountId || req.negotiationHistory?.some(n => n.senderAccountId === req.providerAccountId && n.proposedDate),
 acceptProposal: (user, req, n) => !!user?.emailVerified && [req.requesterAccountId,req.providerAccountId].includes(user.accountId) && req.status === 'OPEN' && !req.neededBy && n.senderAccountId !== user.accountId && isFuture(n.proposedDate),
 createExchange: (user, req) => user.accountId === req.requesterAccountId && req.status === 'OPEN' && isFuture(req.neededBy),
 acceptExchange: (user, ex) => !!user?.emailVerified && user.accountId === ex.providerAccountId && ex.status === 'PENDING' && isFuture(ex.agreedDate),
 cancelExchange: (user, ex) => [ex.requesterAccountId,ex.providerAccountId].includes(user.accountId) && ['PENDING','ACCEPTED'].includes(ex.status),
 completeExchange: (user, ex) => user.accountId === ex.requesterAccountId && ['ACCEPTED','IN_PROGRESS'].includes(ex.status),
 review: (user, ex) => ex.status === 'COMPLETED' && [ex.requesterAccountId,ex.providerAccountId].includes(user.accountId),
 redeem: (balance, amount) => Number.isInteger(amount) && amount >= 5 && balance - amount >= 3,
};
export const negotiationDate = value => value ? value.replace('T',' ').slice(0,16) : null;
export const sessionDate = value => value ? value.replace(' ','T').slice(0,19) : null;
export const safeUrl = value => { try { const url = new URL(value); return ['http:','https:'].includes(url.protocol) ? url.href : null; } catch { return null; } };
