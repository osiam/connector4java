package org.osiam.client.update;
/*
 * for licensing see the file license.txt.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import com.google.common.collect.Sets.SetView;

/**
 * Class to create a UpdateUser Object to update a existing User
 */
public final class UpdateUser{// NOSONAR - Builder constructs instances of this class

    private User user;
    private UpdateUser(Builder builder){
        user = builder.updateUser.build();
    }

    /**
     * the Scim conform User to be used to update a existing User
     * @return User to update
     */
    public User getScimConformUpdateUser(){
        return user;
    }

    /**
     * The Builder is used to construct instances of the {@link UpdateUser}
     *
     */
    public static class Builder{

    	private String userName;
    	private String nickName;
    	private String externalId;
    	private String locale;
    	private String password;
    	private String preferredLanguage;
    	private String profileUrl;
    	private String timezone;
    	private String title;
    	private Name name;
    	private String userType;
    	private String displayName;
    	private Boolean active;
        private User.Builder updateUser = null;
        private Set<String> deleteFields = new HashSet<>();
        private List<MultiValuedAttribute> emails = new ArrayList<>();
        private List<MultiValuedAttribute> ims = new ArrayList<>();
        private List<MultiValuedAttribute> groups = new ArrayList<>();
        private List<MultiValuedAttribute> phoneNumbers = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();
        private List<MultiValuedAttribute> entitlements = new ArrayList<>();
        private List<MultiValuedAttribute> photos = new ArrayList<>();
        private List<MultiValuedAttribute> roles = new ArrayList<>();
        private List<MultiValuedAttribute> certificates = new ArrayList<>();
        private static final String DELETE = "delete";

        public Builder(){
        }
       
//start username
        /**
         * updates the nickName of a existing user
         * @param nickName the new nickName
         * @return The builder itself
         */
        public Builder updateUserName(String userName){
        	this.userName = userName;
            return this;
        }
        //end username
        
//start address
        /**
         * adds a new address to the existing addresses of a existing user
         * @param address the new address
         * @return The builder itself
         */
        public Builder addAddress(Address address){
            addresses.add(address);
            return this;
        }
        
        /**
         * deletes the given address from the list of existing addresses of a existing user
         * @param address address to be deleted
         * @return The builder itself
         */
        public Builder deleteAddress(Address address){
        /*	TODO korrekte Adresse ist nicht in version 0.22 sondern nur 0.23-SNAPSHORT vom scim schema vorhanden
            Address deleteAddress = new Address.Builder(address)
            		.setOperation(DELETE)
            		.build();
 
        	addresses.add(deleteAddress);
        	*/
            return this;
        }
        
        /**
         * deletes all existing addresses of the a existing user
         * @return The builder itself
         */
        public Builder deleteAddresses(){
        	deleteFields.add("addresses");
        	return this;
        }
        
        /**
         * updates the old Address with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Address
         * @return The builder itself
         */
        public Builder updateAddress(Address oldAttribute, Address newAttribute){
        	deleteAddress(oldAttribute);
        	addAddress(newAttribute);
        	return this;
        }
        //end address
        
//start Nickname
        /**
         * deletes the nickName of a existing user
         * @return The builder itself
         */
        public Builder deleteNickName(){
            deleteFields.add(User_.nickName.toString());
            return this;
        }

        /**
         * updates the nickName of a existing user
         * @param nickName the new nickName
         * @return The builder itself
         */
        public Builder updateNickName(String nickName){
            this.nickName = nickName;
            return this;
        }
        //end Nickname
        
//start ExternalID
        /**
         * delete the external Id of a existing user
         * @return The builder itself
         */
        public Builder deleteExternalId(){
            deleteFields.add(User_.externalId.toString());
            return this;
        }

        /**
         * updates the external id of a existing user
         * @param externalId new external id
         * @return The builder itself
         */
        public Builder updateExternalId(String externalId){
        	this.externalId = externalId;
            return this;
        }
        //end ExternalID
        
//start local
        /**
         * delete the local value of a existing user
         * @return The builder itself
         */
        public Builder deleteLocal(){
            deleteFields.add(User_.locale.toString());
            return this;
        }

        /**
         * updates the local of a existing user
         * @param locale new local
         * @return The builder itself
         */
        public Builder updateLocale(String locale){
            this.locale = locale;
            return this;
        }
        //end local
        
//start password
        /**
         * updates the password of a existing user
         * @param password new password
         * @return The builder itself
         */
        public Builder updatePassword(String password){
            this.password = password;
            return this;
        }
        //end password
        
//start preferredLanguage
        /**
         * delete the preferred Language of a existing user
         * @return The builder itself
         */
        public Builder deletePreferredLanguage(){
            deleteFields.add(User_.preferredLanguage.toString());
            return this;
        }

        /**
         * updates the preferred language of a existing user
         * @param preferredLanguage new preferred language
         * @return The builder itself
         */
        public Builder updatePreferredLanguage(String preferredLanguage){
        	this.preferredLanguage = preferredLanguage;
            return this;
        }
        //end preferredLanguage
        
//start ProfileUrl
        /**
         * deletes the profil Url of a existing user
         * @return The builder itself
         */
        public Builder deleteProfileUrl(){
            deleteFields.add(User_.profileUrl.toString());
            return this;
        }

        /**
         * updates the profil URL of a existing user
         * @param profileUrl new profilUrl
         * @return The builder itself
         */
        public Builder updateProfileUrl(String profileUrl){
        	this.profileUrl = profileUrl;
            return this;
        }
        //end ProfileUrl
        
//start timezone
        /**
         * deletes the timezone of a existing user
         * @return The builder itself
         */
        public Builder deleteTimezone(){
            deleteFields.add(User_.timezone.toString());
            return this;
        }

        /**
         * updates the timezone of a existing user
         * @param timezone new timeZone
         * @return The builder itself
         */
        public Builder updateTimezone(String timezone){
            this.timezone = timezone;
            return this;
        }
        //end timezone
        
//start title
        /**
         * deletes the title of a existing user
         * @return The builder itself
         */
        public Builder deleteTitle(){
            deleteFields.add(User_.title.toString());
            return this;
        }

        /**
         * updates the title of a existing user
         * @param title new tile
         * @return The builder itself
         */
        public Builder updateTitle(String title){
            this.title = title;
            return this;
        }
        //end title
        
//start name
        /**
         * deletes the name of a existing user
         * @return The builder itself
         */
        public Builder deleteName(){
            deleteFields.add("name");
            return this;
        }

        /**
         * updates the name of a existing user
         * @param name new Name
         * @return The builder itself
         */
        public Builder updateName(Name name){
            this.name = name;
            return this;
        }
        //end name
        
//start UserType
        /**
         * deletes the user type of a existing user
         * @return The builder itself
         */
        public Builder deleteUserType(){
            deleteFields.add(User_.userType.toString());
            return this;
        }

        /**
         * updates the user type of a existing user
         * @param userType new user type
         * @return The builder itself
         */
        public Builder updateUserType(String userType){
            this.userType = userType;
            return this;
        }
        //end UserType
        
//start DisplayName
        /**
         * deletes the display name of a existing user
         * @return The builder itself
         */
        public Builder deleteDisplayName(){
            deleteFields.add(User_.displayName.toString());
            return this;
        }

        /**
         * updates the display name of a existing user
         * @param displayName new display name
         * @return The builder itself
         */
        public Builder updateDisplayName(String displayName){
            this.displayName = displayName;
            return this;
        }
        //end DisplayName
        
//start email
        /**
         * deletes all emails of a existing user
         * @return The builder itself
         */
        public Builder deleteEmails(){
            deleteFields.add("emails");
            return this;
        }
        
        /**
         * deletes the given email of a existing user
         * @param email to be deleted
         * @return The builder itself
         */
        public Builder deleteEmail(MultiValuedAttribute email){
        	MultiValuedAttribute deleteEmail = new MultiValuedAttribute.Builder()
        		.setValue(email.getValue())
        		.setType(email.getType())
        		.setOperation(DELETE).build();
        	emails.add(deleteEmail);
            return this;
        }

        /**
         * adds or updates a emil of an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param email new email
         * @return The builder itself
         */
        public Builder addEmail(MultiValuedAttribute email){
            emails.add(email);
            return this;
        }
        
        /**
         * updates the old Email with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Email
         * @return The builder itself
         */
        public Builder updateEmail(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deleteEmail(oldAttribute);
        	addEmail(newAttribute);
        	return this;
        }
        //end email
        
//start certificates
        /**
         * deletes all X509Certificates of a existing user
         * @return The builder itself
         */
        public Builder deleteX509Certificates(){
            deleteFields.add("x509Certificates");
            return this;
        }
        
        /**
         * deletes the given certificate of a existing user
         * @param certificate to be deleted
         * @return The builder itself
         */
        public Builder deleteX509Certificate(MultiValuedAttribute certificate){
            MultiValuedAttribute deleteCertificates = new MultiValuedAttribute.Builder()
            		.setValue(certificate.getValue())
                    .setOperation(DELETE).build();
            certificates.add(deleteCertificates);
            return this;
        }

        /**
         * adds or updates certificate to an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param certificate new certificate
         * @return The builder itself
         */
        public Builder addX509Certificate(MultiValuedAttribute certificate){
        	certificates.add(certificate);
            return this;
        }
        
        /**
         * updates the old X509Certificate with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new X509Certificate
         * @return The builder itself
         */
        public Builder updateX509Certificate(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deleteX509Certificate(oldAttribute);
        	addX509Certificate(newAttribute);
        	return this;
        }
        //end certificates
        
//start roles
        /**
         * deletes all roles of a existing user
         * @return The builder itself
         */
        public Builder deleteRoles(){
            deleteFields.add("roles");
            return this;
        }
        
        /**
         * deletes the given role of a existing user
         * @param role to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(MultiValuedAttribute role){
            MultiValuedAttribute deleteRole = new MultiValuedAttribute.Builder()
            		.setValue(role.getValue())
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }
        
        /**
         * deletes the given role of a existing user
         * @param role to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(String role){
            MultiValuedAttribute deleteRole = new MultiValuedAttribute.Builder()
            		.setValue(role)
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }

        /**
         * adds or updates a role of an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param role new role
         * @return The builder itself
         */
        public Builder addRole(MultiValuedAttribute role){
            roles.add(role);
            return this;
        }
        
        /**
         * updates the old Role with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Role
         * @return The builder itself
         */
        public Builder updateRole(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deleteRole(oldAttribute);
        	addRole(newAttribute);
        	return this;
        }
        //end roles
        
//start ims
        /**
         * deletes all ims of a existing user
         * @return The builder itself
         */
        public Builder deleteIms(){
            deleteFields.add("ims");
            return this;
        }
        
        /**
         * deletes the ims of a existing user
         * @param ims to be deleted
         * @return The builder itself
         */
        public Builder deleteIms(MultiValuedAttribute ims){
        	MultiValuedAttribute deleteIms = new MultiValuedAttribute.Builder()
        			.setValue(ims.getValue())
        			.setType(ims.getType())
                    .setOperation(DELETE).build();
            this.ims.add(deleteIms);
            return this;
        }

        /**
         * adds or updates a ims to an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param ims new ims
         * @return The builder itself
         */
        public Builder addIms(MultiValuedAttribute ims){
            this.ims.add(ims);
            return this;
        }
        
        /**
         * updates the old Ims with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Ims
         * @return The builder itself
         */
        public Builder updateIms(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deleteIms(oldAttribute);
        	addIms(newAttribute);
        	return this;
        }
        //end ims
        
//start phonenumbers
        /**
         * adds or updates a phoneNumber to an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param phoneNumber new phoneNumber 
         * @return The builder itself
         */
        public Builder addPhoneNumber(MultiValuedAttribute phoneNumber){
            phoneNumbers.add(phoneNumber);
            return this;
        }
        
        /**
         * deletes the phonenumber of a existing user 
         * @param phoneNumber to be deleted
         * @return The builder itself
         */
        public Builder deletePhoneNumber(MultiValuedAttribute phoneNumber){
        	MultiValuedAttribute deletePhoneNumber = new MultiValuedAttribute.Builder()
        			.setValue(phoneNumber.getValue())
        			.setType(phoneNumber.getType())
                    .setOperation(DELETE).build();
            phoneNumbers.add(deletePhoneNumber);
            return this;
        }
        /**
         * deletes all phonenumbers of a existing user
         * @return The builder itself
         */
        public Builder deletePhoneNumbers(){
            deleteFields.add("phonenumbers");
            return this;
        }
        
        /**
         * updates the old PhoneNumber with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new PhoneNumber
         * @return The builder itself
         */
        public Builder updatePhoneNumber(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deletePhoneNumber(oldAttribute);
        	addPhoneNumber(newAttribute);
        	return this;
        }
        //end phonenumbers
        
//start photos
        /**
         * adds or updates a photo to an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param photo new photo
         * @return The builder itself
         */
        public Builder addPhoto(MultiValuedAttribute photo){
            photos.add(photo);
            return this;
        }
        
        /**
         * deletes the photo of a existing user
         * @param photo to be deleted
         * @return The builder itself
         */
        public Builder deletePhoto(MultiValuedAttribute photo){
        	MultiValuedAttribute deletePhoto = new MultiValuedAttribute.Builder()
        			.setValue(photo.getValue())
        			.setType(photo.getType())
                    .setOperation(DELETE)
                    .build();
            photos.add(deletePhoto);
            return this;
        }
        /**
         * deletes all photos of a existing user
         * @return The builder itself
         */
        public Builder deletePhotos(){
            deleteFields.add("photos");
            return this;
        }
        
        /**
         * updates the old Photo with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Photo
         * @return The builder itself
         */
        public Builder updatePhotos(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deletePhoto(oldAttribute);
        	addPhoto(newAttribute);
        	return this;
        }
        //end photos
        
//start entitlement
        /**
         * deletes all entitlements of a existing user
         * @return The builder itself
         */
        public Builder deleteEntitlements(){
            deleteFields.add("entitlements");
            return this;
        }
                
        /**
         * deletes the entitlement of a existing user
         * @param entitlement to be deleted
         * @return The builder itself
         */
        public Builder deleteEntitlement(MultiValuedAttribute entitlement){
        	MultiValuedAttribute deleteEntitlement = new MultiValuedAttribute.Builder()
        			.setValue(entitlement.getValue())
        			.setType(entitlement.getType())
                    .setOperation(DELETE)
                    .build();
            entitlements.add(deleteEntitlement);
            return this;
        }

        /**
         * adds or updates a entitlement to an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param entitlement new entitlement
         * @return The builder itself
         */
        public Builder addEntitlement(MultiValuedAttribute entitlement){
        	entitlements.add(entitlement);
            return this;
        }
        
        /**
         * updates the old Entitlement with the new one
         * @param oldAttribute to be replaced
         * @param newAttribute new Entitlement
         * @return The builder itself
         */
        public Builder updateEntitlement(MultiValuedAttribute oldAttribute, MultiValuedAttribute newAttribute){
        	deleteEntitlement(oldAttribute);
        	addEntitlement(newAttribute);
        	return this;
        }
        //end entitlement
        
//start group
        /**
         * deletes all group memberships of a existing user
         * @return The builder itself
         */
        public Builder deleteGroups(){
            deleteFields.add("groups");
            return this;
        }
        
        
        /**
         * removes the membership of in the given group of a existing user
         * @param groupId membership to be removed
         * @return The builder itself
         */
        public Builder deleteGroup(String groupId){
        	MultiValuedAttribute deleteGroup = new MultiValuedAttribute.Builder()
                    .setValue(groupId)
                    .setOperation(DELETE).build();
            groups.add(deleteGroup);
            return this;
        }
        
        /**
         * removes the membership of in the given group of a existing user
         * @param groupRef membership to be removed
         * @return The builder itself
         */
        public Builder deleteGroup(MultiValuedAttribute groupRef){
        	MultiValuedAttribute deleteGroup = new MultiValuedAttribute.Builder()
        			.setValue(groupRef.getValue())
                    .setOperation(DELETE).build();
            groups.add(deleteGroup);
            return this;
        }
        
        /**
         * adds or updates an group membership of an existing user
         * if the .getValue() already exists a update will be done. If not a new one will be added
         * @param groupMembership updated group membership
         * @return The builder itself
         */
        public Builder addGroupMembership(MultiValuedAttribute groupMembership){
        	groups.add(groupMembership);
            return this;
        }
        //end group

//start active
        /**
         * updates the active status of a existing User to the given value
         * @param activ new active status
         * @return The builder itself
         */
        public Builder updateActive(boolean active){
        	this.active = active;
        	return this;
        }
        //end activ
        
        /**
         * constructs a UpdateUser with the given values
         *
         * @return a valid {@link UpdateUser}
         */
        public UpdateUser build(){// NOSONAR - Since we build a User it is ok that the Cyclomatic Complexity is over 10 
        	if(userName != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser = new User.Builder(userName);
        	}else{
        		updateUser = new User.Builder();
        	}
        	if(nickName != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setNickName(nickName);
        	}
        	if(externalId != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setExternalId(externalId);
        	}
        	if(locale != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setLocale(locale);
        	}
        	if(password != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setPassword(password);
        	}
        	if(preferredLanguage != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setPreferredLanguage(preferredLanguage);
        	}
        	if(profileUrl != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setProfileUrl(profileUrl);
        	}
        	if(timezone != null) {// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setTimezone(timezone);
        	}
        	if(title != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setTitle(title);
        	}
        	if(name != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setName(name);
        	}
        	if(userType != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setUserType(userType);
        	}
        	if(displayName != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setDisplayName(displayName);
        	}
        	if(active != null){// NOSONAR - false-positive from clover; if-expression is correct
        		updateUser.setActive(active);
        	}
            if(deleteFields.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
                Meta meta = new Meta.Builder()
                        .setAttributes(deleteFields).build();
                updateUser.setMeta(meta);
            }
            if(emails.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setEmails(emails);
            }
            if(phoneNumbers.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setPhoneNumbers(phoneNumbers);
            }
            if(addresses.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setAddresses(addresses);
            }
            if(entitlements.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setEntitlements(entitlements);
            }
            if(ims.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setIms(ims);
            }
            if(photos.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setPhotos(photos);
            }
            if(roles.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setRoles(roles);
            }
            if(certificates.size() > 0){// NOSONAR - false-positive from clover; if-expression is correct
            	updateUser.setX509Certificates(certificates);
            }
            
            return new UpdateUser(this);
        }
    }
}
