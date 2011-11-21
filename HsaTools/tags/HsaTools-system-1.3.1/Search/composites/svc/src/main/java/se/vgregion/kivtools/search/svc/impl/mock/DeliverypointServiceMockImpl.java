package se.vgregion.kivtools.search.svc.impl.mock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.impl.kiv.DeliverypointService;

public class DeliverypointServiceMockImpl implements DeliverypointService {

	@Override
	public List<Deliverypoint> searchDeliveryPointsForUnit(Unit unit)
			throws KivException {
		return  searchDeliveryPointsForUnit(unit.getHsaIdentity());
	}

	@Override
	public List<Deliverypoint> searchDeliveryPointsForUnit(String unitHsaid)
			throws KivException {
		return createDeliverypoints(null, unitHsaid);
	}

	@Override
	public Deliverypoint findDeliveryPointById(String hsaId)
			throws KivException {
		return create0(hsaId,null).get(0);
	}

	private List<Deliverypoint> createDeliverypoints(String id, String unitID){
		List<Deliverypoint> retval=null;
		String str = "1";
		if(id != null && !id.isEmpty()){
			str = id.substring(id.length()-1);
		}
		else {
			if(unitID != null && !unitID.isEmpty()) {
				System.out.println("unitID = " + unitID);
				str = unitID.substring(unitID.length()-1);				
			}
		}
		int nr=0;
		if(str.matches("\\d")){
			int value = Integer.parseInt(str);
			nr = value % 4;
		}

		switch(nr) {
		case 0:
			retval=create0(id,unitID);
		case 1:
			retval=create1(id,unitID);
		case 2:
			retval=create2(id,unitID);
		case 3:
			retval=create3(id,unitID);
		}
		return retval;
 	}
	
	
	List<Deliverypoint> create0(String id, String unitID){
		List<Deliverypoint> retval= new LinkedList<Deliverypoint>();
		return retval;
	}
	
	List<Deliverypoint> create1(String id, String unitID){
		List<Deliverypoint> retval= new LinkedList<Deliverypoint>();
		Deliverypoint dp = new Deliverypoint();
		if(id !=null){
			dp.setCn(id);
			dp.setHsaIdentity(id);
		}
		else {
			dp.setCn("SE2321000131-S000000000585");
			dp.setHsaIdentity("SE2321000131-S000000000585");
		}
		Address address = AddressHelper.convertToAddress("Godsmottagningen SHÖ$Östra Sjukhuset$Smörslottsgatan 1$ $416 85$Göteborg");
		dp.setHsaSedfDeliveryAddress(address);
		dp.setVgrEanCode("7332784048347");
		LinkedList<String> vgrorgrel = new LinkedList<String>();
		if(unitID != null) {
			vgrorgrel.add(unitID);
		}
		vgrorgrel.add("SE2321000131-E000000000928");
		vgrorgrel.add("SE2321000131-E000000000110");
		dp.setVgrOrgRel(vgrorgrel);
		retval.add(dp);
		return retval;
	}
	
	List<Deliverypoint> create2(String id, String unitID){
		List<Deliverypoint> retval= new LinkedList<Deliverypoint>();
		List<Deliverypoint> create1 = create1(null, unitID);
		retval.addAll(create1);
		
		Deliverypoint dp = new Deliverypoint();
		if(id !=null){
			dp.setCn(id);
			dp.setHsaIdentity(id);
		}
		else {
			dp.setCn("SE2321000131-S000000000562");
			dp.setHsaIdentity("SE2321000131-S000000000562");
		}
		Address address = AddressHelper.convertToAddress("$ $Per Dubbsgatan 14, vån 4$ $413 45$Göteborg");
		dp.setHsaSedfDeliveryAddress(address);
		dp.setVgrEanCode("7332784047609");
		LinkedList<String> vgrorgrel = new LinkedList<String>();
		if(unitID != null) {
			vgrorgrel.add(unitID);
		}
		vgrorgrel.add("SE2321000131-E000000006451");
		dp.setVgrOrgRel(vgrorgrel);
		retval.add(dp);
		return retval;
	}
	
	List<Deliverypoint> create3(String id, String unitID){
		List<Deliverypoint> retval= new LinkedList<Deliverypoint>();
		List<Deliverypoint> create2 = create2(null, unitID);
		retval.addAll(create2);

		Deliverypoint dp = new Deliverypoint();
		if(id !=null){
			dp.setCn(id);
			dp.setHsaIdentity(id);
		}
		else {
			dp.setCn("SE2321000131-S000000000507");
			dp.setHsaIdentity("SE2321000131-S000000000507");
		}
		Address address = AddressHelper.convertToAddress("Alingsås lasarett$Akutmottagningen$Södra Ringgatan 30$ $441 83$Alingsås");
		dp.setHsaSedfDeliveryAddress(address);
		dp.setVgrEanCode("7332784047050");
		LinkedList<String> vgrorgrel = new LinkedList<String>();
		if(unitID != null) {
			vgrorgrel.add(unitID);
		}
		vgrorgrel.add("SE2321000131-E000000006335");
		vgrorgrel.add("SE2321000131-F000000000455");
		dp.setVgrOrgRel(vgrorgrel);
		retval.add(dp);
		
		return retval;
	}
}
