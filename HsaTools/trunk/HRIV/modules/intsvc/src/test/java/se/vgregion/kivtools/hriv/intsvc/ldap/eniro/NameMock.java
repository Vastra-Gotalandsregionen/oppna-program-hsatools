package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

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