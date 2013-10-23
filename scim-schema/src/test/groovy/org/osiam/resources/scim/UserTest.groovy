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

package org.osiam.resources.scim

import spock.lang.Specification

class UserTest extends Specification {

    def "default constructor should be present due to json mappings"() {
        when:
        def user = new User()

        then:
        user != null
    }

    def "should contain core schemas as default"() {
        when:
        def user = new User.Builder("username").build()
        then:
        user.schemas == Constants.CORE_SCHEMAS;
    }

    def "should be possible to create an user without name for PATCH"() {
        when:
        def user = new User.Builder().build()
        then:
        user.schemas == Constants.CORE_SCHEMAS;
        user.name == null
    }


    def "should be able to contain schemas"() {
        def schemas = ["urn:wtf", "urn:hajo"] as Set
        when:
        User user = new User.Builder("username").setSchemas(schemas).build()
        then:
        user.schemas == schemas

    }

    def "generateForOutput should copy schemas"() {
        def schemas = ["urn:wtf", "urn:hajo"] as Set
        User oldUser = new User.Builder("username").setSchemas(schemas).build()
        when:
        User user = User.Builder.generateForOutput(oldUser);
        then:
        user.schemas == oldUser.schemas

    }

    def "using null for userName raises exception"() {
        when:
        new User.Builder(null)
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "userName must not be null."
    }


    def "should generate a user based on builder"() {
        given:
        def multivalueAttribute = new MultiValuedAttribute.Builder().build()

        def builder = new User.Builder("test").setActive(true)
                .setDisplayName("display")
                .setLocale("locale")
                .setName(new Name.Builder().build())
                .setNickName("nickname")
                .setPassword("password")
                .setPreferredLanguage("prefereedLanguage")
                .setProfileUrl("profileUrl")
                .setTimezone("time")
                .setTitle("title")
                .setUserType("userType")
                .setEntitlements([multivalueAttribute] as List)
                .setRoles([multivalueAttribute] as List)
                .setX509Certificates([multivalueAttribute] as List)
                .setExternalId("externalid").setId("id").setMeta(new Meta.Builder().build())
        when:
        User user = builder.build()
        then:
        user.active == builder.active
        user.addresses == builder.addresses
        user.displayName == builder.displayName
        user.emails == builder.emails
        user.entitlements == builder.entitlements
        user.groups == builder.groups
        user.ims == builder.ims
        user.locale == builder.locale
        user.name == builder.name
        user.nickName == builder.nickName
        user.password == builder.password
        user.phoneNumbers == builder.phoneNumbers
        user.photos == builder.photos
        user.preferredLanguage == builder.preferredLanguage
        user.profileUrl == builder.profileUrl
        user.roles == builder.roles
        user.timezone == builder.timezone
        user.title == builder.title
        user.userType == builder.userType
        user.x509Certificates == builder.x509Certificates
        user.userName == builder.userName
        user.id == builder.id
        user.externalId == builder.externalId
        user.meta == builder.meta
    }

    def "generateForOutput should remove the password"() {
        given:
        User user = new User.Builder("test")
                .setPassword("password")
                .build()
        when:
        User clonedUser = User.Builder.generateForOutput(user)
        then:
        clonedUser.password == null
    }

    def "should set empty lists for pretty output"() {
        given:
        User user = new User.Builder("test").build()
        when:
        User clonedUser = User.Builder.generateForOutput(user)
        then:
        clonedUser.addresses.empty
        clonedUser.emails.empty
        clonedUser.entitlements.empty
        clonedUser.groups.empty
        clonedUser.ims.empty
        clonedUser.phoneNumbers.empty
        clonedUser.photos.empty
        clonedUser.roles.empty
        clonedUser.x509Certificates.empty
    }

    def "generateForOutput should copy lists from original user"() {
        given:
        def address = new Address.Builder().build()
        def generalAttribute = new MultiValuedAttribute.Builder().build()

        User user = new User.Builder("test")
            .setAddresses([address])
            .setEmails([generalAttribute])
            .setEntitlements([generalAttribute])
            .setGroups([generalAttribute])
            .setIms([generalAttribute])
            .setPhoneNumbers([generalAttribute])
            .setPhotos([generalAttribute])
            .setRoles([generalAttribute])
            .setX509Certificates([generalAttribute])
            .build()

        when:
        User clonedUser = User.Builder.generateForOutput(user)
        then:
        clonedUser.addresses.get(0) == address
        clonedUser.emails.get(0) == generalAttribute
        clonedUser.entitlements.get(0) == generalAttribute
        clonedUser.groups.get(0) == generalAttribute
        clonedUser.ims.get(0) == generalAttribute
        clonedUser.phoneNumbers.get(0) == generalAttribute
        clonedUser.photos.get(0) == generalAttribute
        clonedUser.roles.get(0) == generalAttribute
        clonedUser.x509Certificates.get(0) == generalAttribute

    }


    def "should be able to enrich addresses, emails, entitlements, groups, phone-numbers, photos, roles and certificates"() {
        given:
        def user = new User.Builder("test2").build()
        def address = new Address.Builder().build()
        def generalAttribute = new MultiValuedAttribute.Builder().build()

        when:
        user.getAddresses().add(address)
        user.getEmails().add(generalAttribute)
        user.getEntitlements().add(generalAttribute)
        user.getGroups().add(generalAttribute)
        user.getIms().add(generalAttribute)
        user.getPhoneNumbers().add(generalAttribute)
        user.getPhotos().add(generalAttribute)
        user.getRoles().add(generalAttribute)
        user.getX509Certificates().add(generalAttribute)

        then:
        user.addresses.get(0) == address
        user.emails.get(0) == generalAttribute
        user.entitlements.get(0) == generalAttribute
        user.groups.get(0) == generalAttribute
        user.ims.get(0) == generalAttribute
        user.phoneNumbers.get(0) == generalAttribute
        user.photos.get(0) == generalAttribute
        user.roles.get(0) == generalAttribute
        user.x509Certificates.get(0) == generalAttribute
    }
    
}
