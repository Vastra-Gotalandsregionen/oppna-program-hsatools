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
package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.Name;

class NameMock implements Name {

  String value;

  NameMock(String value) {
    this.value = value;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public Name add(String comp) throws InvalidNameException {
    return new NameMock(comp);
  }

  @Override
  public Name add(int posn, String comp) throws InvalidNameException {
    return null;
  }

  @Override
  public Name addAll(Name suffix) throws InvalidNameException {
    return null;
  }

  @Override
  public Name addAll(int posn, Name n) throws InvalidNameException {
    return null;
  }

  @Override
  public int compareTo(Object obj) {
    return 0;
  }

  @Override
  public boolean endsWith(Name n) {
    return false;
  }

  @Override
  public String get(int posn) {
    return null;
  }

  @Override
  public Enumeration<String> getAll() {
    return null;
  }

  @Override
  public Name getPrefix(int posn) {
    return null;
  }

  @Override
  public Name getSuffix(int posn) {
    return null;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Object remove(int posn) throws InvalidNameException {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean startsWith(Name n) {
    return false;
  }

  @Override
  public Object clone() {
    return null;
  }

  @Override
  public String toString() {
    return value;
  }
}