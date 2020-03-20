package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.repositories.RedirectUrlRepository;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSession;
import de.adorsys.opba.tpp.token.api.model.generated.PsuConsentSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static java.util.Collections.singletonList;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("fintech-ui")
public class RedirectHandlerService {
    private static final String LOCATION_HEADER = "Location";
    @Value("${mock.tppais.listaccounts:false}")
    private String mockTppAisString;
    private String redirectUrl;
    private String exceptionUrl;

    private final RedirectUrlRepository redirectUrlRepository;
    private final AuthorizeService authorizeService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final RequestInfoService requestInfoService;
    private final TokenService tokenService;

    public RedirectUrlsEntity registerRedirectUrlForSession(String xsrfToken, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        String redirectCode = UUID.randomUUID().toString();
        log.debug("ONLY FOR DEBUG: redirectCode: {}", redirectCode);

        String okUrl = fintechRedirectURLOK.replaceAll("^/", "");
        String notOkUrl = fintechRedirectURLNOK.replaceAll("^/", "");

        RedirectUrlsEntity redirectUrls = new RedirectUrlsEntity();

        redirectUrls.setRedirectCode(redirectCode);
        redirectUrls.setRedirectState(xsrfToken);
        redirectUrls.setOkURL(getModifiedUrlWithRedirectCode(okUrl, redirectCode));
        redirectUrls.setNotOkURL(getModifiedUrlWithRedirectCode(notOkUrl, redirectCode));

        return redirectUrlRepository.save(redirectUrls);
    }

    public ResponseEntity doRedirect(String redirectState, String redirectId, String redirectCode) {
        if (StringUtils.isBlank(redirectCode)) {
            log.warn("Validation redirect request was failed: redirect code is empty!");
            return prepareErrorRedirectResponse(exceptionUrl);
        }

        RedirectUrlsEntity redirectUrls = redirectUrlRepository.findByRedirectCode(redirectCode)
                                                  .orElseThrow(() -> new IllegalStateException("Validation redirect request was failed: redirect code is wrong!"));

        if (StringUtils.isBlank(redirectState)) {
            log.warn("Validation redirect request was failed: Xsrf Token is empty!");
            return prepareErrorRedirectResponse(redirectUrls.getNotOkURL());
        }

        if (!authorizeService.isAuthorized(redirectState, null)) {
            log.warn("Validation redirect request was failed: Xsrf Token is wrong or user are not authorized!");
            return prepareErrorRedirectResponse(redirectUrls.getNotOkURL());
        }

        ContextInformation contextInformation = new ContextInformation(UUID.randomUUID());

        PsuConsentSessionResponse response = tokenService.code2Token(contextInformation, redirectCode);
        PsuConsentSession psuConsentSession = response.getPsuConsentSession();
        log.info(psuConsentSession.toString());

        RequestInfoEntity info = requestInfoService.getRequestInfoByXsrfToken(redirectState);
        SessionEntity sessionEntity = authorizeService.getByXsrfToken(redirectState);

        return handleDirectionForRedirect(contextInformation, sessionEntity, redirectUrls, info);
    }

    private ResponseEntity handleDirectionForRedirect(ContextInformation contextInformation,
                                                      SessionEntity sessionEntity,
                                                      RedirectUrlsEntity redirectUrls,
                                                      RequestInfoEntity info) {
        switch (info.getRequestAction()) {
            case LIST_ACCOUNTS:
                return accountService.listAccounts(contextInformation, sessionEntity, redirectUrls, info);
            case LIST_TRANSACTIONS:
                return transactionService.listTransactions(contextInformation, sessionEntity, redirectUrls, info);
            default:
                throw new RuntimeException("DID NOT EXPECT REQUEST ACTION:" + info.getRequestAction());
        }
    }

    private ResponseEntity prepareErrorRedirectResponse(String redirectUrl) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(LOCATION_HEADER, singletonList(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    private String getModifiedUrlWithRedirectCode(String... params) {
        return UriComponentsBuilder.fromHttpUrl(redirectUrl)
                       .buildAndExpand(params)
                       .toUriString();
    }
}
