/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ws;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBElement;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.impl.kiv.DeliverypointService;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeliveryPoint;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.DeliveryPoint;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;

/**
 * @author attra
 *
 */
enum KivwsDeliverypointAttributes {
	cn("cn"), hsaidentity("hsaidentity"), hsasedfdeliveryaddress(
			"hsasedfdeliveryaddress"), hsaconsigneeaddress(
			"hsaconsigneeaddress"), vgreancode("vgreancode"), vgrorgrel("vgrorgrel");

	private KivwsDeliverypointAttributes(String value) {
		this.value = value;
	}

	private String value;

	@Override
	public String toString() {
		return this.value;
	}
	
	public static boolean contains(String test) {

	    for (KivwsDeliverypointAttributes c : KivwsDeliverypointAttributes.values()) {
	        if (c.toString().equalsIgnoreCase(test)) {
	            return true;
	        }
	    }

	    return false;
	}
}

public class KivwsDeliverypointService implements DeliverypointService {

	private final VGRegionWebServiceImplPortType vgregionWebService;
	private static final Log LOG = LogFactory.getLog(KivwsDeliverypointService.class);

	/**
	 * Constructs a new KivwsDeliveryPointService.
	 * 
	 * @param vgregionWebService
	 *            The VGR web service to use.
	 */
	public KivwsDeliverypointService(
			VGRegionWebServiceImplPortType vgregionWebService) {
		this.vgregionWebService = vgregionWebService;
	}


	/* (non-Javadoc)
	 * @see se.vgregion.kivtools.search.svc.impl.kiv.DeliveryPointService#findDeliveryPointById(java.lang.String)
	 */
	@Override
	public Deliverypoint findDeliveryPointById(String hsaId) throws KivException {
		Deliverypoint retval = null;
		List<Deliverypoint> result = this.searchDeliveryPoint("(hsaIdentity=" + hsaId + ")");
		if(!result.isEmpty() && result.size()>0) {
			retval = result.get(0);
		}
		return retval;
	}


	@Override
	public List<Deliverypoint> searchDeliveryPointsForUnit(
			Unit unit) throws KivException {
		String unitHsaIdentity = unit.getHsaIdentity();
		return this.searchDeliveryPointsForUnit(unitHsaIdentity);
	}


	@Override
	public List<Deliverypoint> searchDeliveryPointsForUnit(
			String unitHsaid) throws KivException {
		List<Deliverypoint> result = null;
		result = this.searchDeliveryPoint("(vgrOrgRel=" + unitHsaid + ")");
		return result;
	}
	
	private List<Deliverypoint> searchDeliveryPoint(String filter) throws KivException{
		List<Deliverypoint> retval = new ArrayList<Deliverypoint>();
		Deliverypoint deliverypointObject;
		try {
			ArrayOfDeliveryPoint searchDeliveryPoints = this.vgregionWebService.searchDeliveryPoint(filter, null);
			List<DeliveryPoint> deliveryPointsFromWS = searchDeliveryPoints.getDeliveryPoint();
			for(DeliveryPoint dp : deliveryPointsFromWS) {
				deliverypointObject = null;
				JAXBElement<String2ArrayOfAnyTypeMap> jaxbElemTmp=dp.getAttributes();
				String2ArrayOfAnyTypeMap elements = jaxbElemTmp.getValue();
				deliverypointObject = createDomainObjectDeliverypoint(elements);
				if(LOG.isDebugEnabled()){
					LOG.debug(deliverypointObject.toString());
				}
				retval.add(deliverypointObject);
			}
			
		} catch (VGRException_Exception e) {
			e.printStackTrace();
			throw new KivException(e.getMessage());
		}
		return retval;		
	}
	
	private Deliverypoint createDomainObjectDeliverypoint(String2ArrayOfAnyTypeMap map){
		Deliverypoint point = new Deliverypoint();
		List<Object> value;
		String key;
		for(Entry entry : map.getEntry()) {
			key=null;
			value=null;

			key = entry.getKey();
			value = entry.getValue().getAnyType();

			if(KivwsDeliverypointAttributes.contains(key)){
				KivwsDeliverypointAttributes attributename = KivwsDeliverypointAttributes.valueOf(key.toLowerCase());
				switch (attributename) {
				case cn:
					point.setCn((String) value.get(0));
					break;
				case hsaidentity:
					point.setHsaIdentity((String) value.get(0));
					break;
				case hsaconsigneeaddress:
					Address address1 = AddressHelper.convertToAddress((String) value.get(0));
					point.setHsaConsigneeAddress(address1);
					break;
				case hsasedfdeliveryaddress:
					Address address2 = AddressHelper.convertToAddress((String) value.get(0));
					point.setHsaSedfDeliveryAddress(address2);
					break;
				case vgreancode:
					point.setVgrEanCode((String)value.get(0));
					break;
				case vgrorgrel:
					point.setVgrOrgRel(fetchVgrOrgRel(value));
					break;
				}
			}
		}
		return point;
	}

	private static List<String> fetchVgrOrgRel(List<Object> values){
		List <String> retval = new ArrayList<String>(values.size());
        for (Object object : values) {
        	String tmp = (String) object;
       	retval.add(tmp);
        }
        return retval;
 	}
}
