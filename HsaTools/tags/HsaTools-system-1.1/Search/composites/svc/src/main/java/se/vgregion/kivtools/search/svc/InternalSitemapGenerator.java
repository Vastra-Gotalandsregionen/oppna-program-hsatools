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
 */
package se.vgregion.kivtools.search.svc;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.sitemap.TChangeFreq;
import org.sitemap.TUrl;
import org.sitemap.Urlset;

/**
 * Implementation of SitemapGenerator for internal use. The generated XML contains a private namespace where additional attributes can be added.
 */
public class InternalSitemapGenerator implements SitemapGenerator {
  @Override
  public String generate(List<SitemapEntry> sitemapEntries) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance("org.sitemap:se.vgregion.kivtools.svc.sitemap");
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "iso-8859-1");
      StringWriter writer = new StringWriter();
      Urlset urlset = new Urlset();

      for (SitemapEntry entry : sitemapEntries) {
        TUrl url = new TUrl();
        url.setLoc(entry.getLocation());
        url.setLastmod(entry.getLastModified());
        url.setChangefreq(TChangeFreq.fromValue(entry.getChangeFrequency()));
        url.setPriority(BigDecimal.valueOf(0.5));

        for (Object extraInformation : entry) {
          url.getAny().add(extraInformation);
        }

        urlset.getUrl().add(url);
      }

      marshaller.marshal(urlset, writer);

      return writer.toString();
    } catch (JAXBException e) {
      throw new RuntimeException("Unable to create XML from provided content", e);
    }
  }
}
