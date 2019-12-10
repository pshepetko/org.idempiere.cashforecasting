/**
 * This file is part of iDempiere ERP <http://www.idempiere.org>.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Copyright (c) 2016 Saúl Piña <sauljabin@gmail.com>.
 */

package org.idempiere.cashforecasting.event;

import java.io.IOException;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.CLogger;
import org.idempiere.cashforecasting.base.CustomEventHandler;
import org.idempiere.cashforecasting.util.PluginInfo;

/**
 * This is a example of Event Handler
 */
public class EPrintPluginInfo extends CustomEventHandler {

	private final static CLogger log = CLogger.getCLogger(EPrintPluginInfo.class);

	@Override
	protected void doHandleEvent() {

		try {
			PluginInfo bundleInfo = PluginInfo.getInstance();
			log.info("ID: " + bundleInfo.getBundleID());
			log.info("Name: " + bundleInfo.getBundleName());
			log.info("Vendor: " + bundleInfo.getBundleVendor());
			log.info("Version: " + bundleInfo.getBundleVersion());
			throw new AdempiereException("Name: " + bundleInfo.getBundleName());
		} catch (IOException e) {
			throw new AdempiereException("Error in BundleInfo", e);
		}

	}

}
