/**
 * Copyright 2009 Västa Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
/**
 * 
 */
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import se.vgregion.kivtools.search.util.EnvAssistant;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class DN implements Serializable, Comparator<DN>, Iterable<DN> {

	private static final long serialVersionUID = 1L;
	private List<String> cn;
	private List<String> ou;
	private List<String> dc;
	private String o;
	private static int ADMINISTRATION = -3; // Position of administration
	private int position; // Used for formatting ancestors in web gui

	public DN(List<String> cn, List<String> ou, List<String> dc, String o) {
		this.cn = cn == null ? new ArrayList<String>() : cn;
		this.ou = ou == null ? new ArrayList<String>() : ou;
		this.dc = dc == null ? new ArrayList<String>() : dc;
		this.o = o == null ? "" : o;
	}

	public DN() {
		super();
	}

	/**
	 * 
	 * @param dnString
	 * @return Returns a new DN object
	 * 
	 */
	public static DN createDNFromString(String dnString) {

		List<String> cn = new ArrayList<String>();
		List<String> ou = new ArrayList<String>();
		List<String> dc = new ArrayList<String>();
		String o = "";
		dnString = dnString.replace("\\", "");
		String[] org = dnString.split(",?.?.=");
		String domain = "";
		int start = 0;
		int end = 0;

		for (int i = 0; i < org.length; i++) {
			if (org[i] != null && !org[i].equals("")) {
				end = dnString.indexOf(org[i], start) - 1;
				domain = dnString.substring(start, end);
				if (domain.equalsIgnoreCase("cn")) {
					cn.add(org[i]);
				} else if (domain.equalsIgnoreCase("ou")) {
					ou.add(org[i]);
				} else if (domain.equalsIgnoreCase("dc")) {
					dc.add(org[i]);
				} else if (domain.equalsIgnoreCase("o")) {
					o = org[i];
				}

				start = dnString.indexOf(",", org[i].length() + end) + 1;
			}
		}

		return new DN(cn, ou, dc, o);
	}

	public DN escape() {
		DN aDN = DN.createDNFromString(this.toString());

		for (int i = 0; i < aDN.cn.size(); i++) {
			aDN.cn.set(i, aDN.cn.get(i).replace(",", "\\,"));
		}

		for (int i = 0; i < aDN.ou.size(); i++) {
			aDN.ou.set(i, aDN.ou.get(i).replace(",", "\\,"));
		}

		for (int i = 0; i < aDN.dc.size(); i++) {
			aDN.dc.set(i, aDN.dc.get(i).replace(",", "\\,"));
		}

		return aDN;
	}

	public int compare(DN dn1, DN dn2) {
		return dn1.toString().compareTo(dn2.toString());
	}

	public int compareTo(DN anotherDn) {
		return this.toString().compareTo(anotherDn.toString());
	}

	public String getIsUnitAndAdministrationOnSameLevel() {
		boolean temp = (this.ou.size() <= 2); // HAGY fix, for taking care of
		// special case
		return "" + temp;
	}

	public DN getAdministration() {
		switch (this.ou.size()) {
		case 0:
		case 1:
			return null;
		case 2:
			return this; // HAGY fix, before return null;
		default:
			return this.getAncestor(ADMINISTRATION);
		}
	}

	public String getUnitName() {
		if (this.ou.size() == 0) {
			return "";
		}
		return this.ou.get(0);
	}

	public DN getUnit() {
		if (this.ou.size() == 0) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (String aOU : this.ou) {
			str.append("ou=");
			str.append(aOU);
			str.append(",");
		}

		for (String aDC : this.dc) {
			str.append("dc=");
			str.append(aDC);
			str.append(",");
		}
		if (!"".equals(o)) {
			str.append("o=");
			str.append(o);
		}
		String tmpStr = str.toString();
		if (tmpStr.endsWith(",")) {
			tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
		}

		return DN.createDNFromString(tmpStr);
	}

	public DN getParentDN(int rootLevel) {
		List<String> theCN = this.cn;
		List<String> theOU = this.ou;
		List<String> theDC = this.dc;

		if (theCN != null && theCN.size() > 0) {
			theCN = theCN.size() < 2 ? new ArrayList<String>() : theCN.subList(1, theCN.size());
		} else if (theOU != null && theOU.size() > rootLevel) {
			theOU = getOuWithoutUnit();
		} else {
			return null;
		}
		return new DN(theCN, theOU, theDC, this.o);
	}

	public DN getParentDN() {
		return getParentDN(0);
	}

	/**
	 * @param generations
	 * @return returns the specified number of generations.
	 */
	public List<DN> getAncestors() {
		return getAncestors(1, ADMINISTRATION);
	}

	/**
	 * @param generations
	 * @return returns the specified number of generations.
	 * 
	 *         Use 0 for all generations. A positive number to get an exact count of generations. A negative number to get all generations except for the last n generations.
	 */
	public List<DN> getAncestors(int fromGeneration, int toGeneration) {
		List<DN> ancestors = new ArrayList<DN>();
		DN parent = this.getParentDN();

		int position = 1;
		while (parent != null) {
			parent.setPosition(position++);
			if (--fromGeneration <= 0) {
				ancestors.add(parent);
			}
			parent = parent.getParentDN();
		}

		if (ancestors.size() < Math.abs(toGeneration))
			return null;
		// Remove generations at the end
		if (toGeneration < 0) {
			ancestors = ancestors.subList(0, ancestors.size() + toGeneration);
		}
		return ancestors;
	}

	/**
	 * @param generations
	 * @return returns the specified number of generations.
	 * 
	 *         Use 0 for all generations. A positive number to get an exact count of generations. A negative number to get all generations except for the last n generations.
	 */
	public DN getAncestor(int generation) {
		if (generation == 0) {
			return this;
		}
		List<DN> ancestors = new ArrayList<DN>();
		DN parent = this.getParentDN();

		while (parent != null) {
			ancestors.add(parent);
			parent = parent.getParentDN();
		}

		generation = generation < 0 ? generation + ancestors.size() : generation - 1;

		return ancestors.get(generation);

	}

	/**
	 * The method is mainly used when creating links to units. This can cause problems if the link contains special characters like ������. The method takes care of encoding incompatibility problems
	 * between IBM WAS and Apache Tomcat.
	 * 
	 * @return Url encoded value of the DN.
	 */
	public String getUrlEncoded() {
		try {
			if (EnvAssistant.isRunningOnIBM()) {
				// Ok Running on IBM let�s do standard handling
				return URLEncoder.encode(this.toString(), "UTF-8");
			} else {
				// Ok we don�t run on IBM (maybe Tomcat) so let�s make url
				// encoding like this
				return URLEncoder.encode(this.toString(), "iso-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Exception in method=" + this.getClass().getName() + "::getUrlEncoded(), Exception=" + e.toString()
					+ ", this could be caused due to the fact that the environment variable env.kivtools.server.istomcat is not set.");
			return this.toString().replace("=", "%3D");
		} catch (Exception e) {
			System.out.println("Exception in method=" + this.getClass().getName() + "::getUrlEncoded(), Exception=" + e.toString()
					+ ", this could be caused due to the fact that the environment variable env.kivtools.server.istomcat is not set.");
			return this.toString().replace("=", "%3D");
		}
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		for (String aCN : this.cn) {
			str.append("cn=");
			str.append(aCN);
			str.append(",");
		}
		for (String aOU : this.ou) {
			str.append("ou=");
			str.append(aOU);
			str.append(",");
		}
		for (String aDC : this.dc) {
			str.append("dc=");
			str.append(aDC);
			str.append(",");
		}
		if (!"".equalsIgnoreCase(o)) {
			str.append("o=");
			str.append(o);
		}
		String tempStr = str.toString();
		if (tempStr.endsWith(",")) {
			tempStr = tempStr.substring(0, tempStr.length() - 1);
		}
		return tempStr;
	}

	private List<String> getOuWithoutUnit() {
		if (this.ou.size() < 2) {
			return new ArrayList<String>();
		}
		return this.ou.subList(1, this.ou.size());
	}

	public Iterator<DN> iterator() {
		return new DNIterator();
	}

	private class DNIterator implements Iterator<DN> {
		private int cnPoint = 0;
		private int ouPoint = 0;
		private int dcPoint = 0;
		private String oPoint = o;

		public boolean hasNext() {
			return (cn != null && cnPoint < cn.size()) || (ou != null && ouPoint < ou.size()) || (oPoint != null);
		}

		public DN next() {
			List<String> theCn = cn;
			List<String> theOu = ou;
			List<String> theDc = dc;
			String theO = o;

			if (cn != null && cnPoint < cn.size()) {
				theCn = cn.subList(cnPoint++, cn.size());
			} else if (ou != null && ouPoint < ou.size()) {
				theCn = new ArrayList<String>();
				theOu = ou.subList(ouPoint++, ou.size());
			} else if (oPoint != null) {
				theCn = new ArrayList<String>();
				theOu = new ArrayList<String>();
				oPoint = null;
			} else {
				return null;
			}
			return new DN(theCn, theOu, theDc, theO);
		}

		public void remove() {
		}

	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cn == null) ? 0 : cn.hashCode());
		result = prime * result + ((dc == null) ? 0 : dc.hashCode());
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + ((ou == null) ? 0 : ou.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DN other = (DN) obj;
		if (cn == null) {
			if (other.cn != null)
				return false;
		} else if (!cn.equals(other.cn))
			return false;
		if (dc == null) {
			if (other.dc != null)
				return false;
		} else if (!dc.equals(other.dc))
			return false;
		if (o == null) {
			if (other.o != null)
				return false;
		} else if (!o.equals(other.o))
			return false;
		if (ou == null) {
			if (other.ou != null)
				return false;
		} else if (!ou.equals(other.ou))
			return false;
		return true;
	}
}
