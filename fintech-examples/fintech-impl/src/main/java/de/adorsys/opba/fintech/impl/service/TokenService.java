package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.tppclients.TppTokenClient;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final TppTokenClient tppTokenClient;

    public PsuConsentSessionResponse code2Token(ContextInformation contextInformation, String redirectCode) {
        return tppTokenClient.code2TokenGET(contextInformation.getFintechID(), contextInformation.getXRequestID(), redirectCode).getBody();
    }
}
