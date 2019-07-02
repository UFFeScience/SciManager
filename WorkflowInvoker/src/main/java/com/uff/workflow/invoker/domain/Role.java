package com.uff.workflow.invoker.domain;

public enum Role {
	
	ADMIN("Administrador"), USER("Usuário");
	
	private final String roleName;

	Role(String roleName) {
		this.roleName = roleName;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
}