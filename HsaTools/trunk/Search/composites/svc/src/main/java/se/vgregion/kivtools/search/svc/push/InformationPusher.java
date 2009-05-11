package se.vgregion.kivtools.search.svc.push;

import java.util.List;

import se.vgregion.kivtools.search.svc.domain.Unit;


public interface InformationPusher {

	List<Unit> doPushInformation() throws Exception;

}
