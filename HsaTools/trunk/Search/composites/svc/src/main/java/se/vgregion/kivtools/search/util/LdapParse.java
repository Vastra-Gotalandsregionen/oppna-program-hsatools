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
package se.vgregion.kivtools.search.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 *      
 * Ldap specific parsing. Received from the Kiv Admin project
 */
public class LdapParse {
    
    /**
     * Metod som tar en String som har $ som skiljetecken och ers�tter 
     * detta med radmatningstecken.
     * 
     * @param in
     *          String som inneh�ller $-tecken
     * @return
     *          String d�r $ �r utbytt mot \n
     */     
    public static String convertMultilineBeforeView(String in){
        if(in != null && in.length() > 0){
            in = in.replace('$', '\n');
        }
        return in;
    }

    /**
     * Metod som kontrollerar om en internetadress b�rjar med <code>http://</code> eller
     * <code>https://</code>. Om inte s�tter den dit det prefixet. 
     * @param in
     *      String-v�rde fr�n inmatningsf�lt
     * @return
     *      String med v�rde som har p�lagt prefix om v�rdet inte var tomt
     */
    public static String setInternetFormat(String in){
        if(in != null && in.length() > 5){
            if((in.indexOf("http://") == -1 || in.indexOf("https://") == -1) && in.indexOf(".") != -1){
                return "http://"+in;
            }
        }
        return "";
    }

    /**
     * H�mtar v�rde fr�n en specifik plats fr�n en Str�ng med separator av typen $
     * 
     * @param in
     *          - String som pekar p� det dummy f�lt man populerar med v�rde
     * @param vec
     *          - Vector    pos 0 String H�ller en pekare p� det dolda v�rdet i gui som egentligen �r kopplat till ett ldap-attribut.
     *                      pos 1 String H�ler position p� den substr�ng som ska h�mtas fr�n attributet som �r utpekat i vector(0).
     * @return
     *          -String     Det som str�ngen hade p� angiven position. ex: hej$p�$dig pos1 = p�
     */
    public static String getAddressPart(String in, Vector<String> vec){

        if(vec != null || vec.size() == 2){
            String valueFromGUI = vec.get(0).trim();
            int pos = 0;
            try{
                pos = Integer.parseInt(vec.get(1));
            }catch(Exception e){
                return "";
            }
            String[] tmp = valueFromGUI.split("\\$");
            if(tmp != null && tmp.length > 4){
                if (pos == 4){
                    return convertPostNumberBeforeView(tmp[pos]);
                }   
            }
            return tmp[pos].trim();
        }
        return "";    
    }

    /**
     * �vers�tter ett string nummer till dag i klartext
     * @param number
     *          - String nummer mellan 0-6 representerar varsin dag.
     * @return
     *          - String returnerar dag ex. "6" blir "Lördag".
     */
    public static String getDayName(String number){
        if(number != null && number.length()>0){
            int intNumber = 0;
            try{
                intNumber = Integer.parseInt(number);
            }catch(Exception e){
                return "";
            }
            switch(intNumber){
            case 1: return "Måndag";
            case 2: return "Tisdag";
            case 3: return "Onsdag";
            case 4: return "Torsdag";
            case 5: return "Fredag";
            case 6: return "Lördag";
            case 0: return "Sndag";
            default: return "";
            }
        }
        return "";
    }

    /**
     * Kontroll som ser till att 'i' skrivs med liten bokstav. f�r iNummer.
     * @param in
     *          - String v�rde som �r sparat i attributet <code>vgrInternalSedfInvoiceAddress</code>
     * @return
     *          - String formaterat v�rde.
     */         
    public static String iNumberCheck(String in){
        if(in != null && in.trim().length() > 0){
            return in.toLowerCase();
        }
        return "";
    }

    /**
     * Kontrollerar om angivet datum �r ett riktigt datum. 
     * @param in
     *          String 
     * @return om angivet datum �r ett riktigt datum
     */
    public static boolean isValidDate(String in){
        if(in != null && in.trim().length() > 0){
            String rx = "\\d\\d";
            String timeStr = "";
            Pattern pattern = Pattern.compile(rx);
            Matcher match= pattern.matcher(in);
            while(match.find()){
                timeStr += match.group();
            }
            if(timeStr.length() == 8){
                return isValidDateFormat(in,"yyyyMMdd");
            }
        }
        return false;
    }

    /**
     * Kontrollerar att ett datum med specificerat format �r riktigt
     * @param value
     *          -String datumet
     * @param pattern
     *          -String format p� v�rdet som ska kontrolleras
     * @return om datumet �r riktigt
     */
    public static boolean isValidDateFormat(String value, String pattern){
        try{
            Date sd = new SimpleDateFormat(pattern).parse(value);
            Format formatter = new SimpleDateFormat(pattern);
            if(value.equals(formatter.format(sd))){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Kovnerterar datum/tid till zuluformat
     * @param in
     *      -   String datumangivelse i olika format
     * @return
     *      -   Konverterat datum till zulutime format
     */
    public static String convertTimeToZulu(String in){
        if(in != null && in.trim().length() > 0){
            String rx = "\\d\\d";
            String timeStr = "";
            Pattern pattern = Pattern.compile(rx);
            Matcher match= pattern.matcher(in);
            while(match.find()){
                timeStr += match.group();
            }
            if(timeStr.length() == 14){
                if(isValidDateFormat(timeStr,"yyyyMMddHHmmss")){
                    return timeStr+"Z";
                }
            }else if(timeStr.length() == 12){
                if(isValidDateFormat(timeStr,"yyyyMMddHHmm")){
                    return timeStr+"00Z";
                }
            }else if(timeStr.length() == 8){
                if(isValidDateFormat(timeStr, "yyyyMMdd")){
                    return timeStr+"000000Z";
                }
            }
        }
        return "";
    }

    /**
     * Konverterar zulutime till l�sbart datum ex.<code>20070101121500Z</code> blir <code>2007-01-01 12:15</code>
     * @param in
     *          - String zulutime
     * @return
     *          - String konverterat datum och tid
     */
    
    public static String convertZuluToTime(String in){
        if(in != null && in.trim().length() == 15){
            if(isValidDateFormat(in.replace("Z",""),"yyyyMMddHHmmss")){
                return in.substring(0,4)+"-"+in.substring(4,6)+"-"+in.substring(6,8)+" "+in.substring(8,10)+":"+in.substring(10, 12);
            }
        }
        return "";
    }

    /**
     * Sorterar v�rden i array
     * @param in
     *          -String[] med v�rden
     * @return
     *          -String[] med v�den sorterade
     */
    public static String[] sortMultiValues(String[] in){
        if(in != null && in.length > 1){
            Arrays.sort(in);
        }
        return in;
    }

    /**
     * Konverterar postnummer s� de st�mmer �verens med hur det ska sparas i katalogen
     * @param in
     *          -String postnummer ex <code>123 45</code> 
     * @return
     *          -String postnummer  ex <code>12345</code>
     */
    public static String convertPostNumberBeforeSave(String in){
        if(in != null && in.length() > 0){
            in= in.replace(" ","");
            if(in.length() == 5){
                return in;
            }
        }
        return "";
    }
    /**
     * Konverterar postnummer s� de st�mmer �verens med hur det ska visas f�r anv�ndaren
     * @param in
     *          -String postnummer ex <code>12345</code>
     * @return
     *          -String postnummer ex <code>123 45</code>
     */
    public static String convertPostNumberBeforeView(String in){
        if(in != null && in.trim().length() > 3){
            return in.substring(0,3)+" "+in.substring(3);
        }
        return in;
    }

    /**
     * Visar objektstatus i textform
     * @param in
     *          -String objektstatus som nummerisk str�ng 
     * @return
     *          -String objektstatus som text
     */
    public static String vgrObjectStatusToText(String in){
        if(in != null){
            int val=0;
            try{
                val=Integer.parseInt (in);
                switch (val){
                case 0:     return "[Klarmarkerad]";
                case 10:    return "[Palett] Ny anställning";
                case 11:    return "[Palett] Ansvarsnummer";
                case 12:    return "[Palett] Titel";
                case 20:    return "Ny person";
                case 21:    return "[Västfolket] Namn";
                case 22:    return "[Västfolket] Sekretessmark.";
                case 30:    return "[Notes] E-postadress";
                default:    return "";
                }
            }catch(Exception e){
                return "";
            }
        }
        return "";
    }
/**
 * Konverterar peronnummer till hur det ska lagras i katalogen
 * @param in
 *      -String personnummer inskrivet fr�n gui
 * @return
 *      -String personnummer som det ska lagras i katalogen
 */
    public static String convertPersonNumberBeforeSave(String in){
        if(in != null){
            in = in.replace("-", "").replace(" ", "").trim();
            
        }
        return "";
    }
/**
 * Konverterar personnummer s� det �r indelat med f�delsedatum-nummer
 * @param in
 *          - String v�rde fr�n katalog
 * @return
 *          - String formaterat v�rde.
 */
    public static String convertPersonNumberBeforeView(String in){
        if(in != null && in.trim().length() == 12){
            return in.substring(0, 8)+" - "+in.substring(8);
        }
        return "";
    }
/**
 * Konverterar en eller flera rader med telefontider s� de blir mer l�ttl�sta
 * @param in
 *          -String[] med telefontider i ldap-format
 * @return
 *          -String[] med l�sbara telefontider
 */
    public static String[] convertTelephoneTime(String[] in){
        if(in != null && in.length > 0){
            for(int i = 0 ; i < in.length ; i++){
                in[i] = convertTelephoneTime(in[i]);
            }
        }
        return in;
    }

    /**
     * Escape av LDAP-specifika tecken som kommatecken och backslash
     * @param in
     *          - String v�rde som ska kontrolleras innan det sparas
     * @return
     *          - String med bortescapade tecken.
     */
    public static String escapeDN(String in){
        in = in.trim(); 
        final StringBuffer result = new StringBuffer();

        final StringCharacterIterator iterator = new StringCharacterIterator(in);
        char character =  iterator.current();
        while (character != StringCharacterIterator.DONE ){
            if (character == ',') {
                result.append("\\,");
            }else if (character == '\\') {
                result.append("\\\\");
            }else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }   

    /**
     * Konverterar en String med telefontid i ldap-format.  
     * @param in
     *      - String en telefontid i ldap-format
     * @return
     *      - String med telefontid som �r mer l�ttl�st.
     */
    public static String convertTelephoneTime(String in){
        if(in != null && in.trim().length() > 0 && in.indexOf("#")!= -1){
            try{
                String[] tmp = in.trim().split("#");
                String tmpStr = tmp[0].replace("-", "").trim();
                String startDay = getDayName(tmpStr.substring(0,1));
                String endDay = getDayName(tmpStr.substring(1));
                if(startDay.equals(endDay)){
                    return startDay+" "+tmp[1]+" "+tmp[2];
                }else{
                    return startDay+"-"+endDay+" "+tmp[1]+" "+tmp[2];
                }
            }catch(Exception e){
                return "";
            }
        }
        return "";
    }
    
    public static String showLoginStatus(String in){    
            if(in != null && in.equalsIgnoreCase("false")){
                return "[Login PÅ]";
            }else{
                return "[Login AV]";
            }       
    }
    
	/**
	 * Filters the query, prevents ldap injection
	 * @param filter
	 * @see http://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
	 * @return escaped ldap search filter
	 */
	public static final String escapeLDAPSearchFilter(String filter) {
		if (filter == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < filter.length(); i++) {
			char curChar = filter.charAt(i);
			switch (curChar) {
			case '\\':
				sb.append("\\5c");
				break;
			case '*':
				sb.append("\\2a");
				break;
			case '(':
				sb.append("\\28");
				break;
			case ')':
				sb.append("\\29");
				break;
			case '\u0000':
				sb.append("\\00");
				break;
			default:
				sb.append(curChar);
			}
		}
		return sb.toString();
	}
}
