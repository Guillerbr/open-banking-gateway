package de.adorsys.opba.protocol.xs2a.entrypoint.authorization;

import de.adorsys.opba.protocol.api.authorization.FromAspspRedirect;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.service.ContextUpdateService;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("xs2aFromAspspRedirect")
@RequiredArgsConstructor
public class Xs2aFromAspspRedirect implements FromAspspRedirect {

    private final AuthorizationContinuationService continuationService;
    private final ContextUpdateService ctxUpdater;
    private final ExtendWithServiceContext extender;

    @Override
    public CompletableFuture<Result<UpdateAuthBody>> execute(ServiceContext<FromAspspRequest> serviceContext) {
        String executionId = serviceContext.getAuthContext();

        ctxUpdater.updateContext(
            executionId,
            (Xs2aContext toUpdate) -> {
                toUpdate.setRedirectConsentOk(serviceContext.getRequest().getIsOk());
                extender.extend(toUpdate, serviceContext);
                return toUpdate;
            }
        );

        return continuationService.handleAuthorizationProcessContinuation(executionId);
    }
}
