/**
 * 
 */
package se.vgregion.kivtools.search.domain;

import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Representation of a delivery point objectclass=vgrDeliveryPoint
 * @author attra
 *
 */
public class Deliverypoint {

	// Common Name, (same as hsaIdentity for this objectclass)
	private String cn;

	// HSA identity
	private String hsaIdentity;

	// A list of HsaIdentities to the Units
	// where this deliverypoint is connected
	// e.g. SE2321000131-E000000000101, SE2321000131-E000000005974
	private List<String> vgrOrgRel = new ArrayList<String>();

	// Only one at the time of the two address types below 
	// can exist on a delivery point object
	// Delivery address 
	private Address hsaSedfDeliveryAddress;
	// Goods address
	private Address hsaConsigneeAddress;
	
	// GLN-code - unique for every delivery point
	private String vgrEanCode;

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getHsaIdentity() {
		return hsaIdentity;
	}

	public void setHsaIdentity(String hsaIdentity) {
		this.hsaIdentity = hsaIdentity;
	}

	public Address getHsaSedfDeliveryAddress() {
		return hsaSedfDeliveryAddress;
	}

	public void setHsaSedfDeliveryAddress(Address hsaSedfDeliveryAddress) {
		this.hsaSedfDeliveryAddress = hsaSedfDeliveryAddress;
	}

	public Address getHsaConsigneeAddress() {
		return hsaConsigneeAddress;
	}

	public void setHsaConsigneeAddress(Address hsaConsigneeAddress) {
		this.hsaConsigneeAddress = hsaConsigneeAddress;
	}

	public String getVgrEanCode() {
		return vgrEanCode;
	}

	public void setVgrEanCode(String vgrEanCode) {
		this.vgrEanCode = vgrEanCode;
	}

	public List<String> getVgrOrgRel() {
		return vgrOrgRel;
	}

	public void setVgrOrgRel(List<String> vgrOrgRel) {
		this.vgrOrgRel = vgrOrgRel;
	}
	
	public boolean isEmpty() {
		boolean empty = true;
		empty &= StringUtil.isEmpty(vgrEanCode);
		empty &= StringUtil.isEmpty(cn);
		empty &= StringUtil.isEmpty(hsaIdentity);
		empty &= (hsaConsigneeAddress==null || hsaConsigneeAddress.isEmpty());
		empty &= (hsaSedfDeliveryAddress==null || hsaSedfDeliveryAddress.isEmpty());
		empty &= vgrOrgRel.isEmpty();
		return empty;
	}
	
	public String toString() {
		String result= null;
		String newline = System.getProperty("line.separator");
		StringBuilder buf = new StringBuilder();
		buf.append("Deliverypoint: { ");
		if(this.isEmpty()){
			buf.append("Uninitialized object");
		}
		else {
			if(StringUtil.isEmpty(cn)){
				buf.append("cn= ");
			}
			else {
				buf.append("cn=");
				buf.append(this.cn);
			}
			buf.append(", ");				
			if(StringUtil.isEmpty(hsaIdentity)){
				buf.append("hsaIdentity= ");				
			}
			else {
				buf.append("hsaIdentity=");
				buf.append(this.hsaIdentity);
			}
			buf.append(", ");				
			if(StringUtil.isEmpty(vgrEanCode)){
				buf.append("vgrEanCode= ");				
			}
			else {
				buf.append("vgrEanCode=");
				buf.append(this.vgrEanCode);
			}
			buf.append(", ");								
			if(hsaConsigneeAddress==null || hsaConsigneeAddress.isEmpty()){
				buf.append("hsaConsigneeAddress= ");								
			}
			else {
				buf.append("hsaConsigneeAddress=");
				buf.append(this.hsaConsigneeAddress.toString());
			}
			buf.append(", ");								
			if(hsaSedfDeliveryAddress==null || hsaSedfDeliveryAddress.isEmpty()){
				buf.append("hsaSedfDeliveryAddress= ");								
			}
			else {
				buf.append("hsaSedfDeliveryAddress=");
				buf.append(this.hsaSedfDeliveryAddress.toString());
			}
			buf.append(", ");								
			if(vgrOrgRel.isEmpty()){
				buf.append("vgrOrgRel=[]");												
			}
			else {
				buf.append("vgrOrgRel=[ ");												
				for(int i=0 ; i< (this.vgrOrgRel.size()-1); i++){
					String id = this.vgrOrgRel.get(i);
					buf.append(id);
					buf.append(", ");
				}
				buf.append(this.vgrOrgRel.get(this.vgrOrgRel.size()-1));
				buf.append(" ]");												
			}
			buf.append(" }");
			buf.append(newline);
			result=buf.toString();
		}
		return result;
	}
}
