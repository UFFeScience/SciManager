package com.uff.system.notifier;

import org.springframework.context.annotation.Bean;

public class SystemNotifierTestContext {

	@Bean(destroyMethod = "shutdown")
	public void dataSource() {}
	
}
