package org.osiam.client.update;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.osiam.client.query.metamodel.Group_;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;
/*
 * for licensing see the file license.txt.
 */

/**
 * Class to create a UpdateGroup Object to update a existing Group
 */
public final class UpdateGroup {// NOSONAR - Builder constructs instances of this class

    private Group group;
    private UpdateGroup(Builder builder){
        group = builder.updateGroup.build();
    }

    /**
     * the Scim conform Group to be used to update a existing Group
     * @return Group to update
     */
    public Group getScimConformUpdateGroup(){
        return group;
    }
    
    /**
     * The Builder is used to construct instances of the {@link UpdateGroup}
     *
     */
    public static class Builder{

    	private Group.Builder updateGroup = null;
        private Set<String> deleteFields = new HashSet<>();
        private static final String DELETE = "delete";
        private Set<MultiValuedAttribute> members = new HashSet<>();

        public Builder(){
        	updateGroup = new Group.Builder();
        }
        
//start ExternalID
        /**
         * delete the external Id of a existing group
         * @return The builder itself
         */
        public Builder deleteExternalId(){
            deleteFields.add(Group_.externalId.toString());
            return this;
        }

        /**
         * updates the external id of a existing group
         * @param externalID new external id
         * @return The builder itself
         */
        public Builder updateExternalId(String externalID){
        	updateGroup.setExternalId(externalID);
            return this;
        }
        //end ExternalID
        
//start DisplayName
        /**
         * updates the display name of a existing group
         * @param displayName new display name
         * @return The builder itself
         */
        public Builder updateDisplayName(String displayName){
            updateGroup.setDisplayName(displayName);
            return this;
        }
        //end DisplayName
        
//start members
        /**
         * deletes all group members of a existing group
         * @return The builder itself
         */
        public Builder deleteMembers(){
            deleteFields.add("members");
            return this;
        }
        
        /**
         * removes the membership of the given group or user in a existing group
         * @param memberId group or user id to be removed
         * @return The builder itself
         */
        public Builder deleteMember(UUID memberId){
            MultiValuedAttribute deleteGroup = new MultiValuedAttribute.Builder()
                    .setValue(memberId.toString())
                    .setOperation(DELETE).build();
            members.add(deleteGroup);
            return this;
        }

        /**
         * adds a new membership of a group or a user to a existing group
         * @param member user or group id to be added
         * @return The builder itself
         */
        public Builder addMember(UUID memberId){
            MultiValuedAttribute newGroup = new MultiValuedAttribute.Builder()
            		.setValue(memberId.toString()).build();
            members.add(newGroup);
            return this;
        }
        //end group
        
    	/**
         * constructs a {@link UpdateGroup} with the given values
         *
         * @return a valid {@link UpdateGroup}
         */
        public UpdateGroup build(){
            if(deleteFields.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
                Meta meta = new Meta.Builder()
                        .setAttributes(deleteFields).build();
                updateGroup.setMeta(meta);
            }
            if(members.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateGroup.setMembers(members);
            }
            
            return new UpdateGroup(this);
        }
    }
}
