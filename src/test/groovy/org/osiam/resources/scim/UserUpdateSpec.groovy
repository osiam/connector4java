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
}
