#!/usr/bin/env bash

echo "Validating DEV deployment"

COMMIT="cbf916766119f98b672c3834c64a9da289a80944"
OPBA_URL=https://obg-dev-openbankinggateway.cloud.adorsys.de

echo "Waiting for deployment"
# Wait for deployment to happen
timeout 600s bash -c 'while [[ $(wget -qO- '$OPBA_URL'/actuator/info | grep -c '$COMMIT') != 1 ]]; do echo "Wait for actuator status to return desired commit"; sleep 10; done' || (echo "Failed waiting for deploy $COMMIT";exit 1)

echo "Run smoke tests"
# Run smoke tests:
export ENABLE_SMOKE_TESTS=true
echo "Run API smoke tests"
./mvnw test -DfailIfNoTests=false -Dtest=de.adorsys.opba.smoketests.OpbaApiSmokeE2ETest
echo "Run Consent UI and API smoke tests"
./mvnw test -DfailIfNoTests=false -Dtest=de.adorsys.opba.smoketests.OpbaApiWithConsentUiSmokeE2ETest