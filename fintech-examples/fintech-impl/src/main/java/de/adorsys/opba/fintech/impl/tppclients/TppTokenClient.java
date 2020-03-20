package de.adorsys.opba.fintech.impl.tppclients;

import de.adorsys.opba.tpp.token.api.resource.generated.TppTokenApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${tpp.url}", name = "tppTokenClient")
public interface TppTokenClient extends TppTokenApi {
}
