package org.idempiere.cashforecasting.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MCFCashForecasting extends X_C_CashForecasting{

	/**
	 * 
	 */
	private static final long serialVersionUID = -78512127568095470L;


	public MCFCashForecasting(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}


	public MCFCashForecasting(Properties ctx, int getID, String trxName) {
		super(ctx, getID, trxName);
		// TODO Auto-generated constructor stub
	}

}
