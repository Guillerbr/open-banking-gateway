# Consent confirmation auth approach

## Context

This is consent authorization flow.

## Preface
Currently, session fixation attack is dealt after consent is created and authorized with ASPSP by

1. validating by FinTech that redirectCode matches redirectCookie
1. If match, FinTech activating consent in OpenBanking

<!--
![](https://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/feature/alt-auth/docs/architecture/drafts/auth/opba-auth-now.puml&fmt=svg&vvv=2&sanitize=true)
-->
![](http://www.plantuml.com/plantuml/png/XL5DImCn4BtlhtX7FJHY_H1AMQe5zQ0e_V3KosmosB3hH9Ai1t-zsRhi1Yfu2MGox-EzB48a7zkFfY0TdSVhUblSMzcmtcCoARixPZXNk6tZtddxpT1E0al4TlMaTh9EACQyAZqRwrd7fNErvVbPLOB4eBBfY0uzQOVQoOFhHY-VB7SajPNt8kbqUlaiwKuLbfuf3g95_ZxOkqXhlIbzM3SAgzuEAj9rPk1NgHpyp72f3ghmEQ9FzyYUOSFV-SQYShehXJTghA78_y2EubuhF9ElXuOfW7IqNuoJaSRmTEDgbjEyYsEkcx6JDb3LSB5WCUcZVm00)

This has drawback as user actually authorizes his consent and only after it is created and valid we
prevent the attack on our side.

## Alternative (kind of Oauth)
Instead of validating user identity at the end by FinTech, we can do it at the start.

It is done by:
1. Moving user to OpenBanking where he is granted cookie - loginCookie.
1. Moving user back to FinTech where match of redirectCode with redirectCookie is checked
1. If match, FinTech activates loginCookie and moves user back to OpenBanking

Here, session fixation is impossible as OpenBanking requires user to provide active loginCookie.
<!--
![](https://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/open-banking-gateway/feature/alt-auth/docs/architecture/drafts/auth/opba-auth-proposed.puml&fmt=svg&vvv=2&sanitize=true)
-->
![](http://www.plantuml.com/plantuml/png/ZLDTIyCm57tlhxXlAcpHRlF3CSjqe0m4nJcVzfAcr_MoBf4aK_3NUrkipMm5Nme9ztpSSzAfyzBwtRQ8fFB6mkDY6TsIVaQLGx-Vb6SOGopWUkTpO-aJGHdjK7jOFjsBbLum2c52YSMCB2e_CsP3E3cc1AJE82N-U0CrAQDM-iwKLmzlg6-atf1UHwnJwGLth3iGCBCeFGkaIM7Mf6lsD3ciplCChY0U34T7eLf8Kof6uRbPQoY0dQHIRKA77-Hps2V_iLVFZ64aO7wmhS2SmeiiA6EVtK4qYROsFELabQTtnlxMRDTm9k2_oPqtzdgGeqKWrn5Y64m7ouM0PhCMk4gSVCaionrmdwrsVLcNMB-Sxh7uXEU-dvQgvhCUh5lrx85RXkEu8TavcHPOvHCsn-66sHZ6WqFdo6XujMRxHuLJr1d_9by0)

Pros:
Consent is created by the user who is authorized by FinTech. Fixation is not possible due to cookie required
on consent authorization

Cons:
More redirects (they can be done almost seamlessly)