package se.vgregion.kivtools.search.svc.ws.vardval;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;
/**
 * Factory for generating an IVårdvalService to be used in spring context.
 * @author David Bennehult, Jonas Liljenfeld och Joakim Olsson.
 *
 */
public final class VardvalServiceFactory {

  public static IVårdvalService getIVardvalservice() {
    return new VårdvalService().getBasicHttpBindingIVårdvalService();
  }
}
