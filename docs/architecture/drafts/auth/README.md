# Consent confirmation auth approach

## Context

This is consent authorization flow.

## Preface
Currently, session fixation attack is dealt after consent is created and authorized with ASPSP by

1. validating by FinTech that redirectCode matches redirectCookie
1. If match, FinTech activating consent in OpenBanking

![](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/feature/alt-auth/docs/architecture/drafts/auth/opba-auth-now.puml&fmt=svg&vvv=2&sanitize=true)

This has drawback as user actually authorizes his consent and only after it is created and valid we
prevent the attack on our side.

## Alternative (kind of Oauth)
Instead of validating user identity at the end by FinTech, we can do it at the start.

It is done by:
1. Moving user to OpenBanking where he is granted cookie - loginCookie.
1. Moving user back to FinTech where match of redirectCode with redirectCookie is checked
1. If match, FinTech activates loginCookie and moves user back to OpenBanking

Here, session fixation is impossible as OpenBanking requires user to provide active loginCookie.

![](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/feature/alt-auth/docs/architecture/drafts/auth/opba-auth-proposed.puml&fmt=svg&vvv=2&sanitize=true)

Pros:
Consent is created by the user who is authorized by FinTech. Fixation is not possible due to cookie required
on consent authorization

Cons:
More redirects (they can be done almost seamlessly)