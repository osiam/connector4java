package org.osiam.client.update;
/*
 * for licensing see the file license.txt.
 */

import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;

import java.util.*;

/**
 * Class to create a UpdateUser Object to update a existing User
 */
public final class UpdateUser{

    private User user;
    private UpdateUser(Builder builder){
        user = builder.updateUser.build();
    }

    /**
     * the Scim conform User to be used to update a existing User
     * @return User to update
     */
    public User getUserToUpdate(){
        return user;
    }

    /**
     * The Builder is used to construct instances of the {@link UpdateUser}
     *
     */
    public static class Builder{

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

        /**
         * to be used if no userName has to be updated
         */
        public Builder(){
            updateUser = new User.Builder();
        }

        /**
         * to be used if the userName has to be updated
         * @param userName new userName of a existing User
         */
        public Builder(String userName){
            updateUser = new User.Builder(userName);
        }
       
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
         * deletes the given address from the list of existing addresses of a exisitng user
         * @param address address to be deleted
         * @return The builder itself
         */
        public Builder deleteAddress(Address address){
        /*    Address deleteAddress = new Address.Builder()
            		.setCountry(address.getCountry())
            		.setFormatted(address.getFormatted())
            		.setLocality(address.getLocality())
            		.setPostalCode(address.getPostalCode())
            		.setRegion(address.getRegion())
            		.setStreetAddress(address.getStreetAddress())
            		.setDisplay(address.getDisplay())
            		.setPrimary(address.isPrimary())
            		.setType(address.getType())
            		.setOperation(DELETE)
            		.build()
            		
            		
        	//TODO we can't build a address since setOperation is a Multivalue Field 
        	 * and its builder can't build a address
        	addresses.add(address);
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
            updateUser.setNickName(nickName);
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
         * @param externalID new external id
         * @return The builder itself
         */
        public Builder updateExternalId(String externalID){
            updateUser.setExternalId(externalID);
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
        public Builder updateLocal(String locale){
            updateUser.setLocale(locale);
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
            updateUser.setPassword(password);
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
            updateUser.setPreferredLanguage(preferredLanguage);
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
            updateUser.setProfileUrl(profileUrl);
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
            updateUser.setTimezone(timezone);
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
            updateUser.setTitle(title);
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
            updateUser.setName(name);
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
            updateUser.setUserType(userType);
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
            updateUser.setDisplayName(displayName);
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
        public Builder deleteEmail(String email){
            MultiValuedAttribute delteEmail = new MultiValuedAttribute.Builder()
                    .setValue(email)
                    .setOperation(DELETE).build();
            emails.add(delteEmail);
            return this;
        }

        /**
         * updates a email of a existing user
         * @param email updated email
         * @return The builder itself
         */
        public Builder updateEmail(MultiValuedAttribute email){
            emails.add(email);
            return this;
        }

        /**
         * adds a new emil to the existing ones of a existing user
         * @param email new email
         * @return The builder itself
         */
        public Builder addEmail(MultiValuedAttribute email){
            emails.add(email);
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
        public Builder deleteX509Certificate(String certificate){
            MultiValuedAttribute delteCertificates = new MultiValuedAttribute.Builder()
                    .setValue(certificate)
                    .setOperation(DELETE).build();
            certificates.add(delteCertificates);
            return this;
        }

        /**
         * updates an Certificate of a existing user
         * @param certificate updated certificate
         * @return The builder itself
         */
        public Builder updateX509Certificate(MultiValuedAttribute certificate){
        	certificates.add(certificate);
            return this;
        }

        /**
         * adds a new certificate to the existing ones of a existing user
         * @param certificate new certificate
         * @return The builder itself
         */
        public Builder addX509Certificate(MultiValuedAttribute certificate){
        	certificates.add(certificate);
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
        public Builder deleteRole(String role){
            MultiValuedAttribute delteRole = new MultiValuedAttribute.Builder()
                    .setValue(role)
                    .setOperation(DELETE).build();
            roles.add(delteRole);
            return this;
        }

        /**
         * updates an role of a existing user
         * @param role updated role
         * @return The builder itself
         */
        public Builder updateRole(MultiValuedAttribute role){
            roles.add(role);
            return this;
        }

        /**
         * adds a new role to the existing ones of a existing user
         * @param role new role
         * @return The builder itself
         */
        public Builder addRole(MultiValuedAttribute role){
            roles.add(role);
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
        public Builder deleteIms(String ims){
            MultiValuedAttribute delteIms = new MultiValuedAttribute.Builder()
                    .setValue(ims)
                    .setOperation(DELETE).build();
            this.ims.add(delteIms);
            return this;
        }

        /**
         * updates an ims of a existing user
         * @param ims updated ims
         * @return The builder itself
         */
        public Builder updateIms(MultiValuedAttribute ims){
            this.ims.add(ims);
            return this;
        }

        /**
         * adds a new ims to the existing ones of a existing user
         * @param ims new ims
         * @return The builder itself
         */
        public Builder addIms(MultiValuedAttribute ims){
            this.ims.add(ims);
            return this;
        }
        //end ims
        
//start phonenumbers
        /**
         * adds a new phoneNumber to the existing ones of a existing user
         * @param phoneNumber new phoneNumber 
         * @return The builder itself
         */
        public Builder addPhoneNumber(MultiValuedAttribute phoneNumber){
            phoneNumbers.add(phoneNumber);
            return this;
        }
        
        /**
         * updates an phonenumber of a existing user
         * @param phoneNumber updated phonenumber
         * @return The builder itself
         */
        public Builder updatePhoneNumber(MultiValuedAttribute phoneNumber){
        	phoneNumbers.add(phoneNumber);
            return this;
        }
        
        /**
         * deletes the phonenumber of a existing user 
         * @param phoneNumber to be deleted
         * @return The builder itself
         */
        public Builder deletePhoneNumber(String phoneNumber){
            MultiValuedAttribute deltePhoneNumber = new MultiValuedAttribute.Builder()
                    .setValue(phoneNumber)
                    .setOperation(DELETE).build();
            phoneNumbers.add(deltePhoneNumber);
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
        //end phonenumbers
        
//start photos
        /**
         * adds a new photo to the existing ones of a existing user
         * @param photo new photo
         * @return The builder itself
         */
        public Builder addPhotos(MultiValuedAttribute photo){
            photos.add(photo);
            return this;
        }
        
        /**
         * updates an photo of a existing user
         * @param photo updated photo
         * @return The builder itself
         */
        public Builder updatePhoto(MultiValuedAttribute photo){
        	photos.add(photo);
            return this;
        }
        
        /**
         * deletes the photo of a existing user
         * @param photoUri
         * @return The builder itself
         */
        public Builder deletePhoto(String photoUri){
            MultiValuedAttribute deltePhoto = new MultiValuedAttribute.Builder()
                    .setValue(photoUri)
                    .setOperation(DELETE).build();
            photos.add(deltePhoto);
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
        public Builder deleteEntitlement(String entitlement){
            MultiValuedAttribute deleteEntitlement = new MultiValuedAttribute.Builder()
                    .setValue(entitlement)
                    .setOperation(DELETE).build();
            entitlements.add(deleteEntitlement);
            return this;
        }

        /**
         * updates an entitlement of a existing user
         * @param entitlement updated entitlement
         * @return The builder itself
         */
        public Builder updateEntitlement(MultiValuedAttribute entitlement){
        	entitlements.add(entitlement);
            return this;
        }

        /**
         * adds a new entitlement to the existing ones of a existing user
         * @param entitlement new entitlement
         * @return The builder itself
         */
        public Builder addEntitlement(MultiValuedAttribute entitlement){
        	entitlements.add(entitlement);
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
         * removes the membership of the given group of a existing user
         * @param groupId membership to be removed
         * @return The builder itself
         */
        public Builder deleteGroup(UUID groupId){
            MultiValuedAttribute delteGroup = new MultiValuedAttribute.Builder()
                    .setValue(groupId.toString())
                    .setOperation(DELETE).build();
            groups.add(delteGroup);
            return this;
        }

        /**
         * adds a new membership to a group to the existing ones of a existing user
         * @param groupId new membership
         * @return The builder itself
         */
        public Builder addGroup(UUID groupId){
            MultiValuedAttribute newGroup = new MultiValuedAttribute.Builder()
            .setValue(groupId.toString())
            .build();
            groups.add(newGroup);
            return this;
        }
        
        /**
         * updates an group of a existing user
         * @param group updated group
         * @return The builder itself
         */
        public Builder updateGroup(MultiValuedAttribute group){
        	groups.add(group);
            return this;
        }
        //end group

//start active
        /**
         * sets the activ status of a existing User to the given value
         * @param activ new activ status
         * @return The builder itself
         */
        public Builder setActive(boolean active){
        	updateUser.setActive(active);
        	return this;
        }
        //end activ
        
        /**
         * constructs a UpdateUser with the given values
         *
         * @return a valid UpdateUser
         */
        public UpdateUser build(){
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
