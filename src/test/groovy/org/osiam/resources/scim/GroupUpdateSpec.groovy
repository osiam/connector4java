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

class GroupUpdateSpec extends Specification {

    def 'can update simple, singular attributes'() {
        given:
        def originalGroup = new Group.Builder()
                .setExternalId('externalId')
                .setDisplayName('displayName')
                .build()

        when:
        def group = new Group.Builder(originalGroup)
                .setExternalId('newExternalId')
                .setDisplayName('newDisplayName')
                .build()

        then:
        with(group) {
            externalId == 'newExternalId'
            displayName == 'newDisplayName'
        }
    }

    def 'can update members'() {
        given:
        def originalGroup = new Group.Builder()
                .addMembers([
                        new MemberRef.Builder()
                                .setValue('8b554d62-29cc-11e6-82db-e82aea766790')
                                .setDisplay('display1')
                                .setType(MemberRef.Type.USER)
                                .build(),
                        new MemberRef.Builder()
                                .setValue('8abd8680-29cc-11e6-b2f7-e82aea766790')
                                .setType(MemberRef.Type.USER)
                                .build(),
                ])
                .build()


        when:
        def groupBuilder = new Group.Builder(originalGroup)

        // Updating
        groupBuilder.removeMember(originalGroup.members[0])
                .addMember(new MemberRef.Builder(originalGroup.members[0])
                        .setValue('d869f486-29cc-11e6-a76a-e82aea766790')
                        .setDisplay(null)
                        .build())

        // Adding
        groupBuilder.addMember(new MemberRef.Builder()
                .setValue('e0f07f9e-29cc-11e6-b251-e82aea766790')
                .setDisplay('newDisplay1')
                .setType(MemberRef.Type.GROUP)
                .build())

        // Removing
        groupBuilder.removeMember(originalGroup.members[1])

        def group = groupBuilder.build()

        then:
        group.members.size() == 2
        with(group.members[0]) {
            value == 'd869f486-29cc-11e6-a76a-e82aea766790'
            display == null
            type == MemberRef.Type.USER
        }
        with(group.members[1]) {
            value == 'e0f07f9e-29cc-11e6-b251-e82aea766790'
            display == 'newDisplay1'
            type == MemberRef.Type.GROUP
        }
    }
}
