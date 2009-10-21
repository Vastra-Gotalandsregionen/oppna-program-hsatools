/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.domain.values;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.vgregion.kivtools.util.StringUtil;

/**
 * A Distinguished Name implementation.
 * 
 * @author Anders Asplund - KnowIT
 */
public final class DN implements Serializable, Iterable<DN> {

  private static final int ADMINISTRATION = -3;
  private static final long serialVersionUID = 1L;
  private final List<String> cn;
  private final List<String> ou;
  private final List<String> dc;
  private final String o;
  // Position of administration
  // Used for formatting ancestors in web gui
  private final int position;

  /**
   * Constructs a new DN instance using the provided fields.
   * 
   * @param cn The list of common names to use.
   * @param ou The list of organizational units to use.
   * @param dc The list of domain components to use.
   * @param o The organization to use.
   */
  public DN(List<String> cn, List<String> ou, List<String> dc, String o) {
    this.cn = defensiveCopy(cn);
    this.ou = defensiveCopy(ou);
    this.dc = defensiveCopy(dc);
    this.o = StringUtil.emptyStringIfNull(o);
    this.position = 0;
  }

  /**
   * Constructs a new DN instance using the provided DN as a base but sets the DN's position to the provided value.
   * 
   * @param originalDn The DN to base the new DN on.
   * @param position The new value for position for this DN.
   */
  private DN(DN originalDn, int position) {
    this.cn = defensiveCopy(originalDn.cn);
    this.ou = defensiveCopy(originalDn.ou);
    this.dc = defensiveCopy(originalDn.dc);
    this.o = originalDn.o;
    this.position = position;
  }

  /**
   * Creates a DN instance using the provided string.
   * 
   * @param dnString The DN-string to base the DN instance on.
   * @return Returns a new DN object.
   */
  public static DN createDNFromString(String dnString) {

    List<String> cn = new ArrayList<String>();
    List<String> ou = new ArrayList<String>();
    List<String> dc = new ArrayList<String>();
    String o = "";
    String cleanDnString = dnString.replace("\\", "");
    String[] org = cleanDnString.split(",?.?.=");
    String domain = "";
    int start = 0;
    int end = 0;

    for (int i = 0; i < org.length; i++) {
      if (org[i] != null && !org[i].equals("")) {
        end = cleanDnString.indexOf(org[i], start) - 1;
        domain = cleanDnString.substring(start, end);
        if (domain.equalsIgnoreCase("cn")) {
          cn.add(org[i]);
        } else if (domain.equalsIgnoreCase("ou")) {
          ou.add(org[i]);
        } else if (domain.equalsIgnoreCase("dc")) {
          dc.add(org[i]);
        } else if (domain.equalsIgnoreCase("o")) {
          o = org[i];
        }

        start = cleanDnString.indexOf(",", org[i].length() + end) + 1;
      }
    }

    return new DN(cn, ou, dc, o);
  }

  /**
   * Escapes any comma in the DN's common names, organizational units and domain components.
   * 
   * @return A new DN with commas escaped.
   */
  public DN escape() {
    List<String> newCn = new ArrayList<String>();
    List<String> newOu = new ArrayList<String>();
    List<String> newDc = new ArrayList<String>();

    for (String oldCn : cn) {
      newCn.add(oldCn.replace(",", "\\,"));
    }

    for (String oldOu : ou) {
      newOu.add(oldOu.replace(",", "\\,"));
    }

    for (String oldDc : dc) {
      newDc.add(oldDc.replace(",", "\\,"));
    }

    return new DN(newCn, newOu, newDc, o);
  }

  /**
   * Checks if a unit is at the same level as it's administration.
   * 
   * @return The string "true" if the unit is at the same level as it's administration, otherwise the string "false".
   */
  public String getIsUnitAndAdministrationOnSameLevel() {
    // HAGY fix, for taking care of
    boolean temp = this.ou.size() <= 2;
    // special case
    return "" + temp;
  }

  /**
   * Gets the units administration.
   * 
   * @return null if the unit is above the administration level, the unit itself if the unit is at the administration level and an ancestor at the administration level otherwise.
   */
  public DN getAdministration() {
    DN administration;

    switch (this.ou.size()) {
      case 0:
      case 1:
        administration = null;
        break;
      case 2:
        // HAGY fix, before return null;
        administration = this;
        break;
      default:
        administration = this.getAncestor(ADMINISTRATION);
        break;
    }

    return administration;
  }

  /**
   * Getter for the unit name for this DN.
   * 
   * @return The value of the first organizational unit.
   */
  public String getUnitName() {
    if (this.ou.size() == 0) {
      return "";
    }
    return this.ou.get(0);
  }

  /**
   * Getter for the unit for this DN.
   * 
   * @return The DN for the unit-part of this DN.
   */
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

  /**
   * Gets this DN's parent DN.
   * 
   * @param rootLevel The minimum level of the organizational unit.
   * @return The DN's parent DN or null if no common names or organizational units exists.
   */
  public DN getParentDN(int rootLevel) {
    List<String> theCN = this.cn;
    List<String> theOU = this.ou;
    List<String> theDC = this.dc;

    if (theCN.size() > 0) {
      if (theCN.size() < 2) {
        theCN = new ArrayList<String>();
      } else {
        theCN = theCN.subList(1, theCN.size());
      }
    } else if (theOU.size() > rootLevel) {
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
   * Gets a list of all the DN's ancestor DN's.
   * 
   * @return returns a list of all the DN's ancestor DN's.
   */
  public List<DN> getAncestors() {
    return getAncestors(1, ADMINISTRATION);
  }

  /**
   * Gets a list of the DN's ancestors starting with the provided fromGeneration and ending at the provided toGeneration.
   * 
   * @param fromGeneration The first generation to return.
   * @param toGeneration The last generation to return.
   * @return returns a list of the DN's ancestors starting with the provided fromGeneration and ending at the provided toGeneration.
   */
  public List<DN> getAncestors(int fromGeneration, int toGeneration) {
    int currentFromGeneration = fromGeneration;
    List<DN> ancestors = new ArrayList<DN>();
    DN parent = this.getParentDN();

    int currentPosition = 1;
    while (parent != null) {
      if (--currentFromGeneration <= 0) {
        ancestors.add(new DN(parent, currentPosition));
      }
      currentPosition++;
      parent = parent.getParentDN();
    }

    if (ancestors.size() < Math.abs(toGeneration)) {
      return null;
    }
    // Remove generations at the end
    if (toGeneration < 0) {
      ancestors = ancestors.subList(0, ancestors.size() + toGeneration);
    }
    return ancestors;
  }

  /**
   * Gets the DN for a specific generation ancestor of this DN.
   * 
   * @param generation The generation to get. Use 0 for all generations. A positive number to get an exact count of generations. A negative number to get all generations except for the last n
   *          generations.
   * @return returns the DN of the specified ancestor.
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

    int generationToGet = generation;
    if (generationToGet < 0) {
      generationToGet += ancestors.size();
    } else {
      generationToGet--;
    }
    return ancestors.get(generationToGet);

  }

  /**
   * The method is mainly used when creating links to units. This can cause problems if the link contains special characters like ������. The method takes care of encoding incompatibility problems
   * between IBM WAS and Apache Tomcat.
   * 
   * @return Url encoded value of the DN.
   */
  public String getUrlEncoded() {
    try {
      return URLEncoder.encode(this.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.out.println("Exception in method=" + this.getClass().getName() + "::getUrlEncoded(), Exception=" + e.toString()
          + ", this could be caused due to the fact that the environment variable env.kivtools.server.istomcat is not set.");
      return this.toString().replace("=", "%3D");
    }
  }

  @Override
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

  @Override
  public Iterator<DN> iterator() {
    return new DNIterator();
  }

  /**
   * Iterator for the DN class. Iterates over the DN and its ancestors.
   */
  private class DNIterator implements Iterator<DN> {
    private int cnPoint;
    private int ouPoint;
    private String oPoint = o;

    public boolean hasNext() {
      return cnPoint < cn.size() || ouPoint < ou.size() || oPoint != null;
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
      throw new UnsupportedOperationException("Remove is not implemented");
    }
  }

  public int getPosition() {
    return position;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + cn.hashCode();
    result = prime * result + dc.hashCode();
    result = prime * result + o.hashCode();
    result = prime * result + ou.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    boolean equal = true;
    if (this != obj) {
      if (obj == null) {
        equal = false;
      } else {
        if (getClass() != obj.getClass()) {
          equal = false;
        } else {
          DN other = (DN) obj;

          equal &= cn.equals(other.cn);
          equal &= dc.equals(other.dc);
          equal &= o.equals(other.o);
          equal &= ou.equals(other.ou);
        }
      }
    }
    return equal;
  }

  /**
   * Helper method for performing defensive copying of lists of strings.
   * 
   * @param original The list of strings to copy.
   * @return A new list containing the strings from the original list.
   */
  private List<String> defensiveCopy(List<String> original) {
    List<String> copy = new ArrayList<String>();

    if (original != null) {
      copy.addAll(original);
    }
    return Collections.unmodifiableList(copy);
  }
}
