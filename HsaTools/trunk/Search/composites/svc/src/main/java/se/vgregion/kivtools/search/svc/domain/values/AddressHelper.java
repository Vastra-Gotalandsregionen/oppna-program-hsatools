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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.vgregion.kivtools.search.util.Evaluator;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 * Helper utilities
 */

public class AddressHelper implements Serializable{
    private static final long serialVersionUID = 1L;
    
	// to any special address info
    private static final List<String> VALID_STREET_SUFFIX = new LinkedList<String>();
    private static final int ZIPCODE_LENGTH=5;

    //Define all valid street suffix
    static {
        VALID_STREET_SUFFIX.add("gata");
        VALID_STREET_SUFFIX.add("v\u00E4g");
        VALID_STREET_SUFFIX.add("plats");
        VALID_STREET_SUFFIX.add("torg");
        VALID_STREET_SUFFIX.add("park");
        VALID_STREET_SUFFIX.add("leden");
        VALID_STREET_SUFFIX.add("str\u00E5ket");
        VALID_STREET_SUFFIX.add("backe");
        VALID_STREET_SUFFIX.add("g\u00E5ngen");
        VALID_STREET_SUFFIX.add("\u00F6sterled");
        VALID_STREET_SUFFIX.add("k\u00E4rnsjukhuset");
        VALID_STREET_SUFFIX.add("dalslands sjukhus");
        VALID_STREET_SUFFIX.add("stig");
        VALID_STREET_SUFFIX.add("centrum");
    }

    public AddressHelper() {
        super();
    }
    
    public static boolean isStreet(String text) {
        if (Evaluator.isEmpty(text)) {
            return false;
        }
        for (String validSuffix : AddressHelper.VALID_STREET_SUFFIX) {
            if(text.toLowerCase().contains(validSuffix)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Convert a List of Strings (containing an address to an Address object)
     * @param addressList
     * @return
     */
    public static Address convertToAddress(List<String> addressList) {
    	Address address = new Address();        
        boolean foundCity = false;
        boolean foundPostalCode = false;
        boolean foundStreet = false;

        if (Evaluator.isEmpty(addressList) || (addressList.isEmpty())) {
            return address;
        }
        int size = addressList.size();

        if (size >= 3) {
            // let´s make a copy to handle this case
            List<String> additionalInfo = new ArrayList<String>(addressList);    

            // The list has at least 3 items let´s see if we can find out some things
            int indexToRemove = -1;
            // last item should be city
            String temp = additionalInfo.get(size-1);
            if (Evaluator.containsNoNumbers(temp, true)) {
                address.setCity(temp);
                foundCity=true;
                indexToRemove=size-1;
            }
            if (foundCity) {
                additionalInfo.remove(indexToRemove);// remove city from list
            }

            // the last - 1 item should be postal code.
            String zCode = additionalInfo.get(additionalInfo.size()-1);
            if (ZipCode.isValid(zCode)) {
                address.setZipCode(new ZipCode(zCode));
                foundPostalCode=true;
                indexToRemove=additionalInfo.size()-1;// remove postal code from list
            }
            if (foundPostalCode) {
                additionalInfo.remove(indexToRemove);// remove postal code from list
            }


            // let´s find the street
            size = additionalInfo.size();
            for (int i=(size-1); i >= 0&&(!foundStreet); i--) {
                temp = additionalInfo.get(i);
                if (AddressHelper.isStreet(temp)) {
                    address.setStreet(temp);
                    foundStreet=true;
                    indexToRemove=i;
                }
            }
            if (foundStreet) {
                additionalInfo.remove(indexToRemove);// remove street from list
            }

            if (foundCity && foundPostalCode) {
                // found city and zipCode that´s good enough
                address.setAdditionalInfo(additionalInfo);
                return address;        
            }            	
            // not an ok mapping we can´t rip out enough information
            address = new Address();
        }          
        address.setAdditionalInfo(addressList);
        return address;        
    }
    
    public static void print(Address address) {
    	System.out.println("**** print adress ****");
    	System.out.println("Street:" + address.getStreet() + "*");
    	System.out.println("zipCode:" + address.getZipCode().getFormattedZipCode());
    	System.out.println("City:" + address.getCity());
    	System.out.println("Additional Info:" + address.getAdditionalInfoToString());
    }
    
    private static Address jumpOut(List<String> origAddressList) {
    	Address address=new Address();
    	address.setAdditionalInfo(origAddressList);
    	return address;
    }
    
    private static boolean isStreetMoreThanOnce(List<String> adressList) {
        String temp="";
        int size = adressList.size();
        int foundStreet = 0;
        for (int row = (size-1); row >= 0 ; row--) {
			temp = adressList.get(row);
            if (AddressHelper.isStreet(temp)) {
            	foundStreet++;
            }            
		}    	
        return foundStreet>1;
    }
    /**
     * Convert a List of Strings (containing an address to an Address object)
     * The entry can look like:
     * ex1.
     * Dan Anderssonsgatan 4
     * 422 04 Hisingsbacka
     *
     * ex2.
     * Hus 14 plan 2
     * Dan Anderssonsgatan 4
     * 42204 Hisingsbacka
     *  
     * ex3.
     * Dan Anderssonsgatan 4
	 * 422 04 Hisingsbacka
     * Gröna stråket
     * 
     * ex4.
     * Dan Anderssonsgatan 4
	 * 422 04 Hisingsbacka
     * Till vänster via Huvudentré
     * Hiss A Plan 10 
     *
     * @param addressList
     * @return
     */
    public static Address convertToStreetAddress(List<String> origAddressList) {
    	Address address = new Address();        
        boolean foundCity = false;
        boolean foundZipCode = false;
        boolean foundStreet = false;

        if (Evaluator.isEmpty(origAddressList) || (origAddressList.isEmpty())) {
            return address;
        }
        
        // let´s make a copy to handle this case
        List<String> tempAdressList = new ArrayList<String>(origAddressList);    

        int size = tempAdressList.size();

        if (size <=1) {
        	// no evaluation
        	return jumpOut(origAddressList);
        }


        // 1. rip out the street
        // *********************        
        // this is a quiet tricky one since sometimes the street name is a part
        // of additional information
        // so first check if street address exists twice 
        if (isStreetMoreThanOnce(tempAdressList)) {
        	// we have street name in two different locations we give up
        	return jumpOut(origAddressList);
        }        
        
        String temp="";
        for (int row = (size-1); row >= 0 && (!foundStreet); row--) {
			temp = tempAdressList.get(row);
            if (AddressHelper.isStreet(temp)) {
                address.setStreet(temp);
                foundStreet=true; 
                tempAdressList.remove(row);
            }            
		}
        if (!foundStreet) {
        	// if there was no street we can´t do it
        	return jumpOut(origAddressList);
        }
        size = tempAdressList.size();
        
        
        // 2. rip out postal code (always 5 characters)
        // *******************************************
        ZipCode.isValid("");
        temp="";
        for (int row = (size-1); row >= 0 && (!foundZipCode); row--) {
			temp = tempAdressList.get(row);
			temp=temp.replaceAll(" ", "");
			int tempSize = temp.length();
			if (tempSize==ZIPCODE_LENGTH) {
				// there might be a zipCode here
				if (Evaluator.containsOnlyNumbers(temp, false)) {
					// ok we have a postalCode here...
					address.setZipCode(new ZipCode(temp));
					foundZipCode=true;
				}
			}
			if (tempSize > ZIPCODE_LENGTH) {
				// there might be a zipCode here
				String tempZip = temp.substring(0, ZIPCODE_LENGTH);
				if (Evaluator.containsOnlyNumbers(tempZip, false)) {
					// ok we have a postalCode here...
					address.setZipCode(new ZipCode(tempZip));
					foundZipCode=true;
					
					// check if the city is on the same line
					temp = tempAdressList.get(row);
					temp=temp.substring(ZIPCODE_LENGTH+1, temp.length());
					boolean leadingDigits=true;
					int position=0;
					for (int i=0; i<temp.length()&& (leadingDigits); i++) {
						String c = temp.substring(i, i+1);
						if (Evaluator.isEmpty(c)) {
							position++;
							continue;
						}
						if (Evaluator.isInteger(c)) {
							position++;
						}
						else {
							leadingDigits=false;
						}						
					}
					String city=temp.substring(position);
					if (!Evaluator.isEmpty(city)) {
						foundCity=true;
						address.setCity(city);
					}
				}				
			}
			if (foundZipCode) {
				// ok remove this row
				tempAdressList.remove(row);
				if (foundCity) {
					address.setAdditionalInfo(tempAdressList);
					return address;
				}
			}
		}
        
        if (!foundStreet || !foundCity) {
        	// if there was no street or no city found
        	return jumpOut(origAddressList);
        }                
        return address;        
    }    
}
