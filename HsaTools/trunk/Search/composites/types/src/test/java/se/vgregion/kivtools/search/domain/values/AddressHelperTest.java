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

package se.vgregion.kivtools.search.domain.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AddressHelperTest {

  private static final String CITY = "Göteborg";
  private static final String ZIPCODE_CITY = "412 63 Göteborg";
  private static final String ZIPCODE_FORMATTED = "412 63";
  private static final String ZIPCODE = "41263";
  private static final String STREET = "Storgatan 1";
  private static final String EXCEPTIONED_STREET = "Röda stråket";

  @Test
  public void testConvertToStreetAddress() {
    Address address = AddressHelper.convertToStreetAddress(null);
    assertNotNull("An empty address should have been created", address);
    assertEquals("Street should be empty", "", address.getStreet());

    List<String> origAddress = new ArrayList<String>();
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An empty address should have been created", address);
    assertEquals("Street should be empty", "", address.getStreet());

    origAddress.add(STREET);
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", "", address.getStreet());
    assertEquals("Unexpected value for additional information", STREET, address.getAdditionalInfoToString());

    origAddress.add(ZIPCODE_CITY);
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", STREET, address.getStreet());
    assertEquals("Unexpected value for zipcode", ZIPCODE, address.getZipCode().getZipCode());
    assertEquals("Unexpected value for zipcode formatted", ZIPCODE_FORMATTED, address.getZipCode().getFormattedZipCode().getZipCode());

    origAddress.add(STREET);
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", STREET, address.getStreet());
    assertEquals("Unexpected value for zipcode", ZIPCODE, address.getZipCode().getZipCode());
    assertEquals("Unexpected value for additional information", "Storgatan 1", address.getAdditionalInfoToString());

    origAddress.remove(STREET);
    origAddress.remove(STREET);
    origAddress.add("Sverige");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "412 63 GöteborgSverige", address.getAdditionalInfoToString());

    origAddress.clear();
    origAddress.add("Storgatan");
    origAddress.add("S-41263");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "StorgatanS-41263", address.getAdditionalInfoToString());

    origAddress.remove("S-41263");
    origAddress.add("S1234");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "StorgatanS1234", address.getAdditionalInfoToString());

    origAddress.remove("S1234");
    origAddress.add("41263");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "Storgatan41263", address.getAdditionalInfoToString());

    origAddress.remove("41263");
    origAddress.add("41263  28 Göteborg");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for zipcode", "41263", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", CITY, address.getCity());

    origAddress.remove("41263  28 Göteborg");
    origAddress.add(CITY);
    origAddress.add("Sverige");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "StorgatanGöteborgSverige", address.getAdditionalInfoToString());

    origAddress.add("");
    address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for additional information", "StorgatanGöteborgSverige", address.getAdditionalInfoToString());
  }

  @Test
  public void testConvertToAddress() {
    Address address = AddressHelper.convertToAddress((List<String>) null);
    assertNotNull("An empty address should have been created", address);
    assertEquals("Street should be empty", "", address.getStreet());

    List<String> origAddress = new ArrayList<String>();
    address = AddressHelper.convertToAddress(origAddress);
    assertNotNull("An empty address should have been created", address);
    assertEquals("Street should be empty", "", address.getStreet());

    origAddress.add(STREET);
    address = AddressHelper.convertToAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", "", address.getStreet());
    assertEquals("Unexpected value for additional information", STREET, address.getAdditionalInfoToString());

    origAddress.clear();
    origAddress.add("string");
    origAddress.add("number");
    origAddress.add("date");
    address = AddressHelper.convertToAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", "", address.getStreet());
    assertEquals("Unexpected value for additional information", "stringnumberdate", address.getAdditionalInfoToString());

    origAddress.clear();
    origAddress.add(STREET);
    origAddress.add(ZIPCODE);
    origAddress.add(CITY);
    address = AddressHelper.convertToAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", STREET, address.getStreet());
    assertEquals("Unexpected value for zipcode", ZIPCODE, address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", CITY, address.getCity());
    assertNull("Unexpected value for additional information", address.getAdditionalInfoToString());
  }

  @Test
  public void testStreetAddressWithExceptionedWord() {
    List<String> origAddress = new ArrayList<String>();
    origAddress.add(STREET);
    origAddress.add(ZIPCODE_CITY);
    origAddress.add(EXCEPTIONED_STREET);
    Address address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", STREET, address.getStreet());
    assertEquals("Unexpected value for zipcode", ZIPCODE, address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", CITY, address.getCity());
    assertEquals("Unexpected value for additionalInfo", EXCEPTIONED_STREET, address.getAdditionalInfoToString());
  }

  @Test
  public void kungsportsAvenynConsideredAValidStreetAddress() {
    List<String> origAddress = new ArrayList<String>();
    origAddress.add("Kungsportsavenyn 31-35");
    origAddress.add("411 36 Göteborg");
    Address address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("address", address);
    assertEquals("street", "Kungsportsavenyn 31-35", address.getStreet());
  }

  @Test
  public void addressWithOnlyExceptionedStreetUsesExceptionedStreetAsTheStreetRow() {
    List<String> origAddress = new ArrayList<String>();
    origAddress.add(EXCEPTIONED_STREET);
    origAddress.add(ZIPCODE_CITY);
    Address address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", EXCEPTIONED_STREET, address.getStreet());
    assertEquals("Unexpected value for zipcode", ZIPCODE, address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", CITY, address.getCity());
  }

  @Test
  public void addressFromSahlgrenskaIsConsideredAValidAddress() {
    List<String> origAddress = new ArrayList<String>();
    origAddress.add("Gröna stråket 4");
    origAddress.add("413 45");
    origAddress.add("Göteborg");
    Address address = AddressHelper.convertToStreetAddress(origAddress);
    assertNotNull("An address should have been created", address);
    assertEquals("Unexpected value for street", "Gröna stråket 4", address.getStreet());
    assertEquals("Unexpected value for zipcode", "413 45", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
  }

  @Test
  public void validSkovdeAddress() {
    List<String> source = Arrays.asList("Åsboholmsgatan 6", "405 51", "Borås", "K11 ingången plan 6");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Åsboholmsgatan 6", address.getStreet());
    assertEquals("Unexpected value for zipcode", "405 51", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Borås", address.getCity());
  }

  @Test
  public void validSkovdeAddress2() {
    List<String> source = Arrays.asList("Badhusgatan 4", "541 30", "Skövde", "Vid parkering krävs parkeringstillstånd som hämtas i receptionen.");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Badhusgatan 4", address.getStreet());
    assertEquals("Unexpected value for zipcode", "541 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Skövde", address.getCity());
    assertEquals("[Vid parkering krävs parkeringstillstånd som hämtas i receptionen.]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validSkovdeAddress3() {
    List<String> source = Arrays.asList("Knoppaliden 3", "541 47", "Skövde");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Knoppaliden 3", address.getStreet());
    assertEquals("Unexpected value for zipcode", "541 47", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Skövde", address.getCity());
  }

  @Test
  public void validSkovdeAddressWithInvalidCityWord() {
    List<String> source = Arrays.asList("Fjällgatan 39 nb", "504 61", "Borås", "Entré från busshållsplats");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Fjällgatan 39 nb", address.getStreet());
    assertEquals("Unexpected value for zipcode", "504 61", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Borås", address.getCity());
  }

  @Test
  public void validHisingsKarraAddress() {
    List<String> source = Arrays.asList("Lillekärr Södra 51", "425 31", "Hisings Kärra");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lillekärr Södra 51", address.getStreet());
    assertEquals("Unexpected value for zipcode", "425 31", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Hisings Kärra", address.getCity());
  }

  @Test
  public void validKungalvsSjukhusAddress() {
    List<String> source = Arrays.asList("Kungälvs sjukhus", "442 83", "Kungälv");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Kungälvs sjukhus", address.getStreet());
    assertEquals("Unexpected value for zipcode", "442 83", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Kungälv", address.getCity());
  }

  @Test
  public void validKungalvsSjukhusAsAdditionalInfoAddress() {
    List<String> source = Arrays.asList("Lasarettsgatan", "442 34", "Kungälv", "Plan 4 Kungälvs sjukhus");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lasarettsgatan", address.getStreet());
    assertEquals("Unexpected value for zipcode", "442 34", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Kungälv", address.getCity());
    assertEquals("[Plan 4 Kungälvs sjukhus]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validMolndalsSjukhusAddress() {
    List<String> source = Arrays.asList("Akutmottagningen, Mölndals Sjukhus", "431 30", "Mölndal");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Akutmottagningen, Mölndals Sjukhus", address.getStreet());
    assertEquals("Unexpected value for zipcode", "431 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Mölndal", address.getCity());
  }

  @Test
  public void validMolndalsSjukhusAsAdditionalInfoAddress() {
    List<String> source = Arrays.asList("Biskopsbogatan", "431 80", "Mölndal", "Mölndals sjukhus");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Biskopsbogatan", address.getStreet());
    assertEquals("Unexpected value for zipcode", "431 80", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Mölndal", address.getCity());
    assertEquals("[Mölndals sjukhus]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validAngeredAddress() {
    List<String> source = Arrays.asList("Bergsgårdsgärdet 89B", "424 32", "Angered", "Välkommen 1 trappa upp.");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Bergsgårdsgärdet 89B", address.getStreet());
    assertEquals("Unexpected value for zipcode", "424 32", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Angered", address.getCity());
    assertEquals("[Välkommen 1 trappa upp.]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validStenungsundAddress() {
    List<String> source = Arrays.asList("Jullen 3", "444 30", "Stenungsund", "(Bottenvån)");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Jullen 3", address.getStreet());
    assertEquals("Unexpected value for zipcode", "444 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Stenungsund", address.getCity());
    assertEquals("[(Bottenvån)]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validStenungsundAddress2() {
    List<String> source = Arrays.asList("Jullen 4", "444 30", "Stenungsund", "(Bottenvån)");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Jullen 4", address.getStreet());
    assertEquals("Unexpected value for zipcode", "444 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Stenungsund", address.getCity());
    assertEquals("[(Bottenvån)]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validMolnlyckeAddress() {
    List<String> source = Arrays.asList("Mölnlycke Fabriker 5", "435 35", "Mölnlycke");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Mölnlycke Fabriker 5", address.getStreet());
    assertEquals("Unexpected value for zipcode", "435 35", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Mölnlycke", address.getCity());
  }

  @Test
  public void validCarlanderskaAddress() {
    List<String> source = Arrays.asList("Carlanderska sjukhemmet", "412 55", "Göteborg");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Carlanderska sjukhemmet", address.getStreet());
    assertEquals("Unexpected value for zipcode", "412 55", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
  }

  @Test
  public void validLillaBommenAddress() {
    List<String> source = Arrays.asList("Lilla Bommen 6", "411 04", "Göteborg");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lilla Bommen 6", address.getStreet());
    assertEquals("Unexpected value for zipcode", "411 04", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
  }

  @Test
  public void validOstraSjukhusetAddress() {
    List<String> source = Arrays.asList("Östra sjukhuset", "416 85", "Göteborg", "Centralkliniken, hiss C, plan 0");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Östra sjukhuset", address.getStreet());
    assertEquals("Unexpected value for zipcode", "416 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
    assertEquals("[Centralkliniken, hiss C, plan 0]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validSjoportenAddress() {
    List<String> source = Arrays.asList("Sjöporten 4", "417 64", "Göteborg");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Sjöporten 4", address.getStreet());
    assertEquals("Unexpected value for zipcode", "417 64", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
  }

  @Test
  public void validKarnsjukhusetAddress() {
    List<String> source = Arrays.asList("Kärnsjukhuset", "541 42", "Skövde", "Blå gata entréplan");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Kärnsjukhuset", address.getStreet());
    assertEquals("Unexpected value for zipcode", "541 42", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Skövde", address.getCity());
    assertEquals("[Blå gata entréplan]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validVanersborgAddress() {
    List<String> source = Arrays.asList("Edsvägen 3", "462 23", "Vänersborg", "Vänerparken");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Edsvägen 3", address.getStreet());
    assertEquals("Unexpected value for zipcode", "462 23", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Vänersborg", address.getCity());
    assertEquals("[Vänerparken]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validUddevallaSjukhusAddress() {
    List<String> source = Arrays.asList("Uddevalla sjukhus", "ingång U", "451 80", "Uddevalla");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Uddevalla sjukhus", address.getStreet());
    assertEquals("Unexpected value for zipcode", "451 80", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Uddevalla", address.getCity());
    assertEquals("[ingång U]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validAlingsasAddress() {
    List<String> source = Arrays.asList("Stråhles Allé 1A", "441 32", "Alingsås");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Stråhles Allé 1A", address.getStreet());
    assertEquals("Unexpected value for zipcode", "441 32", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Alingsås", address.getCity());
  }

  @Test
  public void validAlingsasAddress2() {
    List<String> source = Arrays.asList("Alingsås lasarett", "441 83", "Alingsås");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Alingsås lasarett", address.getStreet());
    assertEquals("Unexpected value for zipcode", "441 83", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Alingsås", address.getCity());
  }

  @Test
  public void validStyrsoAddress() {
    List<String> source = Arrays.asList("Vårdcentralen Styrsö", "430 84", "Styrsö");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Vårdcentralen Styrsö", address.getStreet());
    assertEquals("Unexpected value for zipcode", "430 84", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Styrsö", address.getCity());
  }

  @Test
  public void validHamburgsundAddress() {
    List<String> source = Arrays.asList("Sjökanten", "457 45", "Hamburgsund");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Sjökanten", address.getStreet());
    assertEquals("Unexpected value for zipcode", "457 45", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Hamburgsund", address.getCity());
  }

  @Test
  public void validLjungskileAddress() {
    List<String> source = Arrays.asList("Hälle Lider 15", "459 32", "Ljungskile");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Hälle Lider 15", address.getStreet());
    assertEquals("Unexpected value for zipcode", "459 32", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Ljungskile", address.getCity());
  }

  @Test
  public void validHovasAddress() {
    List<String> source = Arrays.asList("Ågrenska Amundön", "436 02", "Hovås");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Ågrenska Amundön", address.getStreet());
    assertEquals("Unexpected value for zipcode", "436 02", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Hovås", address.getCity());
  }

  @Test
  public void validSahlgrenskaAddress() {
    List<String> source = Arrays.asList("Blå Stråket 6", "413 45", "Göteborg", "Blå Stråket 6, plan 3, Sahlgrenska Sjukhuset");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Blå Stråket 6", address.getStreet());
    assertEquals("Unexpected value for zipcode", "413 45", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
    assertEquals("[Blå Stråket 6, plan 3, Sahlgrenska Sjukhuset]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validTrollhattanAddress() {
    List<String> source = Arrays.asList("Lärketorpsvägen", "461 85", "Trollhättan", "Västra gatan Plan 2");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lärketorpsvägen", address.getStreet());
    assertEquals("Unexpected value for zipcode", "461 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Trollhättan", address.getCity());
    assertEquals("[Västra gatan Plan 2]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validTrollhattanAddress2() {
    List<String> source = Arrays.asList("Lärketorpsvägen", "461 85", "Trollhättan", "Södra gatan");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lärketorpsvägen", address.getStreet());
    assertEquals("Unexpected value for zipcode", "461 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Trollhättan", address.getCity());
    assertEquals("[Södra gatan]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validTrollhattanAddress3() {
    List<String> source = Arrays.asList("Lärketorpsvägen", "461 85", "Trollhättan", "Östra gatan till höger");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lärketorpsvägen", address.getStreet());
    assertEquals("Unexpected value for zipcode", "461 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Trollhättan", address.getCity());
    assertEquals("[Östra gatan till höger]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validTrollhattanAddress4() {
    List<String> source = Arrays.asList("Lärketorpsvägen", "461 85", "Trollhättan", "Norra gatan Entréplan");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lärketorpsvägen", address.getStreet());
    assertEquals("Unexpected value for zipcode", "461 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Trollhättan", address.getCity());
    assertEquals("[Norra gatan Entréplan]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validUddevallaAddress() {
    List<String> source = Arrays.asList("Fjällvägen 9", "451 80", "Uddevalla", "Sjukhusparken");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Fjällvägen 9", address.getStreet());
    assertEquals("Unexpected value for zipcode", "451 80", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Uddevalla", address.getCity());
    assertEquals("[Sjukhusparken]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validGoteborgAddress() {
    List<String> source = Arrays.asList("Skärgårdsgatan 4, vån 4", "414 58", "Göteborg", "Hemsjukvårdsläkarna, Primärvården Centrum");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Skärgårdsgatan 4, vån 4", address.getStreet());
    assertEquals("Unexpected value for zipcode", "414 58", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Göteborg", address.getCity());
    assertEquals("[Hemsjukvårdsläkarna, Primärvården Centrum]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validLillaEdetAddress() {
    List<String> source = Arrays.asList("Järnvägsgatan 8", "463 30", "Lilla Edet", "Vårdcentrum");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Järnvägsgatan 8", address.getStreet());
    assertEquals("Unexpected value for zipcode", "463 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Lilla Edet", address.getCity());
    assertEquals("[Vårdcentrum]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validLidkopingAddress() {
    List<String> source = Arrays.asList("Sjukhuset i Lidköping", "531 85", "Lidköping", "Röntgen entréplan");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Sjukhuset i Lidköping", address.getStreet());
    assertEquals("Unexpected value for zipcode", "531 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Lidköping", address.getCity());
    assertEquals("[Röntgen entréplan]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validLidkopingAddress2() {
    List<String> source = Arrays.asList("Esplanaden 7", "531 50", "Lidköping");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Esplanaden 7", address.getStreet());
    assertEquals("Unexpected value for zipcode", "531 50", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Lidköping", address.getCity());
  }

  @Test
  public void validBorasAddress() {
    List<String> source = Arrays.asList("Södra Älvsborgs Sjukhus", "501 82", "Borås", "Ingång 1, plan 3");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Södra Älvsborgs Sjukhus", address.getStreet());
    assertEquals("Unexpected value for zipcode", "501 82", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Borås", address.getCity());
    assertEquals("[Ingång 1, plan 3]", address.getAdditionalInfo().toString());
  }

  @Test
  public void validBorasAddress2() {
    List<String> source = Arrays.asList("Hestra Midgård", "502 66", "Borås");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Hestra Midgård", address.getStreet());
    assertEquals("Unexpected value for zipcode", "502 66", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Borås", address.getCity());
  }

  @Test
  public void validSexdregaAddress() {
    List<String> source = Arrays.asList("Billeberg Eriksgården", "512 91", "Sexdrega");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Billeberg Eriksgården", address.getStreet());
    assertEquals("Unexpected value for zipcode", "512 91", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Sexdrega", address.getCity());
  }

  @Test
  public void validLysekilAddress() {
    List<String> source = Arrays.asList("Lysekils sjukhus", "453 25", "Lysekil");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Lysekils sjukhus", address.getStreet());
    assertEquals("Unexpected value for zipcode", "453 25", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Lysekil", address.getCity());
  }

  @Test
  public void validJarnaAddress() {
    List<String> source = Arrays.asList("Ytterjärna", "153 91", "Järna");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Ytterjärna", address.getStreet());
    assertEquals("Unexpected value for zipcode", "153 91", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Järna", address.getCity());
  }

  @Test
  public void validStromstadAddress() {
    List<String> source = Arrays.asList("Smedjegränd 8", "452 36", "Strömstad");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Smedjegränd 8", address.getStreet());
    assertEquals("Unexpected value for zipcode", "452 36", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Strömstad", address.getCity());
  }

  @Test
  public void validTanumshedeAddress() {
    List<String> source = Arrays.asList("Hedegården", "457 30", "Tanumshede");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Hedegården", address.getStreet());
    assertEquals("Unexpected value for zipcode", "457 30", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Tanumshede", address.getCity());
  }

  @Test
  public void validGrebbestadAddress() {
    List<String> source = Arrays.asList("Tanumstrand", "45795", "Grebbestad");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Tanumstrand", address.getStreet());
    assertEquals("Unexpected value for zipcode", "45795", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Grebbestad", address.getCity());
  }

  @Test
  public void validFalkopingAddress() {
    List<String> source = Arrays.asList("Sjukhuset i Falköping", "521 85", "Falköping");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "Sjukhuset i Falköping", address.getStreet());
    assertEquals("Unexpected value for zipcode", "521 85", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "Falköping", address.getCity());
  }

  @Test
  public void invalidUddevallaAddress() {
    List<String> source = Arrays.asList("Uddevalla sjukhus, ingång U  451 80 Uddevalla");

    Address address = AddressHelper.convertToStreetAddress(source);

    assertEquals("Unexpected value for street", "", address.getStreet());
    assertEquals("Unexpected value for zipcode", "", address.getZipCode().getZipCode());
    assertEquals("Unexpected value for city", "", address.getCity());
    assertEquals("[Uddevalla sjukhus, ingång U  451 80 Uddevalla]", address.getAdditionalInfo().toString());
  }
}
