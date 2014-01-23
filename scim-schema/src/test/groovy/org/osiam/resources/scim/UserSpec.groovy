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
import spock.lang.Unroll

class UserSpec extends Specification {

    static def EXTENSION_URN = "urn:org.osiam:schemas:test:1.0:Test"
    static def EXTENSION_EMPTY = new Extension(EXTENSION_URN)
    static def CORE_SCHEMA_SET = [Constants.USER_CORE_SCHEMA] as Set

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
        user.schemas == CORE_SCHEMA_SET
    }

    def "should be possible to create an user without name for PATCH"() {
        when:
        def user = new User.Builder().build()
        then:
        user.schemas == CORE_SCHEMA_SET
        user.name == null
    }


    def "should be able to contain schemas"() {
        def schemas = ["urn:wtf", "urn:hajo"] as Set
        when:
        User user = new User.Builder("username").setSchemas(schemas).build()
        then:
        user.schemas == schemas

    }

    @Unroll
    def "using #parameter for userName raises exception"() {
        when:
        new User.Builder(parameter)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "userName must not be null or empty."

        where:
        parameter << [null, '']

    }

    def "should generate a user based on builder"() {
        given:
        def entitlement = new Entitlement.Builder().build()
        def role = new Role.Builder().build()
        def certificate = new X509Certificate.Builder().build()
        def meta = new Meta.Builder().build()

        def builder = new User.Builder("test").setActive(true)
                .setDisplayName("display")
                .setLocale("locale")
                .setName(new Name.Builder().build())
                .setNickName("nickname")
                .setPassword("password")
                .setPreferredLanguage("preferredLanguage")
                .setProfileUrl("profileUrl")
                .setTimezone("time")
                .setTitle("title")
                .setUserType("userType")
                .setEntitlements([entitlement] as List)
                .setRoles([role] as List)
                .setX509Certificates([certificate] as List)
                .setId('id')
                .setMeta(meta)
                .setExternalId('external')
                .addExtension(new Extension("urn:org.osiam:schemas:test:1.0:Test"))
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
        user.id == 'id'
        user.meta == meta
        user.externalId == 'external'
        user.extensions == builder.extensions
    }

    def "should be able to enrich addresses, emails, entitlements, groups, phone-numbers, photos, roles and certificates"() {
        given:
        def user = new User.Builder("test2").build()
        def address = new Address.Builder().build()
        def email = new Email.Builder().build()
        def entitlement = new Entitlement.Builder().build()
        def groupRef = new GroupRef.Builder().build()
        def im = new Im.Builder().build()
        def phoneNumber = new PhoneNumber.Builder().build()
        def photo = new Photo.Builder().build()
        def role = new Role.Builder().build()
        def certificate = new X509Certificate.Builder().build()

        when:
        user.getAddresses().add(address)
        user.getEmails().add(email)
        user.getEntitlements().add(entitlement)
        user.getGroups().add(groupRef)
        user.getIms().add(im)
        user.getPhoneNumbers().add(phoneNumber)
        user.getPhotos().add(photo)
        user.getRoles().add(role)
        user.getX509Certificates().add(certificate)

        then:
        user.addresses.get(0) == address
        user.emails.get(0) == email
        user.entitlements.get(0) == entitlement
        user.groups.get(0) == groupRef
        user.ims.get(0) == im
        user.phoneNumbers.get(0) == phoneNumber
        user.photos.get(0) == photo
        user.roles.get(0) == role
        user.x509Certificates.get(0) == certificate
    }

    @Unroll
    def 'creating a user with copy builder copies #field field if present'() {
        given:
        def user = new User.Builder('user').build()

        when:
        user[(field)] = value
        def copiedUser = new User.Builder(user).build()

        then:
        copiedUser[(field)] == value

        where:
        field              | value
        'emails'           | [new Email.Builder().build()]
        'phoneNumbers'     | [new PhoneNumber.Builder().build()]
        'ims'              | [new Im.Builder().build()]
        'photos'           | [new Photo.Builder().build()]
        'addresses'        | [new Address.Builder().build()]
        'groups'           | [new GroupRef.Builder().build()]
        'entitlements'     | [new Entitlement.Builder().build()]
        'roles'            | [new Role.Builder().build()]
        'x509Certificates' | [new X509Certificate.Builder().build()]
        'extensions'       | [(EXTENSION_URN): (EXTENSION_EMPTY)]
    }

    @Unroll
    def 'creating a user with copy builder initializes #field empty if missing in original'() {
        given:
        def user = new User.Builder('user').build()

        when:
        user[(field)] = value
        def copiedUser = new User.Builder(user).build()

        then:
        copiedUser[(field)] == value

        where:
        field              | value
        'emails'           | []
        'phoneNumbers'     | []
        'ims'              | []
        'photos'           | []
        'addresses'        | []
        'groups'           | []
        'entitlements'     | []
        'roles'            | []
        'x509Certificates' | []
        'extensions'       | [:]
    }

    def 'enriching extension using the getter raises exception'() {
        given:
        def user = new User.Builder("test2").build()
        when:
        user.getAllExtensions().put(EXTENSION_URN, EXTENSION_EMPTY)
        then:
        thrown(UnsupportedOperationException)
    }

    def 'builder should add a schema to the schema Set for each added extension'() {
        given:
        def extension1Urn = "urn:org.osiam:schemas:test:1.0:Test1"
        def extension1 = new Extension(extension1Urn)
        def extension2Urn = "urn:org.osiam:schemas:test:1.0:Test2"
        def extension2 = new Extension(extension2Urn)
        when:
        def user = new User.Builder("test2")
                .addExtension(extension1)
                .addExtension(extension2)
                .build()
        then:
        user.schemas.contains(extension1Urn)
        user.schemas.contains(extension2Urn)
    }

    def 'scim core schema must always be present in schema set when adding extensions'() {
        given:
        def extension1Urn = "urn:org.osiam:schemas:test:1.0:Test1"
        def extension1 = new Extension(extension1Urn)
        def extension2Urn = "urn:org.osiam:schemas:test:1.0:Test2"
        def extension2 = new Extension(extension2Urn)
        def coreSchemaUrn = Constants.USER_CORE_SCHEMA

        when:
        def user = new User.Builder("test2")
                .addExtension(extension1)
                .addExtension(extension2)
                .build()
        then:
        user.schemas.contains(coreSchemaUrn)
    }

    def 'an added extension can be retrieved'() {
        given:
        def user = new User.Builder("test2")
                .addExtension(EXTENSION_EMPTY)
                .build()
        expect:
        user.getExtension(EXTENSION_URN) == EXTENSION_EMPTY
    }

    def 'extensions can be added in bulk'() {
        given:
        def extensions = new HashSet<Extension>()
        extensions.add(EXTENSION_EMPTY)
        def user = new User.Builder("test")
                //.addExtensions([(EXTENSION_URN): EXTENSION_EMPTY])
                .addExtensions(extensions)
                .build()

        expect:
        user.getExtension(EXTENSION_URN) == EXTENSION_EMPTY

    }

    @Unroll
    def 'retrieving extension with #testCase urn raises exception'() {
        given:
        def user = new User.Builder("test2")
                .build()
        when:
        user.getExtension(urn)
        then:
        thrown(expectedException)

        where:
        testCase  | urn           | expectedException
        'null'    | null          | IllegalArgumentException
        'empty'   | ''            | IllegalArgumentException
        'invalid' | EXTENSION_URN | NoSuchElementException
    }

    def 'using the copy-of builder with null as parameter raises exception'() {
        when:
        new User.Builder(null)

        then:
        thrown(IllegalArgumentException)
    }

}
