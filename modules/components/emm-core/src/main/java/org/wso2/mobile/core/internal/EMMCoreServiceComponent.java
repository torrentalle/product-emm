
/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.mobile.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.ndatasource.core.CarbonDataSource;
import org.wso2.carbon.ndatasource.core.DataSourceService;
import org.wso2.mobile.core.EMMDBInitializerException;

import javax.sql.DataSource;

/**
 * @scr.component name="emm.utils.service.component" immediate="true"
 * @scr.reference name="datasources.service" interface="org.wso2.carbon.ndatasource.core.DataSourceService"
 * cardinality="1..1" policy="dynamic" bind="setDataSourceService" unbind="unsetDataSourceService"
 */

public class EMMCoreServiceComponent {

	private static final String SETUP_CMD = "setup";
	private static final String EMM_DB_NAME = "WSO2_EMM_DB";
	private static DataSourceService carbonDataSourceService;

	private static final Log log = LogFactory.getLog(EMMCoreServiceComponent.class);

	protected void activate(ComponentContext ctx) {
		try {
			String cmd = System.getProperty(SETUP_CMD);
			if (cmd != null) {
				this.initializeDatabase();
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	protected void deactivate(ComponentContext ctx) {
		log.debug("EMM-Core bundle is deactivated ");
	}

	protected void setDataSourceService(DataSourceService dataSourceService) {
		if (log.isDebugEnabled()) {
			log.debug("Setting the DataSourceService");
		}
		carbonDataSourceService = dataSourceService;
	}

	protected void unsetDataSourceService(DataSourceService dataSourceService) {
		if (log.isDebugEnabled()) {
			log.debug("Unsetting the DataSourceService");
		}
		carbonDataSourceService = null;
	}

	/*
	* This method will initialize the EMM Database creation
	*/
	private void initializeDatabase() throws EMMDBInitializerException {
		try {
			CarbonDataSource cds = carbonDataSourceService.getDataSource(EMM_DB_NAME);
			DataSource dataSource = (DataSource) cds.getDSObject();
			EMMDBInitializer dbInitializer = new EMMDBInitializer(dataSource);
			dbInitializer.createEMMDatabase();
		} catch (EMMDBInitializerException e) {
			throw e;
		} catch (DataSourceException e) {
			String msg = "Error in getting the data-source object for creating EMM database";
			log.error(msg);
			throw new EMMDBInitializerException(msg, e);
		}
	}

}
