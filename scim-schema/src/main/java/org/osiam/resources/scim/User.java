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

package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

import java.util.*;

/**
 * User resources are meant to enable expression of common User informations. With the core attributes it should be
 * possible to express most user data. If more information need to be saved in a user object the user extension can be
 * used to store all customized data.
 * 
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
 * </p>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class User extends Resource {

    private String userName;
    private Name name;
    private String displayName;
    private String nickName;
    private String profileUrl;
    private String title;
    private String userType;
    private String preferredLanguage;
    private String locale;
    private String timezone;
    private Boolean active;
    private String password = "";
    private List<MultiValuedAttribute> emails = new ArrayList<>();
    private List<MultiValuedAttribute> phoneNumbers = new ArrayList<>();
    private List<MultiValuedAttribute> ims = new ArrayList<>();
    private List<MultiValuedAttribute> photos = new ArrayList<>();
    private List<Address> addresses = new ArrayList<>();
    private List<MultiValuedAttribute> groups = new ArrayList<>();
    private List<MultiValuedAttribute> entitlements = new ArrayList<>();
    private List<MultiValuedAttribute> roles = new ArrayList<>();
    private List<MultiValuedAttribute> x509Certificates = new ArrayList<>();
    private Map<String, Extension> extensions = new HashMap<>();

    /**
     * Default constructor for Jackson
     */
    private User() {
    }

    private User(Builder builder) {
        super(builder);
        this.userName = builder.userName;
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.nickName = builder.nickName;
        this.profileUrl = builder.profileUrl;
        this.title = builder.title;
        this.userType = builder.userType;
        this.preferredLanguage = builder.preferredLanguage;
        this.locale = builder.locale;
        this.timezone = builder.timezone;
        this.active = builder.active;
        this.password = builder.password;

        this.emails = builder.emails;
        this.phoneNumbers = builder.phoneNumbers;
        this.ims = builder.ims;
        this.photos = builder.photos;
        this.addresses = builder.addresses;
        this.groups = builder.groups;
        this.entitlements = builder.entitlements;
        this.roles = builder.roles;
        this.x509Certificates = builder.x509Certificates;
        this.extensions = builder.extensions;
    }

    /**
     * Gets the unique identifier for the User.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the components of the User's real name.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the real {@link Name} of the {@link User}
     */
    public Name getName() {
        return name;
    }

    /**
     * Gets the name of the User, suitable for display to end-users.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the display name of the {@link User}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the casual way to address the user in real life,
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the nickname of the {@link User}
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Gets a fully qualified URL to a page representing the User's online profile.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the progile URL of the {@link User}
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * The user's title, such as "Vice President."
     * 
     * @return the title of the {@link User}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the type of the {@link User}
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the type of the {@link User}
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Gets the preferred written or spoken language of the User in ISO 3166-1 alpha 2 format, e.g. "DE" or "US".
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the preferred language of the {@link User}
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Gets the default location of the User in ISO 639-1 two letter language code, e.g. 'de_DE' or 'en_US'
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the default location of the {@link User}
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Gets the User's time zone in the "Olson" timezone database format
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the time zone of the {@link User}
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Gets a Boolean that indicates the User's administrative status.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the active status of the {@link User}
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Gets the password from the User.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6">SCIM core schema 2.0, section 6</a>
     * </p>
     * 
     * @return the password of the {@link User}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets all E-mail addresses for the User.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return the email addresses of the {@link User}
     */
    public List<MultiValuedAttribute> getEmails() {
        return emails;
    }

    /**
     * Gets the phone numbers for the user.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return the phone numbers of the {@link User}
     */
    public List<MultiValuedAttribute> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Gets the instant messaging address for the user.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return the ims of the {@link User}
     */
    public List<MultiValuedAttribute> getIms() {
        return ims;
    }

    /**
     * Gets the URL's of the photos of the user.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return the photo URL's of the {@link User}
     */
    public List<MultiValuedAttribute> getPhotos() {
        return photos;
    }

    /**
     * Gets the physical mailing addresses for this user.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return the addresses of the {@link User}
     */
    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * Gets a list of groups that the user belongs to.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return a list of all {@link Group}s where the {@link User} is a member of
     */
    public List<MultiValuedAttribute> getGroups() {
        return groups;
    }

    /**
     * Gets a list of entitlements for the user that represent a thing the User has.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return a list of all entitlements of the {@link User}
     */
    public List<MultiValuedAttribute> getEntitlements() {
        return entitlements;
    }

    /**
     * Gets a list of roles for the user that collectively represent who the User is.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return a list of the roles of the {@link User}
     */
    public List<MultiValuedAttribute> getRoles() {
        return roles;
    }

    /**
     * Gets a list of certificates issued to the user. Values are Binary and DER encoded x509.
     * 
     * <p>
     * For more detailed information please look at the <a
     * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02#section-6.2">SCIM core schema 2.0, section
     * 6.2</a>
     * </p>
     * 
     * @return a list of the certificates of the {@link User}
     */
    public List<MultiValuedAttribute> getX509Certificates() {
        return x509Certificates;
    }

    /**
     * Provides an unmodifiable view of all additional {@link Extension} fields of the user
     * 
     * @return an unmodifiable view of the extensions
     */
    @JsonAnyGetter
    public Map<String, Extension> getAllExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    /**
     * Provides the {@link Extension} with the given URN
     * 
     * @param urn
     *        The URN of the extension
     * @return The extension for the given URN
     * @throws IllegalArgumentException
     *         If urn is null or empty
     * @throws NoSuchElementException
     *         If extension with given urn is not available
     */
    public Extension getExtension(String urn) {
        if (urn == null || urn.isEmpty()) {
            throw new IllegalArgumentException("urn must be neither null nor empty");
        }

        if (!extensions.containsKey(urn)) {
            throw new NoSuchElementException("extension " + urn + " is not available");
        }

        return extensions.get(urn);
    }

    /**
     * Builder class that is used to build {@link User} instances
     */
    public static class Builder extends Resource.Builder {
        private String userName;
        private String password = "";
        private Boolean active;
        private String timezone;
        private String locale;
        private String preferredLanguage;
        private String userType;
        private String title;
        private String profileUrl;
        private String nickName;
        private String displayName;
        private Name name;
        private List<MultiValuedAttribute> emails = new ArrayList<>();
        private List<MultiValuedAttribute> phoneNumbers = new ArrayList<>();
        private List<MultiValuedAttribute> ims = new ArrayList<>();
        private List<MultiValuedAttribute> photos = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();
        private List<MultiValuedAttribute> groups = new ArrayList<>();
        private List<MultiValuedAttribute> entitlements = new ArrayList<>();
        private List<MultiValuedAttribute> roles = new ArrayList<>();
        private List<MultiValuedAttribute> x509Certificates = new ArrayList<>();
        private Map<String, Extension> extensions = new HashMap<>();

        /**
         * Constructs a new builder by with a set userName
         * 
         * @param userName
         *        Unique identifier for the User (See {@link User#getUserName()})
         */
        public Builder(String userName) {
            this();
            if (userName == null || userName.isEmpty()) {
                throw new IllegalArgumentException("userName must not be null or empty.");
            }
            this.userName = userName;
        }

        /**
         * Creates a new builder without a userName
         */
        public Builder() {
            super();
            this.schemas.add(Constants.USER_CORE_SCHEMA);
        }

        /**
         * Constructs a new builder by copying all values from the given {@link User}
         * 
         * @param user
         *        a old {@link User}
         */
        public Builder(User user) {
            super(user);
            this.userName = user.userName;
            this.name = user.name;
            this.displayName = user.displayName;
            this.nickName = user.nickName;
            this.profileUrl = user.profileUrl;
            this.title = user.title;
            this.userType = user.userType;
            this.preferredLanguage = user.preferredLanguage;
            this.locale = user.locale;
            this.timezone = user.timezone;
            this.active = user.active;
            this.password = user.password;
            this.emails = Objects.firstNonNull(user.emails, this.emails);
            this.phoneNumbers = Objects.firstNonNull(user.phoneNumbers, this.phoneNumbers);
            this.ims = Objects.firstNonNull(user.ims, this.ims);
            this.photos = Objects.firstNonNull(user.photos, this.photos);
            this.addresses = Objects.firstNonNull(user.addresses, this.addresses);
            this.groups = Objects.firstNonNull(user.groups, this.groups);
            this.entitlements = Objects.firstNonNull(user.entitlements, this.entitlements);
            this.roles = Objects.firstNonNull(user.roles, this.roles);
            this.x509Certificates = Objects.firstNonNull(user.x509Certificates, this.x509Certificates);
            this.extensions = Objects.firstNonNull(user.extensions, this.extensions);
        }

        /**
         * Sets the components of the {@link User}'s real name (See {@link User#getName()}).
         * 
         * @param name
         *        the name object of the {@link User}
         * @return the builder itself
         */
        public Builder setName(Name name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the display name (See {@link User#getDisplayName()}).
         * 
         * @param displayName
         *        the display name of the {@link User}
         * @return the builder itself
         */
        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the nick name (See {@link User#getNickName()}).
         * 
         * @param nickName
         *        the nick name of the {@link User}
         * @return the builder itself
         */
        public Builder setNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        /**
         * Sets the profile URL (See {@link User#getProfileUrl()}).
         * 
         * @param profileUrl
         *        the profil URL of the {@link User}
         * @return the builder itself
         */
        public Builder setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        /**
         * Sets the user's title (See {@link User#getTitle()}).
         * 
         * @param title
         *        the title of the {@link User}
         * @return the builder itself
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the user type (See {@link User#getUserType()}).
         * 
         * @param userType
         *        the type of the {@link User}
         * @return the builder itself
         */
        public Builder setUserType(String userType) {
            this.userType = userType;
            return this;
        }

        /**
         * Sets the preferred language of the USer (See {@link User#getPreferredLanguage()}).
         * 
         * @param preferredLanguage
         *        sets the preferred language of the {@link User}
         * @return the builder itself
         */
        public Builder setPreferredLanguage(String preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        /**
         * Sets the default location of the User (See {@link User#getLocale()}).
         * 
         * @param locale
         *        sets the local of the {@link User}
         * @return the builder itself
         */
        public Builder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Sets the User's time zone (See {@link User#getTimezone()}).
         * 
         * @param timezone
         *        sets the time zone of the {@link User}
         * @return the builder itself
         */
        public Builder setTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        /**
         * Sets a Boolean value indicating the User's administrative status. (See {@link User#isActive()})
         * 
         * @param active
         *        the active status of the {@link User}
         * @return the builder itself
         */
        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        /**
         * Sets the User's clear text password (See {@link User#getPassword()}).
         * 
         * @param password
         *        the password as clear text
         * @return the builder itself
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the E-mail addresses for the User (See {@link User#getEmails()}).
         * 
         * @param emails
         *        the emails as Set
         * @return the builder itself
         */
        public Builder setEmails(List<MultiValuedAttribute> emails) {
            this.emails = emails;
            return this;
        }

        /**
         * Sets the phone numbers for the User (See {@link User#getPhoneNumbers()}).
         * 
         * @param phoneNumbers
         *        the phone numbers of the the {@link User}
         * @return the builder itself
         */
        public Builder setPhoneNumbers(List<MultiValuedAttribute> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        /**
         * Sets the instant messaging addresses for the User (See {@link User#getIms()}).
         * 
         * @param ims
         *        a list of the ims of the {@link User}
         * @return the builder itself
         */
        public Builder setIms(List<MultiValuedAttribute> ims) {
            this.ims = ims;
            return this;
        }

        /**
         * Sets the URL's of photo's of the User (See {@link User#getPhotos()}).
         * 
         * @param photos
         *        the photos of the {@link User}
         * @return the builder itself
         */
        public Builder setPhotos(List<MultiValuedAttribute> photos) {
            this.photos = photos;
            return this;
        }

        /**
         * Sets the physical mailing addresses for this User (See {@link User#getAddresses()}).
         * 
         * @param addresses
         *        a list of the addresses of the {@link User}
         * @return the builder itself
         */
        public Builder setAddresses(List<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        /**
         * Sets a list of groups that the user belongs to (See {@link User#getGroups()})
         * 
         * @param groups
         *        groups of the User
         * @return the builder itself
         */
        public Builder setGroups(List<MultiValuedAttribute> groups) {
            this.groups = groups;
            return this;
        }

        /**
         * Sets a list of entitlements for the User (See {@link User#getEntitlements()}).
         * 
         * @param entitlements
         *        the entitlements of the {@link User}
         * @return the builder itself
         */
        public Builder setEntitlements(List<MultiValuedAttribute> entitlements) {
            this.entitlements = entitlements;
            return this;
        }

        /**
         * Sets a list of roles for the User (See {@link User#getRoles()}).
         * 
         * @param roles
         *        a list of roles
         * @return the builder itself
         */
        public Builder setRoles(List<MultiValuedAttribute> roles) {
            this.roles = roles;
            return this;
        }

        /**
         * Sets a list of certificates issued to the User (See {@link User#getX509Certificates()}).
         * 
         * @param x509Certificates
         *        the certificates of the {@link User}
         * @return the builder itself
         */
        public Builder setX509Certificates(List<MultiValuedAttribute> x509Certificates) {
            this.x509Certificates = x509Certificates;
            return this;
        }

        /**
         * Sets a List of Extension to the User (See {@link User#getAllExtensions()}).
         * 
         * @param extensions
         *        a list of extensions
         * @return the builder itself
         */
        public Builder addExtensions(Set<Extension> extensions) {
            if (extensions == null) {
                throw new IllegalArgumentException("The given extensions can't be null.");
            }
            for (Extension entry : extensions) {
                this.addExtension(entry);
            }
            return this;
        }

        /**
         * Sets a Extension to the User (See {@link User#getExtension(String)}).
         * 
         * @param extension
         *        a single Extension
         * @return the builder itself
         */
        public Builder addExtension(Extension extension) {
            if (extension == null) {
                throw new IllegalArgumentException("The given extension can't be null.");
            }
            extensions.put(extension.getUrn(), extension);
            schemas.add(extension.getUrn());
            return this;
        }

        @Override
        public Builder setMeta(Meta meta) {
            super.setMeta(meta);
            return this;
        }

        @Override
        public Builder setExternalId(String externalId) {
            super.setExternalId(externalId);
            return this;
        }

        @Override
        public Builder setId(String id) {
            super.setId(id);
            return this;
        }

        @Override
        public Builder setSchemas(Set<String> schemas) {
            super.setSchemas(schemas);
            return this;
        }

        @Override
        public User build() {
            return new User(this);
        }
    }
}
