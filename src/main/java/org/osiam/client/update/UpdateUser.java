/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client.update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Entitlement;
import org.osiam.resources.scim.Im;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.Photo;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.User;
import org.osiam.resources.scim.X509Certificate;

/**
 * Class to create a UpdateUser Object to update a existing User
 */
public final class UpdateUser {// NOSONAR - Builder constructs instances of this class

    private User user;

    private UpdateUser(Builder builder) {
        user = builder.updateUser.build();
    }

    /**
     * the Scim conform User to be used to update a existing User
     * 
     * @return User to update
     */
    public User getScimConformUpdateUser() {
        return user;
    }

    /**
     * The Builder is used to construct instances of the {@link UpdateUser}
     * 
     */
    public static class Builder {

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
        private List<Email> emails = new ArrayList<>();
        private List<Im> ims = new ArrayList<>();
        private List<PhoneNumber> phoneNumbers = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();
        private List<Entitlement> entitlements = new ArrayList<>();
        private List<Photo> photos = new ArrayList<>();
        private List<Role> roles = new ArrayList<>();
        private List<X509Certificate> certificates = new ArrayList<>();
        private static final String DELETE = "delete";

        public Builder() {
        }

        // start username
        /**
         * updates the nickName of a existing user
         * 
         * @param userName
         *        the new user name
         * @return The builder itself
         */
        public Builder updateUserName(String userName) {
            this.userName = userName;
            return this;
        }

        // end username

        // start address
        /**
         * adds a new address to the existing addresses of a existing user
         * 
         * @param address
         *        the new address
         * @return The builder itself
         */
        public Builder addAddress(Address address) {
            addresses.add(address);
            return this;
        }

        /**
         * deletes the given address from the list of existing addresses of a existing user
         * 
         * @param address
         *        address to be deleted
         * @return The builder itself
         */
        public Builder deleteAddress(Address address) {
            Address deleteAddress = new Address.Builder(address)
                    .setOperation(DELETE)
                    .build();
            addresses.add(deleteAddress);
            return this;
        }

        /**
         * deletes all existing addresses of the a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteAddresses() {
            deleteFields.add("addresses");
            return this;
        }

        /**
         * updates the old Address with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Address
         * @return The builder itself
         */
        public Builder updateAddress(Address oldAttribute, Address newAttribute) {
            deleteAddress(oldAttribute);
            addAddress(newAttribute);
            return this;
        }

        // end address

        // start Nickname
        /**
         * deletes the nickName of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteNickName() {
            deleteFields.add(User_.nickName.toString());
            return this;
        }

        /**
         * updates the nickName of a existing user
         * 
         * @param nickName
         *        the new nickName
         * @return The builder itself
         */
        public Builder updateNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        // end Nickname

        // start ExternalID
        /**
         * delete the external Id of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteExternalId() {
            deleteFields.add(User_.externalId.toString());
            return this;
        }

        /**
         * updates the external id of a existing user
         * 
         * @param externalId
         *        new external id
         * @return The builder itself
         */
        public Builder updateExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        // end ExternalID

        // start local
        /**
         * delete the local value of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteLocal() {
            deleteFields.add(User_.locale.toString());
            return this;
        }

        /**
         * updates the local of a existing user
         * 
         * @param locale
         *        new local
         * @return The builder itself
         */
        public Builder updateLocale(String locale) {
            this.locale = locale;
            return this;
        }

        // end local

        // start password
        /**
         * updates the password of a existing user
         * 
         * @param password
         *        new password
         * @return The builder itself
         */
        public Builder updatePassword(String password) {
            this.password = password;
            return this;
        }

        // end password

        // start preferredLanguage
        /**
         * delete the preferred Language of a existing user
         * 
         * @return The builder itself
         */
        public Builder deletePreferredLanguage() {
            deleteFields.add(User_.preferredLanguage.toString());
            return this;
        }

        /**
         * updates the preferred language of a existing user
         * 
         * @param preferredLanguage
         *        new preferred language
         * @return The builder itself
         */
        public Builder updatePreferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        // end preferredLanguage

        // start ProfileUrl
        /**
         * deletes the profil Url of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteProfileUrl() {
            deleteFields.add(User_.profileUrl.toString());
            return this;
        }

        /**
         * updates the profil URL of a existing user
         * 
         * @param profileUrl
         *        new profilUrl
         * @return The builder itself
         */
        public Builder updateProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        // end ProfileUrl

        // start timezone
        /**
         * deletes the timezone of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteTimezone() {
            deleteFields.add(User_.timezone.toString());
            return this;
        }

        /**
         * updates the timezone of a existing user
         * 
         * @param timezone
         *        new timeZone
         * @return The builder itself
         */
        public Builder updateTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        // end timezone

        // start title
        /**
         * deletes the title of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteTitle() {
            deleteFields.add(User_.title.toString());
            return this;
        }

        /**
         * updates the title of a existing user
         * 
         * @param title
         *        new tile
         * @return The builder itself
         */
        public Builder updateTitle(String title) {
            this.title = title;
            return this;
        }

        // end title

        // start name
        /**
         * deletes the name of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteName() {
            deleteFields.add("name");
            return this;
        }

        /**
         * updates the name of a existing user
         * 
         * @param name
         *        new Name
         * @return The builder itself
         */
        public Builder updateName(Name name) {
            this.name = name;
            return this;
        }

        // end name

        // start UserType
        /**
         * deletes the user type of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteUserType() {
            deleteFields.add(User_.userType.toString());
            return this;
        }

        /**
         * updates the user type of a existing user
         * 
         * @param userType
         *        new user type
         * @return The builder itself
         */
        public Builder updateUserType(String userType) {
            this.userType = userType;
            return this;
        }

        // end UserType

        // start DisplayName
        /**
         * deletes the display name of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteDisplayName() {
            deleteFields.add(User_.displayName.toString());
            return this;
        }

        /**
         * updates the display name of a existing user
         * 
         * @param displayName
         *        new display name
         * @return The builder itself
         */
        public Builder updateDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        // end DisplayName

        // start email
        /**
         * deletes all emails of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteEmails() {
            deleteFields.add("emails");
            return this;
        }

        /**
         * deletes the given email of a existing user
         * 
         * @param email
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteEmail(Email email) {
            Email deleteEmail = new Email.Builder()
                    .setValue(email.getValue())
                    .setType(email.getType())
                    .setOperation(DELETE).build();
            emails.add(deleteEmail);
            return this;
        }

        /**
         * adds or updates a emil of an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         * 
         * @param email
         *        new email
         * @return The builder itself
         */
        public Builder addEmail(Email email) {
            emails.add(email);
            return this;
        }

        /**
         * updates the old Email with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Email
         * @return The builder itself
         */
        public Builder updateEmail(Email oldAttribute, Email newAttribute) {
            deleteEmail(oldAttribute);
            addEmail(newAttribute);
            return this;
        }

        // end email

        // start certificates
        /**
         * deletes all X509Certificates of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteX509Certificates() {
            deleteFields.add("x509Certificates");
            return this;
        }

        /**
         * deletes the given certificate of a existing user
         * 
         * @param certificate
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteX509Certificate(X509Certificate certificate) {
            X509Certificate deleteCertificates = new X509Certificate.Builder()
                    .setValue(certificate.getValue())
                    .setOperation(DELETE).build();
            certificates.add(deleteCertificates);
            return this;
        }

        /**
         * adds or updates certificate to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         * 
         * @param certificate
         *        new certificate
         * @return The builder itself
         */
        public Builder addX509Certificate(X509Certificate certificate) {
            certificates.add(certificate);
            return this;
        }

        /**
         * updates the old X509Certificate with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new X509Certificate
         * @return The builder itself
         */
        public Builder updateX509Certificate(X509Certificate oldAttribute, X509Certificate newAttribute) {
            deleteX509Certificate(oldAttribute);
            addX509Certificate(newAttribute);
            return this;
        }

        // end certificates

        // start roles
        /**
         * deletes all roles of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteRoles() {
            deleteFields.add("roles");
            return this;
        }

        /**
         * deletes the given role of a existing user
         * 
         * @param role
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(Role role) {
            Role deleteRole = new Role.Builder()
                    .setValue(role.getValue())
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }

        /**
         * deletes the given role of a existing user
         * 
         * @param role
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteRole(String role) {
            Role deleteRole = new Role.Builder()
                    .setValue(role)
                    .setOperation(DELETE).build();
            roles.add(deleteRole);
            return this;
        }

        /**
         * adds or updates a role of an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         * 
         * @param role
         *        new role
         * @return The builder itself
         */
        public Builder addRole(Role role) {
            roles.add(role);
            return this;
        }

        /**
         * updates the old Role with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Role
         * @return The builder itself
         */
        public Builder updateRole(Role oldAttribute, Role newAttribute) {
            deleteRole(oldAttribute);
            addRole(newAttribute);
            return this;
        }

        // end roles

        // start ims
        /**
         * deletes all ims of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteIms() {
            deleteFields.add("ims");
            return this;
        }

        /**
         * deletes the ims of a existing user
         * 
         * @param ims
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteIms(Im ims) {
            Im deleteIms = new Im.Builder()
                    .setValue(ims.getValue())
                    .setType(ims.getType())
                    .setOperation(DELETE).build();
            this.ims.add(deleteIms);
            return this;
        }

        /**
         * adds or updates a ims to an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         * 
         * @param ims
         *        new ims
         * @return The builder itself
         */
        public Builder addIms(Im ims) {
            this.ims.add(ims);
            return this;
        }

        /**
         * updates the old Ims with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Ims
         * @return The builder itself
         */
        public Builder updateIms(Im oldAttribute, Im newAttribute) {
            deleteIms(oldAttribute);
            addIms(newAttribute);
            return this;
        }

        // end ims

        // start phonenumbers
        /**
         * adds or updates a phoneNumber to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         * 
         * @param phoneNumber
         *        new phoneNumber
         * @return The builder itself
         */
        public Builder addPhoneNumber(PhoneNumber phoneNumber) {
            phoneNumbers.add(phoneNumber);
            return this;
        }

        /**
         * deletes the phonenumber of a existing user
         * 
         * @param phoneNumber
         *        to be deleted
         * @return The builder itself
         */
        public Builder deletePhoneNumber(PhoneNumber phoneNumber) {
            PhoneNumber deletePhoneNumber = new PhoneNumber.Builder()
                    .setValue(phoneNumber.getValue())
                    .setType(phoneNumber.getType())
                    .setOperation(DELETE).build();
            phoneNumbers.add(deletePhoneNumber);
            return this;
        }

        /**
         * deletes all phonenumbers of a existing user
         * 
         * @return The builder itself
         */
        public Builder deletePhoneNumbers() {
            deleteFields.add("phoneNumbers");
            return this;
        }

        /**
         * updates the old PhoneNumber with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new PhoneNumber
         * @return The builder itself
         */
        public Builder updatePhoneNumber(PhoneNumber oldAttribute, PhoneNumber newAttribute) {
            deletePhoneNumber(oldAttribute);
            addPhoneNumber(newAttribute);
            return this;
        }

        // end phonenumbers

        // start photos
        /**
         * adds or updates a photo to an existing user if the .getValue() already exists a update will be done. If not a
         * new one will be added
         * 
         * @param photo
         *        new photo
         * @return The builder itself
         */
        public Builder addPhoto(Photo photo) {
            photos.add(photo);
            return this;
        }

        /**
         * deletes the photo of a existing user
         * 
         * @param photo
         *        to be deleted
         * @return The builder itself
         */
        public Builder deletePhoto(Photo photo) {
            Photo deletePhoto = new Photo.Builder()
                    .setValue(photo.getValue())
                    .setType(photo.getType())
                    .setOperation(DELETE)
                    .build();
            photos.add(deletePhoto);
            return this;
        }

        /**
         * deletes all photos of a existing user
         * 
         * @return The builder itself
         */
        public Builder deletePhotos() {
            deleteFields.add("photos");
            return this;
        }

        /**
         * updates the old Photo with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Photo
         * @return The builder itself
         */
        public Builder updatePhotos(Photo oldAttribute, Photo newAttribute) {
            deletePhoto(oldAttribute);
            addPhoto(newAttribute);
            return this;
        }

        // end photos

        // start entitlement
        /**
         * deletes all entitlements of a existing user
         * 
         * @return The builder itself
         */
        public Builder deleteEntitlements() {
            deleteFields.add("entitlements");
            return this;
        }

        /**
         * deletes the entitlement of a existing user
         * 
         * @param entitlement
         *        to be deleted
         * @return The builder itself
         */
        public Builder deleteEntitlement(Entitlement entitlement) {
            Entitlement deleteEntitlement = new Entitlement.Builder()
                    .setValue(entitlement.getValue())
                    .setType(entitlement.getType())
                    .setOperation(DELETE)
                    .build();
            entitlements.add(deleteEntitlement);
            return this;
        }

        /**
         * adds or updates a entitlement to an existing user if the .getValue() already exists a update will be done. If
         * not a new one will be added
         * 
         * @param entitlement
         *        new entitlement
         * @return The builder itself
         */
        public Builder addEntitlement(Entitlement entitlement) {
            entitlements.add(entitlement);
            return this;
        }

        /**
         * updates the old Entitlement with the new one
         * 
         * @param oldAttribute
         *        to be replaced
         * @param newAttribute
         *        new Entitlement
         * @return The builder itself
         */
        public Builder updateEntitlement(Entitlement oldAttribute, Entitlement newAttribute) {
            deleteEntitlement(oldAttribute);
            addEntitlement(newAttribute);
            return this;
        }

        // end entitlement

        // start active
        /**
         * updates the active status of a existing User to the given value
         * 
         * @param active
         *        new active status
         * @return The builder itself
         */
        public Builder updateActive(boolean active) {
            this.active = active;
            return this;
        }

        // end activ

        /**
         * constructs a UpdateUser with the given values
         * 
         * @return a valid {@link UpdateUser}
         */
        public UpdateUser build() {// NOSONAR - Since we build a User it is ok that the Cyclomatic Complexity is over 10
            if (userName == null || userName.isEmpty()) {
                updateUser = new User.Builder();
            } else {
                updateUser = new User.Builder(userName);
            }
            if (nickName != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setNickName(nickName);
            }
            if (externalId != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setExternalId(externalId);
            }
            if (locale != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setLocale(locale);
            }
            if (password != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setPassword(password);
            }
            if (preferredLanguage != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setPreferredLanguage(preferredLanguage);
            }
            if (profileUrl != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setProfileUrl(profileUrl);
            }
            if (timezone != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setTimezone(timezone);
            }
            if (title != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setTitle(title);
            }
            if (name != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setName(name);
            }
            if (userType != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setUserType(userType);
            }
            if (displayName != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setDisplayName(displayName);
            }
            if (active != null) {// NOSONAR - false-positive from clover; if-expression is correct
                updateUser.setActive(active);
            }
            if (deleteFields.size() > 0) {// NOSONAR - false-positive from clover; if-expression is correct
                Meta meta = new Meta.Builder()
                        .setAttributes(deleteFields).build();
                updateUser.setMeta(meta);
            }
            if (emails.size() > 0) {
                updateUser.setEmails(emails);
            }
            if (phoneNumbers.size() > 0) {
                updateUser.setPhoneNumbers(phoneNumbers);
            }
            if (addresses.size() > 0) {
                updateUser.setAddresses(addresses);
            }
            if (entitlements.size() > 0) {
                updateUser.setEntitlements(entitlements);
            }
            if (ims.size() > 0) {
                updateUser.setIms(ims);
            }
            if (photos.size() > 0) {
                updateUser.setPhotos(photos);
            }
            if (roles.size() > 0) {
                updateUser.setRoles(roles);
            }
            if (certificates.size() > 0) {
                updateUser.setX509Certificates(certificates);
            }

            return new UpdateUser(this);
        }
    }
}