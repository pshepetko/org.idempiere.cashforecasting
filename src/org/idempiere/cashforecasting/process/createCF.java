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

package org.idempiere.cashforecasting.process;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MQuery;
import org.compiere.model.MRequisition;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.MTable;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportCtl;
import org.compiere.print.ReportEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.idempiere.cashforecasting.base.CustomProcess;
import org.idempiere.cashforecasting.model.MCFCashForecasting;
import org.idempiere.cashforecasting.model.MCFPeriod;
import org.idempiere.cashforecasting.model.MCFTemplateLine;

/**
 * Process create Cash Forecasting
 */
public class createCF extends CustomProcess {

	private String processVerNo = "[v.1.01] ";

	private int p_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());

	private static String result_msg = "Cash Forecasting Line created: ";

	/** Date From */
	private Timestamp p_DateFrom = null;
	/** Date To */
	private Timestamp p_DateTo = null;

	private int p_CF_PeriodDefinition_ID = 0;
	private int p_CF_Template_ID = 0;
	private boolean p_DeleteOld;
	private BigDecimal amt_balance = Env.ZERO;
	private int count_period = 0;
	private BigDecimal count_seq = Env.ONE;

	private int count_order = 0;
	private int count_invoice = 0;
	private int count_payment = 0;
	private int count_requisition = 0;

	private int period_id = 0;

	private int AD_PInstance_ID = 0;
	private Trx currentTrx = null;

	protected void prepare() {
		// Parameter
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null)
				;
			else if (name.equals("DatePromised")) {
				p_DateFrom = (Timestamp) para[i].getParameter();
				p_DateTo = (Timestamp) para[i].getParameter_To();
			} else if (name.equals("C_CF_PeriodDefinition_ID"))
				p_CF_PeriodDefinition_ID = para[i].getParameterAsInt();

			else if (name.equals("C_CF_Template_ID"))
				p_CF_Template_ID = para[i].getParameterAsInt();
			
			else if (name.equals("DeleteOld")) 
				p_DeleteOld = para[i].getParameterAsBoolean();
			

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	protected String doIt() throws Exception {
		result_msg = "";

		currentTrx = Trx.get(get_TrxName(), false);
		
		AD_PInstance_ID = getAD_PInstance_ID();

		// Delete Old Records
		if (p_DeleteOld)
			DB.executeUpdateEx("DELETE FROM C_CashForecasting;", currentTrx.getTrxName());

		// get Open Balance
		// amt_balance=addBalance(p_DateFrom, "OB",Env.ZERO);

		// set first period
		count_period = 1;

		// get Periods
		ArrayList<Object> parameters2 = new ArrayList<Object>();
		StringBuffer whereClause2 = new StringBuffer(
				" AD_Client_ID=?  AND C_CF_PeriodDefinition_ID=? AND isActive='Y' AND StartDate >=? AND EndDate <=?");
		parameters2.add(p_AD_Client_ID);
		parameters2.add(p_CF_PeriodDefinition_ID);
		parameters2.add(p_DateFrom);
		parameters2.add(p_DateTo);
		List<MCFPeriod> periods = new Query(getCtx(), MCFPeriod.Table_Name, whereClause2.toString(),currentTrx.getTrxName())
				.setParameters(parameters2).list();
		for (MCFPeriod period : periods) {

			// get Open Balance
			if (period_id == 0)
				amt_balance = addBalance(p_DateFrom, "OB", Env.ZERO);

			period_id = period.getC_CF_Period_ID();

			// set Projected Balance for Period
			addBalance(period.getStartDate(), "POBP", count_seq);

			ArrayList<Object> parameters1 = new ArrayList<Object>();
			StringBuffer whereClause1 = new StringBuffer(" AD_Client_ID=?  AND C_CF_Template_ID=? AND isActive='Y' ");
			parameters1.add(p_AD_Client_ID);
			parameters1.add(p_CF_Template_ID);
			List<MCFTemplateLine> tlines = new Query(getCtx(), MCFTemplateLine.Table_Name, whereClause1.toString(),
					get_TrxName()).setParameters(parameters1).setOrderBy("Sequence").list();
			for (MCFTemplateLine tline : tlines) {
				count_seq = tline.getSequence();
				if (tline.getAD_Table().getTableName().equals("C_Order")
						|| tline.getAD_Table().getTableName().equals("C_OrderLine"))
					getOrder(tline, period);

				else if (tline.getAD_Table().getTableName().equals("C_Invoice")
						|| tline.getAD_Table().getTableName().equals("C_InvoiceLine"))
					getInvoice(tline, period);

				else if (tline.getAD_Table().getTableName().equals("C_Payment"))
					getPayment(tline, period);

				else if (tline.getAD_Table().getTableName().equals("M_Requisition")
						|| tline.getAD_Table().getTableName().equals("M_RequisitionLine"))
					getRequisition(tline, period);

				// else if (tline.getAD_Table().getTableName().equals("C_CashPlan"))
			}

			// set Projected Balance for Period
			count_seq = count_seq.add(Env.ONE);
			addBalance(period.getEndDate(), "PEBP", count_seq);

			count_period++;
		}
		
		// set Projected Ending Balance
		addBalance(p_DateTo, "PEB", count_seq);
		
		currentTrx.commit(true);
		
		// Get Format & Data
		ReportEngine re = this.getReportEngine("Cash Forecasting","C_CashForecasting");
		
		if(re == null )
		{
			return "";
		}
		ReportCtl.preview(re);
		re.print(); // prints only original

		return processVerNo + " Requisitions:" + count_requisition + ", Orders:" + count_order + ", Invoices:"
				+ count_invoice + ", Payments:" + count_payment + " created.";
	}

	public String getOrder(MCFTemplateLine tline, MCFPeriod period) {
		BigDecimal amt_order = Env.ZERO;
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer whereClause = new StringBuffer(
				" AD_Client_ID=? AND DatePromised BETWEEN ? AND ? AND DocStatus=? AND isSOTrx=? ");
		parameters.add(p_AD_Client_ID);
		parameters.add(period.getStartDate());
		parameters.add(period.getEndDate());
		parameters.add(tline.getDocStatus());
		parameters.add(tline.getC_DocType().isSOTrx());
		List<MOrder> morders = new Query(getCtx(), MOrder.Table_Name, whereClause.toString(),currentTrx.getTrxName())
				.setParameters(parameters).list();
		for (MOrder morder : morders) {
			if (tline.getAD_Table().getTableName().equals("C_Order")) {
				if (morder.isSOTrx()) {
					amt_balance = amt_balance.add(morder.getTotalLines());
					amt_order = amt_order.add(morder.getTotalLines());
				} else {
					amt_balance = amt_balance.add(morder.getTotalLines().negate());
					amt_order = amt_order.add(morder.getTotalLines().negate());
				}
			}

			// add doc lines
			else if (tline.getAD_Table().getTableName().equals("C_OrderLine")) {
				MOrderLine[] morderlines = morder.getLines();
				for (MOrderLine morderline : morderlines) {
					if (morder.isSOTrx()) {
						amt_balance = amt_balance.add(morderline.getLineNetAmt());
						amt_order = morderline.getLineNetAmt();
					} else {
						amt_balance = amt_balance.add(morderline.getLineNetAmt().negate());
						amt_order = morderline.getLineNetAmt().negate();
					}

					addCashForecasting(tline, period, amt_order, amt_balance, morderline.getC_BPartner_ID(),
							morderline.getM_Product_ID(), morderline.getDatePromised(), morderline.getQtyEntered());
				}
			}
		}
		// add Cash Forecasting record
		if (tline.getAD_Table().getTableName().equals("C_Order"))
			addCashForecasting(tline, period, amt_order, amt_balance);

		count_order++;

		return "";
	}

	public String getInvoice(MCFTemplateLine tline, MCFPeriod period) {
		BigDecimal amt_invoice = Env.ZERO;
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer whereClause = new StringBuffer(
				" AD_Client_ID=? AND DateInvoiced BETWEEN ? AND ? AND DocStatus=? AND isSOTrx=? AND isPaid='N' ");
		parameters.add(p_AD_Client_ID);
		parameters.add(period.getStartDate());
		parameters.add(period.getEndDate());
		parameters.add(tline.getDocStatus());
		parameters.add(tline.getC_DocType().isSOTrx());
		List<MInvoice> minvoices = new Query(getCtx(), MInvoice.Table_Name, whereClause.toString(),currentTrx.getTrxName())
				.setParameters(parameters).list();
		for (MInvoice minvoice : minvoices) {

			if (minvoice.isSOTrx()) {
				amt_balance = amt_balance.add(minvoice.getTotalLines());
				amt_invoice = amt_invoice.add(minvoice.getTotalLines());
			} else {
				amt_balance = amt_balance.add(minvoice.getTotalLines().negate());
				amt_invoice = amt_invoice.add(minvoice.getTotalLines().negate());
			}

			// add doc lines
			if (tline.getAD_Table().getTableName().equals("C_InvoiceLine")) {
				MInvoiceLine[] minvlines = minvoice.getLines();
				for (MInvoiceLine minvline : minvlines) {
					addCashForecasting(tline, period, amt_invoice, amt_balance,
							minvline.getC_Invoice().getC_BPartner_ID(), minvline.getM_Product_ID(),
							minvline.getC_Invoice().getDateInvoiced(), minvline.getQtyEntered());
				}
			}
		}

		// add Cash Forecasting record
		if (tline.getAD_Table().getTableName().equals("C_Invoice"))
			addCashForecasting(tline, period, amt_invoice, amt_balance);

		count_invoice++;

		return "";
	}

	public String getPayment(MCFTemplateLine tline, MCFPeriod period) {
		BigDecimal amt_payment = Env.ZERO;
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer whereClause = new StringBuffer(
				" AD_Client_ID=? AND DateTrx BETWEEN ? AND ? AND DocStatus=? AND isReceipt=? ");
		parameters.add(p_AD_Client_ID);
		parameters.add(period.getStartDate());
		parameters.add(period.getEndDate());
		parameters.add(tline.getDocStatus());
		parameters.add(tline.getC_DocType().isSOTrx());
		List<MPayment> mpayments = new Query(getCtx(), MPayment.Table_Name, whereClause.toString(),currentTrx.getTrxName())
				.setParameters(parameters).list();
		for (MPayment mpayment : mpayments) {
			if (mpayment.isReceipt()) {
				amt_balance = amt_balance.add(mpayment.getPayAmt());
				amt_payment = amt_payment.add(mpayment.getPayAmt());
			} else {
				amt_balance = amt_balance.add(mpayment.getPayAmt().negate());
				amt_payment = amt_payment.add(mpayment.getPayAmt().negate());
			}
		}
		// add Cash Forecasting record
		addCashForecasting(tline, period, amt_payment, amt_balance);
		count_payment++;

		return "";
	}

	public String getRequisition(MCFTemplateLine tline, MCFPeriod period) {
		BigDecimal amt_req = Env.ZERO;
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer whereClause = new StringBuffer(
				" AD_Client_ID=? AND DateRequired BETWEEN ? AND ? AND DocStatus=? ");
		parameters.add(p_AD_Client_ID);
		parameters.add(period.getStartDate());
		parameters.add(period.getEndDate());
		parameters.add(tline.getDocStatus());
		List<MRequisition> mreqs = new Query(getCtx(), MRequisition.Table_Name, whereClause.toString(),currentTrx.getTrxName())
				.setParameters(parameters).setOrderBy("DateRequired, DocumentNo ").list();
		for (MRequisition mreq : mreqs) {
			if (tline.getAD_Table().getTableName().equals("M_Requisition")) {
				amt_balance = amt_balance.add(mreq.getTotalLines().negate());
				amt_req = amt_req.add(mreq.getTotalLines().negate());
			}

			// add doc lines
			else if (tline.getAD_Table().getTableName().equals("M_RequisitionLine")) {
				MRequisitionLine[] mreglines = mreq.getLines();
				for (MRequisitionLine mregline : mreglines) {
					amt_balance = amt_balance.add(mregline.getLineNetAmt().negate());
					amt_req = mregline.getLineNetAmt().negate();
					addCashForecasting(tline, period, amt_req, amt_balance, mregline.getC_BPartner_ID(),
							mregline.getM_Product_ID(), mregline.getDateRequired(), mregline.getQty());
				}
			}
		}

		// add Cash Forecasting record for perion
		if (tline.getAD_Table().getTableName().equals("M_Requisition"))
			addCashForecasting(tline, period, amt_req, amt_balance);

		count_requisition++;

		return "";
	}

	/**************************************************************************
	 * add Cash Forecastings record
	 * 
	 * @param MCFTemplateLine tline
	 * @param MCFPeriod       period
	 * @param BigDecimal      amt
	 * @param BigDecimal      amt_balance
	 * @throws SQLException
	 */
	public int addCashForecasting(MCFTemplateLine tline, MCFPeriod period, BigDecimal amt, BigDecimal amt_balance) {
		int count_cf = 0;

		{
			MCFCashForecasting cf = new MCFCashForecasting(getCtx(), 0,currentTrx.getTrxName());
			cf.setName(tline.getName());
			cf.setC_DocType_ID(tline.getC_DocType_ID());
			cf.setDescription("Period Definition:" + period.getC_CF_PeriodDefinition().getName() + " | Period: "
					+ period.getName());
			cf.setC_CF_Template_ID(p_CF_Template_ID);

			cf.setValue(tline.getValue());
			cf.setSequence(BigDecimal.valueOf(count_period).add((tline.getSequence()).divide(BigDecimal.valueOf(10))));
			cf.setAmt(amt);
			cf.setBalance(amt_balance);
			cf.setDatePromised(period.getEndDate());
			cf.setC_CF_Period_ID(period.getC_CF_Period_ID());
			cf.setAD_PInstance_ID(AD_PInstance_ID);
			cf.saveEx();
		}
		result_msg += "[Records :" + count_cf + "]";

		return count_cf;
	}

	/**************************************************************************
	 * add Cash Forecastings record
	 * 
	 * @param MCFTemplateLine tline
	 * @param MCFPeriod       period
	 * @param BigDecimal      amt
	 * @param BigDecimal      amt_balance
	 * @param BigDecimal      C_BPartner_ID
	 * @param BigDecimal      M_Product_ID
	 * @throws SQLException
	 */
	public int addCashForecasting(MCFTemplateLine tline, MCFPeriod period, BigDecimal amt, BigDecimal amt_balance,
			int BP_ID, int Product_ID, Timestamp DatePromised, BigDecimal qty) {
		int count_cf = 0;

		{
			MCFCashForecasting cf = new MCFCashForecasting(getCtx(), 0,currentTrx.getTrxName());
			cf.setName(tline.getName());
			cf.setC_DocType_ID(tline.getC_DocType_ID());
			cf.setDescription("Period Definition:" + period.getC_CF_PeriodDefinition().getName() + " | Period: "
					+ period.getName());
			cf.setC_CF_Template_ID(p_CF_Template_ID);

			cf.setValue(tline.getValue());
			cf.setSequence(BigDecimal.valueOf(count_period).add((tline.getSequence()).divide(BigDecimal.valueOf(10))));
			cf.setAmt(amt);
			cf.setBalance(amt_balance);
			// cf.setDatePromised(period.getEndDate());
			cf.setDatePromised(DatePromised);
			cf.setC_CF_Period_ID(period.getC_CF_Period_ID());
			cf.setAD_PInstance_ID(AD_PInstance_ID);
			// add for detail report [draft for test]
			if (BP_ID > 0)
				cf.setC_BPartner_ID(BP_ID);
			if (Product_ID > 0)
				cf.setM_Product_ID(Product_ID);
			cf.setQty(qty);
			//
			cf.saveEx();
		}
		result_msg += "[Records :" + count_cf + "]";

		return count_cf;
	}

	/**************************************************************************
	 * add Balance
	 * 
	 * @param Timestamp dateacct
	 * @param String    balanceType: OB-Opening Balance from GL for DateFrom
	 *                  parameter, POBP-Projected Opening Balance for StartDate of
	 *                  Period (calculated), PEBP-Projected Ending Balance for
	 *                  EndDate of Period (calculated), PEB-Projected Ending Balance
	 *                  for DateTo parameter (calculated),
	 * @throws SQLException
	 */
	public BigDecimal addBalance(Timestamp dateacct, String balanceType, BigDecimal Sequence) {

		log.info("Calculating balance");

		BigDecimal Balance = Env.ZERO;

		MCFCashForecasting cf = new MCFCashForecasting(getCtx(), 0,currentTrx.getTrxName());
		cf.setC_CF_Template_ID(p_CF_Template_ID);

		if (balanceType == "OB") {

			StringBuilder sqlBalance = new StringBuilder(
					"SELECT COALESCE(SUM(acctBalance(fa.Account_ID,fa.AmtAcctDr,fa.AmtAcctCr)),0) FROM Fact_Acct fa WHERE fa.DateAcct<=");
			sqlBalance.append(DB.TO_DATE(dateacct)).append(" AND fa.PostingType='").append(MFactAcct.POSTINGTYPE_Actual)
					.

					// for debug - B_Asset_Acct,B_InTransit_Acct and B_UnallocatedCash_Acct
					append("' AND ((fa.Account_ID IN (SELECT vc.Account_ID FROM C_ValidCombination vc WHERE C_ValidCombination_ID IN (SELECT DISTINCT(baa.B_Asset_Acct) FROM C_BankAccount_Acct baa WHERE AD_Client_ID=")
					.append(p_AD_Client_ID)
					.append("))) OR (fa.Account_ID IN (SELECT vc.Account_ID FROM C_ValidCombination vc WHERE C_ValidCombination_ID IN (SELECT DISTINCT(baa.B_InTransit_Acct) FROM C_BankAccount_Acct baa WHERE AD_Client_ID=")
					.append(p_AD_Client_ID)
					.append("))) OR (fa.Account_ID IN (SELECT vc.Account_ID FROM C_ValidCombination vc WHERE C_ValidCombination_ID IN (SELECT DISTINCT(baa.B_UnallocatedCash_Acct) FROM C_BankAccount_Acct baa WHERE AD_Client_ID=")
					.append(p_AD_Client_ID).append("))))");
			//

			Balance = DB.getSQLValueBD(get_TrxName(), sqlBalance.toString());

			cf.setBalance(Balance);
			cf.setSequence(Env.ZERO);
			cf.setValue("Opening Balance");
			cf.setName("GL Balance");
			cf.setDescription("Opening Balance from GL for DateFrom parameter");
		} else {
			if (balanceType == "POBP") {
				cf.setValue("Opening Balance");
				cf.setDescription("Projected Opening Balance for StartDate of Period (calculated)");
				Sequence = Env.ZERO;
			} else if (balanceType == "PEBP") {
				cf.setValue("Ending Balance");
				cf.setDescription("Projected Ending Balance for EndDate of Period  (calculated)");
			} else if (balanceType == "PEB") {
				cf.setValue("Ending Balance");
				cf.setDescription("Projected Ending Balance for DateTo parameter (calculated)");
				Sequence = Env.ZERO;
				count_period = 999;
			}
			cf.setBalance(amt_balance);
			cf.setSequence(BigDecimal.valueOf(count_period).add((Sequence).divide(BigDecimal.valueOf(10))));
		}

		cf.setName("Calculating Balance");
		cf.setDatePromised(dateacct);
		cf.setC_CF_Period_ID(period_id);
		cf.setAD_PInstance_ID(AD_PInstance_ID);
		cf.saveEx();
		return Balance;
	}
	
	/*
	 * get the a Report Engine Instance using the view table 
	 * @param tableName
	 */
	private ReportEngine getReportEngine(String formatName, String tableName)
	{
		// Get Format & Data
		int format_id= MPrintFormat.getPrintFormat_ID(formatName, MTable.getTable_ID(tableName), getAD_Client_ID());
		MPrintFormat format = MPrintFormat.get(getCtx(), format_id, true);
		if (format == null)
		{
			addLog("@NotFound@ @AD_PrintFormat_ID@");
			return null;
		}
		// query
		MQuery query = new MQuery(tableName);
		query.addRestriction("AD_PInstance_ID", MQuery.EQUAL, AD_PInstance_ID);
		// Engine
		PrintInfo info = new PrintInfo(tableName,  MTable.getTable_ID(tableName), AD_PInstance_ID);
		ReportEngine re = new ReportEngine(getCtx(), format, query, info);
		return re;
	}

}
