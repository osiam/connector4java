package org.osiam.client.oauth;

import java.util.Set;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.osiam.resources.scim.Group;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class QueryResult {

    private Integer totalResults;
    /*private Integer itemsPerPage;
    private Integer startIndex;
    private Set<String> schemas;
    private Set<Group> Resources;*/
    
    //JSON Serializing
    public QueryResult(){}

    public QueryResult(Builder builder) {
        this.totalResults = builder.totalResults;
        /*this.itemsPerPage = builder.itemsPerPage;
        this.startIndex = builder.startIndex;
        this.schemas = builder.schemas;
        this.Resources = builder.Resources;*/
    }
    
    public Integer getTotalResults() {
        return totalResults;
    }
    
   /* public Integer getItemsPerPage(){
    	return itemsPerPage;
    }
    
    public Integer getStartIndex(){
    	return startIndex;
    }
    
    public Set<String> getSchemas(){
    	return schemas;
    }
    
    public Set<Group> Resources(){
    	return Resources;
    }*/
    
    public static class Builder{
    	private Integer totalResults;
    	/*private Integer itemsPerPage;
    	private Integer startIndex;
        private Set<String> schemas;
        private Set<Group> Resources;*/
        
        public Builder setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
            return this;
        }
      /*  
        public Builder setItemsPerPage(Integer itemsPerPage){
        	this.itemsPerPage = itemsPerPage;
        	return this;
        }
        
        public Builder setStartIndex(Integer startIndex){
        	this.startIndex = startIndex;
        	return this;
        }
        
        public Builder setSchemas(Set<String> schemas){
        	this.schemas = schemas;
        	return this;
        }
        
        public Builder setResources(Set<Group> Resources){
        	this.Resources = Resources;
        	return this;
        }*/
        
        public QueryResult build(){
            return new QueryResult(this);
        }
    }
}
