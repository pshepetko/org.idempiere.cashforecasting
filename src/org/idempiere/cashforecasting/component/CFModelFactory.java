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

package org.idempiere.cashforecasting.component;

import org.idempiere.cashforecasting.base.CustomModelFactory;

/**
 * Model Factory
 */
public class CFModelFactory extends CustomModelFactory {

	/**
	 * For initialize class. Register the models to build
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	registerTableModel(MTableExample.Table_Name, MTableExample.class);
	 * }
	 * </pre>
	 */
	@Override
	protected void initialize() {
 		registerTableModel(org.idempiere.cashforecasting.model.MCFCashForecasting.Table_Name, org.idempiere.cashforecasting.model.MCFCashForecasting.class);
 		registerTableModel(org.idempiere.cashforecasting.model.MCFPeriod.Table_Name, org.idempiere.cashforecasting.model.MCFPeriod.class);
 		registerTableModel(org.idempiere.cashforecasting.model.MCFPeriodDefinition.Table_Name, org.idempiere.cashforecasting.model.MCFPeriodDefinition.class);
 		registerTableModel(org.idempiere.cashforecasting.model.MCFTemplate.Table_Name, org.idempiere.cashforecasting.model.MCFTemplate.class);
 		registerTableModel(org.idempiere.cashforecasting.model.MCFTemplateLine.Table_Name, org.idempiere.cashforecasting.model.MCFTemplateLine.class);
	}

}
