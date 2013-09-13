package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

/**
 * supported access scopes by the OSIAM Server
 */
public enum Scope {

	GET
	,POST
	,PUT
	,PATCH
	,DELETE
	,ALL{ 
			@Override
			public String toString(){
				StringBuilder allScopes = new StringBuilder();
				for (Scope actScope : Scope.values()) {
					if(actScope != this){// NOSONAR - false-positive from clover; if-expression is correct
						allScopes.append(" ").append(actScope.toString());
					}
				}
				return allScopes.toString().trim();
			}
		}
	;
}
