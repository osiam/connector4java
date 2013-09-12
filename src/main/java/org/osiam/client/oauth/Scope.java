package org.osiam.client.oauth;

public enum Scope {

	GET
	,POST
	,PUT
	,PATCH
	,DELETE
	,ALL{ 
			public String toString(){
				StringBuilder allScopes = new StringBuilder();
				for (Scope actScope : Scope.values()) {
					if(!actScope.equals(this)){
						allScopes.append(" ").append(actScope.toString());
					}
				}
				return allScopes.toString().trim();
			}
		}
	;
}
