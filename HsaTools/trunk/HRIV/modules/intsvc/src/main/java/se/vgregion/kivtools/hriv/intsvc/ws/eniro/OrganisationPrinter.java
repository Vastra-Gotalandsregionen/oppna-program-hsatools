package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;

/**
 * Used for print out a visualized view of an organisation tree.
 * 
 * @author David Bennehult & Joakim Olsson.
 * 
 */
public class OrganisationPrinter {

  private Log logger = LogFactory.getLog(OrganisationPrinter.class);
  private int unitCount;

  /**
   * Print organisastion tree.
   * 
   * @param organization organisation object to print.
   * @param writer to print the content to.
   */
  public void printOrganisation(Organization organization, Writer writer) {
    printUnits(organization.getUnit(), writer, 0);
    try {
      writer.write("\n");
      writer.write("Total units in organisation tree: " + unitCount);
    } catch (IOException e) {
      logger.error("Failed to write out content", e);
    }
  }

  /**
   * Prints recursively the unit tree.
   * 
   * @param units units to print out.
   * @param writer to write unit tree to.
   * @param level - tree level.
   */
  public void printUnits(List<Unit> units, Writer writer, int level) {
    try {
      for (Unit unit : units) {
        for (int i = 0; i < level; i++) {
          writer.write("\t\t");
        }
        String id = "";
        if (unit.getId() != null) {
          id = ": " + unit.getId();
        }
        writer.write(unit.getName() + id + "\n");
        printUnits(unit.getUnit(), writer, level + 1);
        unitCount++;
      }
    } catch (IOException e) {
      logger.error("Failed to write out content", e);
    }
  }
}
