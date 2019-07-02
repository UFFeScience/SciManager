package com.uff.workflow.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class WorkflowMonitorTestContext {

	@Bean(destroyMethod = "shutdown")
	public EmbeddedDatabase dataSource() {
	    return new EmbeddedDatabaseBuilder().
	            setType(EmbeddedDatabaseType.H2).
	            addScript("db/migration/creates.sql").	            
	            build();
	}
	
}
