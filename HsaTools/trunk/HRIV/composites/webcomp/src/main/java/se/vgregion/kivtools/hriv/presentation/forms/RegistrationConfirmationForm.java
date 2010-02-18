/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.hriv.presentation.forms;

import java.io.Serializable;

/**
 * Form for the registration confirmation page.
 * 
 * @author Jonas Liljenfeldt & David Bennehult
 */
public class RegistrationConfirmationForm implements Serializable {
  private static final long serialVersionUID = 1L;

  private String ssn;
  private String hsaIdentity;
  private String signature;

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public String getHsaIdentity() {
    return hsaIdentity;
  }

  public void setHsaIdentity(String hsaIdentity) {
    this.hsaIdentity = hsaIdentity;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
}
