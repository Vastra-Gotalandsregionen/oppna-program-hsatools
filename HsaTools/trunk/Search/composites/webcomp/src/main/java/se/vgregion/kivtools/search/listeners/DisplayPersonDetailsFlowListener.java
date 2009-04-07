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
package se.vgregion.kivtools.search.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Anders Asplund / KnowIT
 *
 * Used when persondetails should be viewed via a vgrId
 */
public class DisplayPersonDetailsFlowListener extends FlowExecutionListenerAdapter {
    Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void sessionStarting(RequestContext context,
            FlowDefinition definition, MutableAttributeMap input) {
        String vgrId = context.getRequestParameters().get("vgrId");
        logger.info("DisplayPersonDetailsFlowListener.sessionStarting() -> vgrId: " + vgrId);
        input.put("vgrId", vgrId);
    }

}
