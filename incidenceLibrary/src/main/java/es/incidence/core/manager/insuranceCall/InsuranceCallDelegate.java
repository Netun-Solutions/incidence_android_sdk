package es.incidence.core.manager.insuranceCall;

import es.incidence.core.domain.Incidence;
import es.incidence.core.manager.IResponse;

public interface InsuranceCallDelegate {
    void onLocationErrorResult();
    void onBadResponseReport(IResponse response);
    void onSuccessReport(Incidence incidence);
    void onSuccessReportToCall(Incidence incidence);
}
