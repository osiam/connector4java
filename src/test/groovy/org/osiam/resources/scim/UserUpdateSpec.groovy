/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.scim

import spock.lang.Specification

class UserUpdateSpec extends Specification {

    def 'can update simple, singular attributes'() {
        given:
        def originalUser = new User.Builder()
                .setExternalId('externalId')
                .setUserName('userName')
                .setDisplayName('displayName')
                .setNickName('nickName')
                .setProfileUrl('profileUrl')
                .setTitle('title')
                .setUserType('userType')
                .setPreferredLanguage('preferredLanguage')
                .setLocale('locale')
                .setTimezone('timezone')
                .setActive(true)
                .build()

        when:
        def user = new User.Builder(originalUser)
                .setExternalId('newExternalId')
                .setUserName('newUserName')
                .setDisplayName('newDisplayName')
                .setNickName('newNickName')
                .setProfileUrl('newProfileUrl')
                .setTitle('newTitle')
                .setUserType('newUserType')
                .setPreferredLanguage('newPreferredLanguage')
                .setLocale('newLocale')
                .setTimezone('newTimezone')
                .setActive(false)
                .build()

        then:
        with(user) {
            externalId == 'newExternalId'
            userName == 'newUserName'
            displayName == 'newDisplayName'
            nickName == 'newNickName'
            profileUrl == 'newProfileUrl'
            title == 'newTitle'
            userType == 'newUserType'
            preferredLanguage == 'newPreferredLanguage'
            locale == 'newLocale'
            timezone == 'newTimezone'
            !active
        }
    }

    def 'can update name attribute'() {
        given:
        def originalUser = new User.Builder()
                .setName(new Name.Builder()
                        .setFormatted('formatted')
                        .setFamilyName('familyName')
                        .setGivenName('givenName')
                        .setMiddleName('middleName')
                        .setHonorificPrefix('honorificPrefix')
                        .setHonorificSuffix('honorificSuffix')
                        .build())
                .build()

        when:
        def user = new User.Builder(originalUser)
                .setName(new Name.Builder(originalUser.getName())
                        .setFamilyName('newFamilyName')
                        .setGivenName(null)
                        .setHonorificPrefix('newHonorificPrefix')
                        .setHonorificSuffix(null)
                        .build())
                .build()

        then:
        with(user.name) {
            formatted == 'formatted'
            familyName == 'newFamilyName'
            givenName == null
            middleName == 'middleName'
            honorificPrefix == 'newHonorificPrefix'
            honorificSuffix == null
        }
    }

    def 'can update emails'() {
        given:
        def originalUser = new User.Builder()
                .addEmails([
                        new Email.Builder()
                                .setValue('value1@example.com')
                                .setType(Email.Type.WORK)
                                .build(),
                        new Email.Builder()
                                .setValue('value2@example.com')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeEmail(originalUser.emails[0])
                .addEmail(new Email.Builder(originalUser.emails[0])
                        .setValue('newValue1@example.com')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addEmail(new Email.Builder()
                .setValue('newValue2@example.com')
                .setType(Email.Type.HOME)
                .build())

        // Removing
        userBuilder.removeEmail(originalUser.emails[1])

        def user = userBuilder.build()

        then:
        user.emails.size() == 2
        with(user.emails[0]) {
            value == 'newValue1@example.com'
            type == Email.Type.WORK
            primary
        }
        with(user.emails[1]) {
            value == 'newValue2@example.com'
            type == Email.Type.HOME
            !primary
        }
    }

    def 'can update phone numbers'() {
        given:
        def originalUser = new User.Builder()
                .addPhoneNumbers([
                        new PhoneNumber.Builder()
                                .setValue('202-555-0001')
                                .setType(PhoneNumber.Type.WORK)
                                .build(),
                        new PhoneNumber.Builder()
                                .setValue('202-555-0002')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removePhoneNumber(originalUser.phoneNumbers[0])
                .addPhoneNumber(new PhoneNumber.Builder(originalUser.phoneNumbers[0])
                        .setValue('202-555-1001')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addPhoneNumber(new PhoneNumber.Builder()
                .setValue('202-555-1002')
                .setType(PhoneNumber.Type.HOME)
                .build())

        // Removing
        userBuilder.removePhoneNumber(originalUser.phoneNumbers[1])

        def user = userBuilder.build()

        then:
        user.phoneNumbers.size() == 2
        with(user.phoneNumbers[0]) {
            value == '202-555-1001'
            type == PhoneNumber.Type.WORK
            primary
        }
        with(user.phoneNumbers[1]) {
            value == '202-555-1002'
            type == PhoneNumber.Type.HOME
            !primary
        }
    }

    def 'can update IMs'() {
        given:
        def originalUser = new User.Builder()
                .addIms([
                        new Im.Builder()
                                .setValue('account1')
                                .setType(Im.Type.XMPP)
                                .build(),
                        new Im.Builder()
                                .setValue('account2')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeIm(originalUser.ims[0])
                .addIm(new Im.Builder(originalUser.ims[0])
                        .setValue('newAccount1')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addIm(new Im.Builder()
                .setValue('newAccount2')
                .setType(Im.Type.QQ)
                .build())

        // Removing
        userBuilder.removeIm(originalUser.ims[1])

        def user = userBuilder.build()

        then:
        user.ims.size() == 2
        with(user.ims[0]) {
            value == 'newAccount1'
            type == Im.Type.XMPP
            primary
        }
        with(user.ims[1]) {
            value == 'newAccount2'
            type == Im.Type.QQ
            !primary
        }
    }

    def 'can update photos'() {
        given:
        def originalUser = new User.Builder()
                .addPhotos([
                        new Photo.Builder()
                                .setValue('example.com/photo1')
                                .setType(Photo.Type.PHOTO)
                                .build(),
                        new Photo.Builder()
                                .setValue('example.com/photo2')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removePhoto(originalUser.photos[0])
                .addPhoto(new Photo.Builder(originalUser.photos[0])
                        .setValue('example.com/newPhoto1')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addPhoto(new Photo.Builder()
                .setValue('example.com/newPhoto2')
                .setType(Photo.Type.THUMBNAIL)
                .build())

        // Removing
        userBuilder.removePhoto(originalUser.photos[1])

        def user = userBuilder.build()

        then:
        user.photos.size() == 2
        with(user.photos[0]) {
            value == 'example.com/newPhoto1'
            type == Photo.Type.PHOTO
            primary
        }
        with(user.photos[1]) {
            value == 'example.com/newPhoto2'
            type == Photo.Type.THUMBNAIL
            !primary
        }
    }

    def 'can update addresses'() {
        given:
        def originalUser = new User.Builder()
                .addAddresses([
                        new Address.Builder()
                                .setStreetAddress('streetAddress1')
                                .setPostalCode('postalCode1')
                                .setLocality('locality1')
                                .setRegion('region1')
                                .setCountry('country1')
                                .setType(Address.Type.WORK)
                                .build(),
                        new Address.Builder()
                                .setStreetAddress('streetAddress2')
                                .setPostalCode('postalCode2')
                                .setLocality('locality2')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeAddress(originalUser.addresses[0])
                .addAddress(new Address.Builder(originalUser.addresses[0])
                        .setStreetAddress('newStreetAddress1')
                        .setPostalCode(null)
                        .setRegion('newRegion1')
                        .setType(Address.Type.OTHER)
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addAddress(new Address.Builder()
                .setStreetAddress('newStreetAddress2')
                .setPostalCode('newPostalCode2')
                .setLocality('newLocality2')
                .setType(Address.Type.HOME)
                .build())

        // Removing
        userBuilder.removeAddress(originalUser.addresses[1])

        def user = userBuilder.build()

        then:
        user.addresses.size() == 2
        with(user.addresses[0]) {
            streetAddress == 'newStreetAddress1'
            postalCode == null
            locality == 'locality1'
            region == 'newRegion1'
            country == 'country1'
            type == Address.Type.OTHER
            primary
        }
        with(user.addresses[1]) {
            streetAddress == 'newStreetAddress2'
            postalCode == 'newPostalCode2'
            locality == 'newLocality2'
            region == null
            country == null
            type == Address.Type.HOME
            !primary
        }
    }

    def 'can update entitlements'() {
        given:
        def originalUser = new User.Builder()
                .addEntitlements([
                    new Entitlement.Builder()
                            .setValue('entitlement1')
                            .setType(new Entitlement.Type('type1'))
                            .build(),
                    new Entitlement.Builder()
                            .setValue('entitlement2')
                            .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeEntitlement(originalUser.entitlements[0])
                .addEntitlement(new Entitlement.Builder(originalUser.entitlements[0])
                        .setValue('newEntitlement1')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addEntitlement(new Entitlement.Builder()
                .setValue('newEntitlement2')
                .setType(new Entitlement.Type('newType2'))
                .build())

        // Removing
        userBuilder.removeEntitlement(originalUser.entitlements[1])

        def user = userBuilder.build()

        then:
        user.entitlements.size() == 2
        with(user.entitlements[0]) {
            value == 'newEntitlement1'
            type == new Entitlement.Type('type1')
            primary
        }
        with(user.entitlements[1]) {
            value == 'newEntitlement2'
            type == new Entitlement.Type('newType2')
            !primary
        }
    }

    def 'can update roles'() {
        given:
        def originalUser = new User.Builder()
                .addRoles([
                    new Role.Builder()
                            .setValue('role1')
                            .setType(new Role.Type('type1'))
                            .build(),
                    new Role.Builder()
                            .setValue('role2')
                            .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeRole(originalUser.roles[0])
                .addRole(new Role.Builder(originalUser.roles[0])
                    .setValue('newRole1')
                    .setPrimary(true)
                    .build())

        // Adding
        userBuilder.addRole(new Role.Builder()
                .setValue('newRole2')
                .setType(new Role.Type('newType2'))
                .build())

        // Removing
        userBuilder.removeRole(originalUser.roles[1])

        def user = userBuilder.build()

        then:
        user.roles.size() == 2
        with(user.roles[0]) {
            value == 'newRole1'
            type == new Role.Type('type1')
            primary
        }
        with(user.roles[1]) {
            value == 'newRole2'
            type == new Role.Type('newType2')
            !primary
        }
    }

    def 'can update x509Certificates'() {
        given:
        def originalUser = new User.Builder()
                .addX509Certificates([
                        new X509Certificate.Builder()
                                .setValue('x509Certificate1')
                                .setType(new X509Certificate.Type('type1'))
                                .build(),
                        new X509Certificate.Builder()
                                .setValue('x509Certificate2')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeX509Certificate(originalUser.x509Certificates[0])
                .addX509Certificate(new X509Certificate.Builder(originalUser.x509Certificates[0])
                        .setValue('newX509Certificate1')
                        .setPrimary(true)
                        .build())

        // Adding
        userBuilder.addX509Certificate(new X509Certificate.Builder()
                .setValue('newX509Certificate2')
                .setType(new X509Certificate.Type('newType2'))
                .build())

        // Removing
        userBuilder.removeX509Certificate(originalUser.x509Certificates[1])

        def user = userBuilder.build()

        then:
        user.x509Certificates.size() == 2
        with(user.x509Certificates[0]) {
            value == 'newX509Certificate1'
            type == new X509Certificate.Type('type1')
            primary
        }
        with(user.x509Certificates[1]) {
            value == 'newX509Certificate2'
            type == new X509Certificate.Type('newType2')
            !primary
        }
    }

    def 'can update extensions'(){
        given:
        def originalUser = new User.Builder()
                .addExtensions([
                        new Extension.Builder('urn1')
                                .setField('field1', 'value1')
                                .setField('field2', 'value2')
                                .setField('field3', 'value3')
                                .build(),
                        new Extension.Builder('urn2')
                                .setField('field4', 'value4')
                                .build(),
                ])
                .build()


        when:
        def userBuilder = new User.Builder(originalUser)

        // Updating
        userBuilder.removeExtension('urn1')
                .addExtension(new Extension.Builder(originalUser.getExtension('urn1'))
                        .setField('field1', 'newValue1')
                        .removeField('field2')
                        .build())

        // Adding
        userBuilder.addExtension(new Extension.Builder('urn2')
                .setField('field1', 'newValue2')
                .setField('field2', 'newValue3')
                .build())

        // Removing
        userBuilder.removeX509Certificate(originalUser.x509Certificates[1])

        def user = userBuilder.build()

        then:
        user.extensions.size() == 2
        with(user.getExtension('urn1')) {
            getFieldAsString('field1') == 'newValue1'
            !isFieldPresent('field2')
            getFieldAsString('field3') == 'value3'
        }
        with(user.getExtension('urn2')) {
        }
    }
}
