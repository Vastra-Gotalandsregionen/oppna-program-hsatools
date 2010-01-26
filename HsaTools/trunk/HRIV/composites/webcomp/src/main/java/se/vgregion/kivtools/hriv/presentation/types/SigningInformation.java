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
package se.vgregion.kivtools.hriv.presentation.types;

/**
 * Information regarding a signed document.
 * 
 * @author Jonas & Joakim
 */
public class SigningInformation {
  private final String nationalId;
  private final String samlResponse;

  /**
   * Constructs a SigningInformation object.
   * 
   * @param nationalId The national id of the user signing the document.
   * @param samlResponse The complete SAMLResponse from the signing.
   */
  public SigningInformation(String nationalId, String samlResponse) {
    this.nationalId = nationalId;
    this.samlResponse = samlResponse;
  }

  /**
   * Getter for the samlResponse property.
   * 
   * @return The complete SAMLResponse from the signing.
   */
  public String getSamlResponse() {
    return samlResponse;
  }

  /**
   * Getter for the nationalId property.
   * 
   * @return The national id of the user signing the document.
   */
  public String getNationalId() {
    return nationalId;
  }
}
