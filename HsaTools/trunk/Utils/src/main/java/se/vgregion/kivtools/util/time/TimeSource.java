package se.vgregion.kivtools.util.time;

/**
 * TimeSource interface used to provide different time sources for TimeUtil.
 * 
 * @author Joakim Olsson & David Bennehult
 */
public interface TimeSource {
  /**
   * Retrieves current date/time of the time source in milliseconds.
   * @return Current date/time of the time source in milliseconds.
   */
  public long millis();
}
