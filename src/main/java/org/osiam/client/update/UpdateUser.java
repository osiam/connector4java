package org.osiam.client.update;

import org.osiam.client.query.metamodel.Attribute;
import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;

import java.util.*;

public class UpdateUser{

    private User user;
    private UpdateUser(Builder builder){
        user = builder.updateUser.build();
    }

    public User getUserToUpdate(){
        return user;
    }

    public static class Builder{

        User.Builder updateUser = null;
        Set<String> deleteFields = new HashSet<>();
        List<MultiValuedAttribute> emails = new ArrayList<>();
        List<MultiValuedAttribute> ims = new ArrayList<>();
        List<MultiValuedAttribute> groups = new ArrayList<>();
        List<MultiValuedAttribute> phoneNumbers = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        List<MultiValuedAttribute> entitlements = new ArrayList<>();
        List<MultiValuedAttribute> photos = new ArrayList<>();
        List<MultiValuedAttribute> roles = new ArrayList<>();
        List<MultiValuedAttribute> certificates = new ArrayList<>();

        public Builder(){
            updateUser = new User.Builder();
        }

        public Builder(String userName){
            updateUser = new User.Builder(userName);
        }

        //start active
        public Builder setActiv(boolean activ){
        	updateUser.setActive(activ);
            return this;
        }
        //end activ
        
        //start address
        public Builder addAddress(Address address){
            addresses.add(address);
            return this;
        }
        
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
            		.setOperation("delete")
            		.build()
            		
            		
        	//TODO we can't build a address since setOperation is a Multivalue Field 
        	 * and its builder can't build a address
        	addresses.add(address);
        	*/
            return this;
        }
        
        public Builder deleteAddresses(){
        	deleteFields.add("addresses");
        	return this;
        }
        //end address
        
        //start Nickname
        public Builder deleteNickname(){
            deleteFields.add(User_.nickName.toString());
            return this;
        }

        public Builder updateNickname(String nickName){
            updateUser.setNickName(nickName);
            return this;
        }
        //end Nickname
        
        //start ExternalID
        public Builder deleteExternalId(){
            deleteFields.add(User_.externalId.toString());
            return this;
        }

        public Builder updateExternalId(String nickName){
            updateUser.setExternalId(nickName);
            return this;
        }
        //end ExternalID
        
        //start local
        public Builder deleteLocal(){
            deleteFields.add(User_.locale.toString());
            return this;
        }

        public Builder updateLocal(String locale){
            updateUser.setLocale(locale);
            return this;
        }
        //end local
        
        //start password
        public Builder updatePassword(String password){
            updateUser.setPassword(password);
            return this;
        }
        //end password
        
        //start preferredLanguage
        public Builder deletePreferredLanguage(){
            deleteFields.add(User_.preferredLanguage.toString());
            return this;
        }

        public Builder updatePreferredLanguage(String preferredLanguage){
            updateUser.setPreferredLanguage(preferredLanguage);
            return this;
        }
        //end preferredLanguage
        
        //start ProfileUrl
        public Builder deleteProfileUrl(){
            deleteFields.add(User_.profileUrl.toString());
            return this;
        }

        public Builder updateProfileUrl(String profileUrl){
            updateUser.setProfileUrl(profileUrl);
            return this;
        }
        //end ProfileUrl
        
        //start timezone
        public Builder deleteTimezone(){
            deleteFields.add(User_.timezone.toString());
            return this;
        }

        public Builder updateTimezone(String timezone){
            updateUser.setTimezone(timezone);
            return this;
        }
        //end timezone
        
        //start title
        public Builder deleteTitle(){
            deleteFields.add(User_.title.toString());
            return this;
        }

        public Builder updateTitle(String title){
            updateUser.setTitle(title);
            return this;
        }
        //end title
        
        //start UserType
        public Builder deleteUserType(){
            deleteFields.add(User_.userType.toString());
            return this;
        }

        public Builder updateUserType(String userType){
            updateUser.setUserType(userType);
            return this;
        }
        //end UserType
        
        //start DisplayName
        public Builder deleteDisplayName(){
            deleteFields.add(User_.displayName.toString());
            return this;
        }

        public Builder updateDisplayName(String displayName){
            updateUser.setDisplayName(displayName);
            return this;
        }
        //end DisplayName
        
        //start email
        public Builder deleteEmails(){
            deleteFields.add("emails");
            return this;
        }
        
        public Builder deleteEmail(String email){
            MultiValuedAttribute delteEmail = new MultiValuedAttribute.Builder()
                    .setValue(email)
                    .setOperation("delete").build();
            emails.add(delteEmail);
            return this;
        }

        public Builder updateEmail(MultiValuedAttribute email){
            emails.add(email);
            return this;
        }

        public Builder addEmail(MultiValuedAttribute email){
            emails.add(email);
            return this;
        }
        //end email
        
      //start certificates
        public Builder deleteX509Certificates(){
            deleteFields.add("certificates");
            return this;
        }
        
        public Builder deleteX509Certificate(String certificate){
            MultiValuedAttribute delteCertificates = new MultiValuedAttribute.Builder()
                    .setValue(certificate)
                    .setOperation("delete").build();
            certificates.add(delteCertificates);
            return this;
        }

        public Builder updateX509Certificate(MultiValuedAttribute certificate){
        	certificates.add(certificate);
            return this;
        }

        public Builder addX509Certificate(MultiValuedAttribute certificate){
        	certificates.add(certificate);
            return this;
        }
        //end certificates
        
      //start roles
        public Builder deleteRoles(){
            deleteFields.add("roles");
            return this;
        }
        
        public Builder deleteRole(String role){
            MultiValuedAttribute delteRole = new MultiValuedAttribute.Builder()
                    .setValue(role)
                    .setOperation("delete").build();
            roles.add(delteRole);
            return this;
        }

        public Builder updateRole(MultiValuedAttribute role){
            roles.add(role);
            return this;
        }

        public Builder addRole(MultiValuedAttribute role){
            roles.add(role);
            return this;
        }
        //end roles
        
      //start ims
        public Builder deleteIms(){
            deleteFields.add("ims");
            return this;
        }
        
        public Builder deleteIms(String ims){
            MultiValuedAttribute delteIms = new MultiValuedAttribute.Builder()
                    .setValue(ims)
                    .setOperation("delete").build();
            this.ims.add(delteIms);
            return this;
        }

        public Builder updateIms(MultiValuedAttribute ims){
            this.ims.add(ims);
            return this;
        }

        public Builder addIms(MultiValuedAttribute ims){
            this.ims.add(ims);
            return this;
        }
        //end ims
        
        //start phonenumbers
        public Builder addPhoneNumber(MultiValuedAttribute phoneNumber){
            phoneNumbers.add(phoneNumber);
            return this;
        }
        
        public Builder updatePhoneNumber(MultiValuedAttribute phoneNumber){
        	phoneNumbers.add(phoneNumber);
            return this;
        }
        
        public Builder deletePhoneNumber(String phoneNumber){
            MultiValuedAttribute deltePhoneNumber = new MultiValuedAttribute.Builder()
                    .setValue(phoneNumber)
                    .setOperation("delete").build();
            phoneNumbers.add(deltePhoneNumber);
            return this;
        }
        
        public Builder deletePhoneNumbers(){
            deleteFields.add("phonenumbers");
            return this;
        }
        //end phonenumbers
        
      //start photos
        public Builder addPhotos(MultiValuedAttribute photo){
            photos.add(photo);
            return this;
        }
        
        public Builder updatePhoto(MultiValuedAttribute photo){
        	photos.add(photo);
            return this;
        }
        
        public Builder deletePhoto(String photoUri){
            MultiValuedAttribute deltePhoto = new MultiValuedAttribute.Builder()
                    .setValue(photoUri)
                    .setOperation("delete").build();
            photos.add(deltePhoto);
            return this;
        }
        
        public Builder deletePhotos(){
            deleteFields.add("photos");
            return this;
        }
        //end photos
        
      //start entitlement
        public Builder deleteEntitlements(){
            deleteFields.add("entitlements");
            return this;
        }
        
      //start group
        public Builder deleteGroups(){
            deleteFields.add("groups");
            return this;
        }
        
        public Builder deleteGroup(UUID groupId){
            MultiValuedAttribute delteGroup = new MultiValuedAttribute.Builder()
                    .setValue(groupId.toString())
                    .setOperation("delete").build();
            groups.add(delteGroup);
            return this;
        }

        public Builder addGroup(UUID groupId){
            MultiValuedAttribute newGroup = new MultiValuedAttribute.Builder()
            .setValue(groupId.toString())
            .build();
            groups.add(newGroup);
            return this;
        }
        //end group
        
        public Builder deleteEntitlement(String entitlement){
            MultiValuedAttribute deleteEntitlement = new MultiValuedAttribute.Builder()
                    .setValue(entitlement)
                    .setOperation("delete").build();
            entitlements.add(deleteEntitlement);
            return this;
        }

        public Builder updateEntitlement(MultiValuedAttribute entitlement){
        	entitlements.add(entitlement);
            return this;
        }

        public Builder addEntitlement(MultiValuedAttribute entitlement){
        	entitlements.add(entitlement);
            return this;
        }
        //end entitlement
        
        public Builder setActive(boolean active){
        	updateUser.setActive(active);
        	return this;
        }
        
        public UpdateUser build(){
            if(deleteFields.size() > 0){
                Meta meta = new Meta.Builder()
                        .setAttributes(deleteFields).build();
                updateUser.setMeta(meta);
            }
            if(emails.size() > 0){
                updateUser.setEmails(emails);
            }
            if(phoneNumbers.size() > 0){
            	updateUser.setPhoneNumbers(phoneNumbers);
            }
            if(addresses.size() > 0){
            	updateUser.setAddresses(addresses);
            }
            if(entitlements.size() > 0){
            	updateUser.setEntitlements(entitlements);
            }
            if(ims.size() > 0){
            	updateUser.setIms(ims);
            }
            if(photos.size() > 0){
            	updateUser.setPhotos(photos);
            }
            if(roles.size() > 0){
            	updateUser.setRoles(roles);
            }
            if(certificates.size() > 0){
            	updateUser.setX509Certificates(certificates);
            }
            
            return new UpdateUser(this);
        }
    }
}
