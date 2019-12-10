/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2012 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): victor.perez@e-evolution.com www.e-evolution.com   		  *
 *****************************************************************************/

package org.idempiere.cashforecasting.process;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MPeriod;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.idempiere.cashforecasting.base.CustomProcess;
import org.idempiere.cashforecasting.model.MCFPeriod;
import org.idempiere.cashforecasting.model.MCFPeriodDefinition;
import org.compiere.model.MYear;

/**
 * Creates period calendar based on the Period Definition using the specified
 * start date. if start date is not specified, 1st of Jan will be used. The
 * period name will be generated from the start date of each period using the
 * Java SimpleDataFormat pattern provided.
 * 
 * @author victor.perez, pshepetko
 * 
 */
public class CreatePeriods extends CustomProcess {
	private int p_MCFPeriodDefinition_ID =0;
	private int p_NoPeriods = 0;
	private Timestamp p_StartDate;
	private String p_DateFormat;
	private String result;
	
	MCFPeriodDefinition m_perioddef = null;

	/**
	 * Prepare
	 */
	protected void prepare() {

		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("StartDate"))
				p_StartDate = (Timestamp) para.getParameter();
			else if (name.equals("PeriodNo"))
				p_NoPeriods = para.getParameterAsInt();
			else if (name.equals("DateFormat"))
				p_DateFormat = (String) para.getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_MCFPeriodDefinition_ID =  getRecord_ID();
	} // prepare

	/**
	 * Process
	 * 
	 * @return info
	 * @throws Exception
	 */
	protected String doIt() throws Exception {
				
		m_perioddef = new MCFPeriodDefinition(getCtx(), p_MCFPeriodDefinition_ID, get_TrxName());
				
		if (p_MCFPeriodDefinition_ID == 0
				|| m_perioddef.get_ID() != p_MCFPeriodDefinition_ID)
			throw new AdempiereUserError(
					"@NotFound@: @M_Year_ID@ - "
							+ p_MCFPeriodDefinition_ID);
		log.info(m_perioddef.toString());
		
		DB.executeUpdateEx("DELETE FROM C_CF_Period WHERE C_CF_PeriodDefinition_ID="+p_MCFPeriodDefinition_ID,get_TrxName());

		//
		if (createPeriods(null, p_StartDate, p_DateFormat,p_NoPeriods))
			return "@OK@"+result;
		
		return "@Error@";
	} // doIt
	
	
	/**
	 * @param locale
	 *            locale
	 * @param startDate
	 *            first day of the calendar year
	 * @param dateFormat
	 *            SimpleDateFormat pattern for generating the period names.
	 * @return true if created
	 */
	public boolean createPeriods(Locale locale, Timestamp startDate,
			String dateFormat, int noPeriods) {

		if (locale == null) {
			MClient client = MClient.get(getCtx());
			locale = client.getLocale();
		}

		if (locale == null && Language.getLoginLanguage() != null)
			locale = Language.getLoginLanguage().getLocale();
		if (locale == null)
			locale = Env.getLanguage(getCtx()).getLocale();
		//
		SimpleDateFormat formatter;
		if (dateFormat == null || dateFormat.equals("")) {
			if (noPeriods > 52)
			 	dateFormat = "dd-MMM-yy";
			if (noPeriods == 52)
				dateFormat = "ww-yy";
			if (noPeriods == 12)
				dateFormat = "MMM-yy";
		}
	
		formatter = new SimpleDateFormat(dateFormat, locale);

		//
		int year = Integer.parseInt(m_perioddef.getCalendarYear());
		GregorianCalendar cal = new GregorianCalendar(locale);
		if (startDate != null) {
			cal.setTime(startDate);
			if (cal.get(Calendar.YEAR) != year) // specified start date takes
												// precedence in setting year
				year = cal.get(Calendar.YEAR);

		} else {
			cal.set(Calendar.YEAR, year);
			if (noPeriods == 12)
				cal.set(Calendar.MONTH, 0);
			if (noPeriods == 52)
				cal.set(Calendar.WEEK_OF_YEAR, 1);

			cal.set(Calendar.DAY_OF_MONTH, 1);

		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		//
		for (int p = 0; p < noPeriods; p++) {

			Timestamp start = new Timestamp(cal.getTimeInMillis());
			
			// get last day of same month
			if (noPeriods == 52)
				cal.add(Calendar.WEEK_OF_YEAR, 1);
			if (noPeriods == 12)
				cal.add(Calendar.MONTH, 1);
			
 			if (noPeriods ==365 || noPeriods ==366)
				cal.add(Calendar.DAY_OF_YEAR, 2);

			cal.add(Calendar.DAY_OF_YEAR, -1);
 			
			Timestamp end = new Timestamp(cal.getTimeInMillis());
			
			String name = formatter.format(end);
			//
//			MPeriod period = MPeriod.findByCalendar(getCtx(), start, m_year.getC_Calendar_ID(), get_TrxName());
			MCFPeriod period =null;
			if (period == null) {
				period = new MCFPeriod(getCtx(), 0, get_TrxName());
				period.setC_CF_PeriodDefinition_ID(p_MCFPeriodDefinition_ID);
				//period = new MPeriod(m_year, p + 1, name, start, end);
				//period.setC_Year_ID(m_year.getC_Year_ID());
			}

			period.setPeriodNo(p + 1);
			period.setName(name);
			period.setStartDate(start);
			period.setEndDate(end);
			period.saveEx(get_TrxName()); // Creates Period Control
			// get first day of next month
			if (noPeriods == 52)
				cal.add(Calendar.DAY_OF_WEEK, 1);
			if (noPeriods == 12)
				cal.add(Calendar.DAY_OF_YEAR, 1);
		}

		return true;

	} // createStdPeriods
	
} // Create Periods
