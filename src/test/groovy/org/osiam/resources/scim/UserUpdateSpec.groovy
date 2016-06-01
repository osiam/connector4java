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
                .externalId('externalId')
                .userName('userName')
                .displayName('displayName')
                .nickName('nickName')
                .profileUrl('profileUrl')
                .title('title')
                .userType('userType')
                .preferredLanguage('preferredLanguage')
                .locale('locale')
                .timezone('timezone')
                .active(true)
                .build()

        when:
        def user = new User.Builder(originalUser)
                .externalId('newExternalId')
                .userName('newUserName')
                .displayName('newDisplayName')
                .nickName('newNickName')
                .profileUrl('newProfileUrl')
                .title('newTitle')
                .userType('newUserType')
                .preferredLanguage('newPreferredLanguage')
                .locale('newLocale')
                .timezone('newTimezone')
                .active(false)
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
}
