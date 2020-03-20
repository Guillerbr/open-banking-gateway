package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.token.model.generated.PsuConsentSessionResponse;
import de.adorsys.opba.tppbankingapi.token.resource.generated.TppTokenApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TppBankingApiTokenController implements TppTokenApi {
    @Override
    public ResponseEntity<PsuConsentSessionResponse> code2TokenGET(String authorization, UUID xRequestID, String redirectCode) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
