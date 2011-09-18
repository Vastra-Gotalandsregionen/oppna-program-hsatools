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

package se.vgregion.kivtools.search.svc.impl.kiv.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.springframework.ldap.core.ContextMapper;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.util.Arguments;

enum KivwsPersonAttributes {
  cn("cn"), hsaidentity("hsaidentity"), hsatitle("hsatitle"), givenname("givenname"), mail("mail"), sn("sn"), vgrid("vgr-id"), hsanickname("hsanickname"), hsaspecialitycode("hsaspecialitycode"), hsalanguageknowledgecode(
      "hsalanguageknowledgecode"), hsamiddlename("hsamiddlename");

  private KivwsPersonAttributes(String value) {
    this.value = value;
  }

  private String value;

  @Override
  public String toString() {
    return this.value;
  }
}

public class KivwsPersonMapper implements ContextMapper {
  private Map<String, List<Object>> attributes;

  @Override
  public Person mapFromContext(Object ctx) {
    Arguments.notNull("ctx", ctx);
    Person person = new Person();

    se.vgregion.kivtools.search.svc.ws.domain.kivws.Person kivwsPerson = (se.vgregion.kivtools.search.svc.ws.domain.kivws.Person) ctx;
    JAXBElement<String2ArrayOfAnyTypeMap> jaxbElmTmp = kivwsPerson.getAttributes();
    String2ArrayOfAnyTypeMap entries = jaxbElmTmp.getValue();
    this.attributes = new HashMap<String, List<Object>>();

    for (Entry entry : entries.getEntry()) {
      this.attributes.put(entry.getKey(), entry.getValue().getAnyType());
    }
    person.setHsaIdentity(this.getSingleValue(KivwsPersonAttributes.hsaidentity.toString()));
    person.setCn(this.getSingleValue(KivwsPersonAttributes.cn.toString()));
    person.setHsaTitle(this.getSingleValue(KivwsPersonAttributes.hsatitle.toString()));
    person.setGivenName(this.getSingleValue(KivwsPersonAttributes.givenname.toString()));
    person.setMail(this.getSingleValue(KivwsPersonAttributes.mail.toString()));
    person.setSn(this.getSingleValue(KivwsPersonAttributes.sn.toString()));
    person.setVgrId(this.getSingleValue(KivwsPersonAttributes.vgrid.toString()));
    person.setHsaNickName(this.getSingleValue(KivwsPersonAttributes.hsanickname.toString()));
    person.setHsaSpecialityCode(this.getMultiValue(KivwsPersonAttributes.hsaspecialitycode.toString()));
    person.setHsaLanguageKnowledgeCode(this.getMultiValue(KivwsPersonAttributes.hsalanguageknowledgecode.toString()));
    person.setHsaMiddleName(this.getSingleValue(KivwsPersonAttributes.hsamiddlename.toString()));

    return person;
  }

  private String getSingleValue(String key) {
    String returnValue = "";
    if (this.attributes.containsKey(key)) {
      returnValue = (String) this.attributes.get(key).get(0);
    }
    return returnValue;
  }

  private List<String> getMultiValue(String key) {
    List<String> returnValue = new ArrayList<String>();
    if (this.attributes.containsKey(key)) {
      List<Object> list = this.attributes.get(key);
      for (Object object : list) {
        String tmp = (String) object;
        String[] split = tmp.split("\\$");
        for (String string : split) {
          returnValue.add(string.trim());
        }
      }
    }
    return returnValue;
  }
}
