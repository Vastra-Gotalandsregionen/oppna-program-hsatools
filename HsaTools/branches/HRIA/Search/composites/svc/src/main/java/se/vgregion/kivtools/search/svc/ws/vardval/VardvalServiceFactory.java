package se.vgregion.kivtools.search.svc.ws.vardval;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;

public final class VardvalServiceFactory {

  public static IVårdvalService getIVardvalservice() {
    return new VårdvalService().getBasicHttpBindingIVårdvalService();
  }
}
