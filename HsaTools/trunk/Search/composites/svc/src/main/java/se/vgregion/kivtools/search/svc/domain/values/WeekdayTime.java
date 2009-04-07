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
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;

/**
 * Represents a time intervall. Used for Telephone times and hours.
 * 
 * <h4>Katalogformat</h4>
 * <li><code>1-5#08:30#10:00</code></li>
 * <li><code>2-2#07:15#16:45</code></li>
 * 
 * <h4>Presented format</h4>
 * <br>
 * <li><code>Mondag-Fredag 08:30-10:00</code></li>
 * <li><code>Tisdag 07:15-16:45</code></li>
 * 
 * @author JENJA13
 * @author ULFSA3
 * 
 */
public class WeekdayTime implements Comparable<WeekdayTime>, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Minv�rde f�r veckodagskod, f.n. 0.
     */
    public static final int MIN_DAY_CODE = 0;
    /**
     * Maxv�rde f�r veckodagskod, f.n. 6.
     */
    public static final int MAX_DAY_CODE = 7;

    /**
     * Minv�rde f�r timmar, f.n. 0.
     */
    public static final int MIN_HOUR = 0;
    /**
     * Maxv�rde f�r timmar, f.n. 23.
     */
    public static final int MAX_HOUR = 23;
    /**
     * Minv�rde f�r timmar, f.n. 0.
     */
    public static final int MIN_MINUTE = 0;
    /**
     * Minv�rde f�r minuter, f.n. 59.
     */
    public static final int MAX_MINUTE = 59;

    private int endDay;
    private int endHour;

    private int endMin;
    private int startDay;

    private int startHour;
    private int startMin;

    /**
     * Skapar en nytt tidsintervall.
     * 
     * @param startDay
     *            kod f�r startveckodag.
     * @param endDay
     *            kod f�r slutveckodag
     * @param startHour
     *            starttimme
     * @param startMin
     *            startminut
     * @param endHour
     *            sluttimme
     * @param endMin
     *            slutminut
     * @throws InvalidFormatException
     *             om n�gon av inparametrarna ligger utanf�r till�tna intervall.
     */
    public WeekdayTime(int startDay, int endDay, int startHour, int startMin,
            int endHour, int endMin) throws InvalidFormatException {
        this.setStartDay(startDay);
        this.setEndDay(endDay);
        this.setStartHour(startHour);
        this.setStartMin(startMin);
        this.setEndHour(endHour);
        this.setEndMin(endMin);
    }

    public static List<WeekdayTime> createWeekdayTimeList(List<String> saveValues) {
        List<WeekdayTime> timeList = new ArrayList<WeekdayTime>();
        if (saveValues!=null) {
            for (String telephoneTime : saveValues) {
                try {
                    timeList.add(new WeekdayTime(telephoneTime));
                } catch (InvalidFormatException e) {}
            }
        }
        // Sort list before returning it. Shall be sorted on WeekdayTime.startDay
        List<WeekdayTime> returnTimeList = new ArrayList<WeekdayTime>();
        WeekdayTime currentWDT = null;
        int startDay = 8;
        while (!timeList.isEmpty()) {
        	for (WeekdayTime wdt : timeList) {
        		if (wdt.getStartDay() < startDay) {
        			currentWDT = wdt;
        			startDay = wdt.getStartDay();
        		}
        	}
        	returnTimeList.add(currentWDT);
        	timeList.remove(currentWDT);
        	startDay = 8;
        }
        return returnTimeList;
    }
    /**
     * Skapar en nytt tidsintervall utifr�n en str�ng p� katalogformat.
     * 
     * @param saveValue
     * 
     * @throws InvalidFormatException
     *             om n�gon av inparametrarna ligger utanf�r till�tna intervall.
     */
    public WeekdayTime(String saveValue) throws InvalidFormatException {
        this(0, 0, 0, 0, 0, 0);

        String[] splits = saveValue.split("#");
        if (3 == splits.length) {
            String[] daySplits = splits[0].split("-");

            if (2 == daySplits.length) {
                this.setStartDay(daySplits[0]);
                this.setEndDay(daySplits[1]);
            } else {
                throw new InvalidFormatException("Felaktigt antal -");
            }

            String[] startTimeSplits = splits[1].split(":");
            if (2 == startTimeSplits.length) {
                this.setStartHour(startTimeSplits[0]);
                this.setStartMin(startTimeSplits[1]);
            } else {
                throw new InvalidFormatException("Felaktigt antal :");
            }

            String[] endTimeSplits = splits[2].split(":");
            if (2 == endTimeSplits.length) {
                this.setEndHour(endTimeSplits[0]);
                this.setEndMin(endTimeSplits[1]);
            } else {
                throw new InvalidFormatException("Felaktigt antal :");
            }

        } else {
            throw new InvalidFormatException("Felaktigt antal #");
        }

    }

    /**
     * Skapar en nytt tidsintervall.
     * 
     * @param startDay
     *            kod f�r startveckodag.
     * @param endDay
     *            kod f�r slutveckodag
     * @param startHour
     *            starttimme
     * @param startMin
     *            startminut
     * @param endHour
     *            sluttimme
     * @param endMin
     *            slutminut
     * 
     * @throws InvalidFormatException
     *             om n�gon av inparametrarna ligger utanf�r till�tna intervall.
     */
    public WeekdayTime(String startDay, String endDay, String startHour,
            String startMin, String endHour, String endMin)
            throws InvalidFormatException {
        this.setStartDay(startDay);
        this.setEndDay(endDay);
        this.setStartHour(startHour);
        this.setStartMin(startMin);
        this.setEndHour(endHour);
        this.setEndMin(endMin);
    }

    /**
     * J�mf�r detta tidsintervall med det specificerade tidsintervallet f�r
     * ordning. Returnerar ett negativt heltal, noll, eller ett positivt heltal
     * om detta tidsintervall ligger f�re, �r lika, eller ligger efter det
     * specificerade tidsintervallet.
     * 
     * <p>
     * 
     * 
     * </p>
     * 
     * @param other
     *            tidsintervallet som skall j�mf�ras
     * @return ett negativt heltal, noll, eller ett positivt heltal om detta
     *         tidsintervall �r ligger f�re, �r lika med eller ligger efter det
     *         specificerade tidsintervallet.
     */
    public int compareTo(WeekdayTime other) {
        return this.getSaveValue().compareTo(other.getSaveValue());

    }

    /**
     * Kontrollerar om tv� tidsintervall �r lika. Tv� tidsintervall �r lika om
     * deras dagar och tider �r samma.
     * 
     * @param other
     * @return
     */
    public boolean equals(WeekdayTime other) {
        return (this.getSaveValue().equals(other.getSaveValue()));
    }

    /**
     * Returnerar en hashkod f�r tidsintervallet. �verskuggingen beror p� att
     * {@link #equals(WeekdayTime)} �verskuggas och att tv� objekt som �r lika
     * enligt equals skall generera samma hashkod.
     * 
     * @return ett hashkodsv�rde f�r detta objekt.
     */
    public int hashCode() {
        return this.getSaveValue().hashCode();
    }

    /**
     * 
     * H�mtar representation av tidsintervall till format som presenteras f�r
     * anv�ndaren.
     * 
     * 
     * 
     * @return
     */
    public String getDisplayValue() {

        // If open all the time, return "Dygnet runt"
		if (endDay == 7 && endHour == 0 && endMin == 0 && endMin == 0
				&& startDay == 1 && startHour == 0 && startMin == 0) {
			return "Dygnet runt";
		}
        
		String returnString = "";
        returnString += WeekdayTime.getDayName(this.getStartDay());

        if (this.getStartDay() != this.getEndDay()) {
            // om �ver flera dagar...
            returnString += "-" + WeekdayTime.getDayName(this.getEndDay());
        }

        returnString += " ";
        returnString += this.getTwoDigitNumber(this.getStartHour());
        returnString += ":";
        returnString += this.getTwoDigitNumber(this.getStartMin());
        returnString += "-";
        returnString += this.getTwoDigitNumber(this.getEndHour());
        returnString += ":";
        returnString += this.getTwoDigitNumber(this.getEndMin());

        return returnString;

    }

    /**
     * H�mtar kod f�r slut-veckodag. {@link Parse#getDayName(int)}.
     * 
     * @return kod f�r slut-veckodag
     */
    public int getEndDay() {
        return endDay;
    }

    /**
     * H�mtar sluttidens timmesdel.
     * 
     * @return sluttidens timmesdel
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * H�mtar sluttidens minutdel.
     * 
     * @return sluttidens minutdel
     */
    public int getEndMin() {
        return endMin;
    }

    /**
     * H�mtar str�ng med hur tidsintervallet skall lagras i katalogen.
     * 
     * @return str�ng med tidintervallet s� som det sparas i katalogen. Exempel:
     *         <code>1-5#08:15#16:30</code>.
     */
    public String getSaveValue() {
        return this.getStartDay() + "-" + this.getEndDay() + "#"
                + this.getTwoDigitNumber(this.getStartHour()) + ":"
                + this.getTwoDigitNumber(this.getStartMin()) + "#"
                + this.getTwoDigitNumber(this.getEndHour()) + ":"
                + this.getTwoDigitNumber(this.getEndMin());
    }

    /**
     * H�mtar kod f�r start-veckodag. {@link Parse#getDayName(int)}.
     * 
     * @return kod f�r start-veckodag
     */
    public int getStartDay() {
        return startDay;
    }

    /**
     * H�mtar starttidens timmesdel.
     * 
     * @return starttidens timmesdel
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * H�mtar starttidens minutdel.
     * 
     * @return starttidens minutdel
     */
    public int getStartMin() {
        return startMin;
    }

    /**
     * S�tter kod f�r slut-veckodagen. Kontrollerar s� den �r i intervallet
     * {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
     * 
     * @param endDay
     * 
     */
    public void setEndDay(int endDay) throws InvalidFormatException {
        if ((MIN_DAY_CODE <= endDay) && (endDay <= MAX_DAY_CODE)) { 
            this.endDay = mapSunday(endDay);
        } else {
            throw new InvalidFormatException("");
        }

    }

    /**
     * S�tter kod f�r slut-veckodagen. Kontrollerar s� den �r i intervallet
     * {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
     * 
     * @param endDay
     * 
     */
    public void setEndDay(String endDay) throws InvalidFormatException {
        int parseDay = Integer.parseInt(endDay);
        this.setEndDay(parseDay);
    }

    /**
     * S�tter sluttidens timmesdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_HOUR}-{@link #MAX_HOUR}.
     * 
     * @param endHour
     * 
     */
    public void setEndHour(int endHour) throws InvalidFormatException {
        if ((MIN_HOUR <= endHour) && (endHour <= MAX_HOUR)) {
            this.endHour = endHour;
        } else {
            throw new InvalidFormatException("");
        }
    }

    /**
     * S�tter sluttidens timmesdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_HOUR}-{@link #MAX_HOUR}.
     * 
     * @param endHour
     * 
     */
    public void setEndHour(String endHour) throws InvalidFormatException {
        int parse = Integer.parseInt(endHour);
        this.setEndHour(parse);
    }

    /**
     * S�tter sluttidens minutdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
     * 
     * @param endMin
     * 
     */
    public void setEndMin(int endMin) throws InvalidFormatException {
        if ((MIN_MINUTE <= endMin) && (endMin <= MAX_MINUTE)) {
            this.endMin = endMin;
        } else {
            throw new InvalidFormatException("");
        }

    }

    /**
     * S�tter sluttidens minutdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
     * 
     * @param endMin
     * 
     */
    public void setEndMin(String endMin) throws InvalidFormatException {
        int parse = Integer.parseInt(endMin);
        this.setEndMin(parse);
    }

    /**
     * S�tter kod f�r start-veckodagen. Kontrollerar s� den �r i intervallet
     * {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
     * 
     * @param startDay
     * 
     */
    public void setStartDay(int startDay) throws InvalidFormatException {
        if ((MIN_DAY_CODE <= startDay) && (startDay <= MAX_DAY_CODE)) {
            this.startDay = mapSunday(startDay);
        } else {
            throw new InvalidFormatException("");
        }
    }

    /**
     * S�tter kod f�r start-veckodagen. Kontrollerar s� den �r i intervallet
     * {@link #MIN_DAY_CODE}-{@link #MAX_DAY_CODE}.
     * 
     * @param startDay
     * 
     */
    public void setStartDay(String startDay) throws InvalidFormatException {

        int parseDay = Integer.parseInt(startDay);
        this.setStartDay(parseDay);

    }

    /**
     * S�tter starttidens timmesdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_HOUR}-{@link #MAX_HOUR}.
     * 
     * @param startHour
     * 
     */
    public void setStartHour(int startHour) throws InvalidFormatException {
        if ((MIN_HOUR <= startHour) && (startHour <= MAX_HOUR)) {
            this.startHour = startHour;
        } else {
            throw new InvalidFormatException("");
        }
    }

    /**
     * S�tter starttidens timmesdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_HOUR}-{@link #MAX_HOUR}.
     * 
     * @param startHour
     * 
     */
    public void setStartHour(String startHour) throws InvalidFormatException {
        int parse = Integer.parseInt(startHour);
        this.setStartHour(parse);
    }

    /**
     * S�tter starttidens minutdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
     * 
     * @param startMin
     * 
     */
    public void setStartMin(int startMin) throws InvalidFormatException {
        if ((MIN_MINUTE <= startMin) && (startMin <= MAX_MINUTE)) {
            this.startMin = startMin;
        } else {
            throw new InvalidFormatException("");
        }

        this.startMin = startMin;
    }

    /**
     * S�tter starttidens minutdel. Kontrollerar s� den �r i intervallet
     * {@link #MIN_MINUTE}-{@link #MAX_MINUTE}.
     * 
     * @param startMin
     * 
     */
    public void setStartMin(String startMin) throws InvalidFormatException {
        int parse = Integer.parseInt(startMin);
        this.setStartMin(parse);
    }

    /**
     * Mappar om det gamla v�rdet f�r s�ndagar - 0 till det nya - 7. Denna
     * funktion kan tas bort n�r katalogen blivit st�dad.
     * 
     * @param day
     *            nummer som representerar dagen
     * @return om day �r 0 returneras 7, annars returneras day
     */
    private int mapSunday(int day) {
        if (0 == day)
            return 7;
        return day;
    }

    /**
     * �vers�tter ett nummer till dag i klartext. 0 och 7 �vers�tts b�da till S�ndag.
     * 
     * @param day -
     *            int day mellan 0-7 representerar varsin dag.
     * @return - String returnerar dag ex. "6" blir "L�rdag".
     */
    public static String getDayName(int day) {

        switch (day) {
        case 1:
            return "M\u00E5ndag";
        case 2:
            return "Tisdag";
        case 3:
            return "Onsdag";
        case 4:
            return "Torsdag";
        case 5:
            return "Fredag";
        case 6:
            return "L\u00F6rdag";
        case 7: 
            return "S\u00F6ndag";
        case 0:
            return "S\u00F6ndag";
        default:
            return "";
        }
    }

    /**
     * Konverterar siffror, t ex timmar, s� att de visas med tv� siffror. T ex
     * blir 9 "09".
     * 
     * @param number
     * @return en str�ng med tv� siffor.
     */
    private String getTwoDigitNumber(int number) {
        if (number > 9) {
            return Integer.toString(number);
        }
        return "0" + Integer.toString(number);

    }
}
