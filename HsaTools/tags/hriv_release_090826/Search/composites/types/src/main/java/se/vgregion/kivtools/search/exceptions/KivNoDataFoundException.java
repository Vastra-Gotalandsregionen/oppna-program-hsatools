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
package se.vgregion.kivtools.search.exceptions;

import java.io.Serializable;


/**
 * @author Hans Gyllensten - KnowIT
 *
 */
@SuppressWarnings("serial")
public class KivNoDataFoundException extends KivException implements Serializable{

    /**
     * @param message
     */
    public KivNoDataFoundException(String message) {
        super(message);
    }

    public KivNoDataFoundException() {
        super("S\u00F6kningen resulterade inte i n\u00E5gra tr\u00E4ffar.");
    }
}
