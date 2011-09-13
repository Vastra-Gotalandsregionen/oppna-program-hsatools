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

package se.vgregion.kivtools.search.svc.impl.cache;

import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.PersonCache;

import com.domainlanguage.time.TimePoint;

public class PersonCacheLoaderMock implements CacheLoader<PersonCache> {

  @Override
  public PersonCache loadCache() {
    PersonCache personCache = new PersonCache();

    personCache.add(new PersonBuilder().withVgrid("kon829").withHsaIdentity("hsa-456").withGivenName("Kristoffer").withSurname("Olin").withEmployment()
        .withModified(TimePoint.atMidnightGMT(2010, 2, 12)).endEmployment().withEmployment().withModified(TimePoint.atMidnightGMT(2010, 2, 16)).endEmployment().withEmployment()
        .withModified(TimePoint.atMidnightGMT(2010, 1, 10)).endEmployment().build());
    personCache
        .add(new PersonBuilder().withVgrid("krila8").withHsaIdentity("hsa-123").withGivenName("Kristian").withSurname("Norling").withEmployment().withTitle("Assistent").endEmployment().build());
    personCache.add(new PersonBuilder().withVgrid("hanjo26").withHsaIdentity("hsa-789").withGivenName("Hanna").withSurname("Jonsson").withEmployment().withTitle("Projektledare").endEmployment()
        .build());

    return personCache;
  }

  @Override
  public PersonCache createEmptyCache() {
    return new PersonCache();
  }

  private static class PersonBuilder {
    private String vgrid;
    private String hsaIdentity;
    private String dn;
    private String givenName;
    private String surname;
    private final List<Employment> employments = new ArrayList<Employment>();

    public PersonBuilder withVgrid(String vgrid) {
      this.vgrid = vgrid;
      return this;
    }

    public PersonBuilder withHsaIdentity(String hsaIdentity) {
      this.hsaIdentity = hsaIdentity;
      return this;
    }

    public PersonBuilder withDn(String dn) {
      this.dn = dn;
      return this;
    }

    public PersonBuilder withGivenName(String givenName) {
      this.givenName = givenName;
      return this;
    }

    public PersonBuilder withSurname(String surname) {
      this.surname = surname;
      return this;
    }

    public EmploymentBuilder withEmployment() {
      return new EmploymentBuilder(this);
    }

    public Person build() {
      Person person = new Person();

      person.setVgrId(this.vgrid);
      person.setHsaIdentity(this.hsaIdentity);
      person.setDn(this.dn);
      person.setGivenName(this.givenName);
      person.setSn(this.surname);
      if (this.employments.size() > 0) {
        person.setEmployments(this.employments);
      }

      return person;
    }

    public void addEmployment(Employment employment) {
      this.employments.add(employment);
    }
  }

  private static class EmploymentBuilder {
    private final PersonBuilder personBuilder;
    private TimePoint modified;
    private String title;

    public EmploymentBuilder(PersonBuilder personBuilder) {
      this.personBuilder = personBuilder;
    }

    public EmploymentBuilder withModified(TimePoint modified) {
      this.modified = modified;
      return this;
    }

    public EmploymentBuilder withTitle(String title) {
      this.title = title;
      return this;
    }

    public PersonBuilder endEmployment() {
      Employment employment = new Employment();
      if (this.modified != null) {
        employment.setModifyTimestamp(this.modified);
      } else {
        employment.setModifyTimestamp(TimePoint.from(0));
      }
      employment.setTitle(this.title);
      this.personBuilder.addEmployment(employment);
      return this.personBuilder;
    }
  }
}
