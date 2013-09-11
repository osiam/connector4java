package org.osiam.client.oauth;

public enum Scope {

	GET
	,POST
	,PUT
	,PATCH
	,DELETE
	,ALL;
	
	public String toString(){
		if(this == Scope.ALL){
			return "GET POST PUT PATCH DELETE";
		}else{
			return super.toString();
		}
	}
}
