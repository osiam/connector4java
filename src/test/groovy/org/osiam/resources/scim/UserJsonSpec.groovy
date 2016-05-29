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

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.restassured.path.json.JsonPath
import org.osiam.test.util.DateHelper
import spock.lang.Shared
import spock.lang.Specification

import java.nio.ByteBuffer

import static com.jayway.restassured.path.json.JsonPath.from

class UserJsonSpec extends Specification {

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    def 'A User is correctly serialized'() {
        given:
        User user = new User.Builder()
                .userName('bjensen')
                .externalId('bjensen')
                .setName(new Name.Builder()
                        .setFormatted('Ms. Barbara J Jensen III')
                        .setFamilyName('Jensen')
                        .setGivenName('Barbara').build())
                .displayName('BarbaraJ.')
                .nickName('Barbara')
                .title('Dr.')
                .locale('de')
                .addEmail(new Email.Builder()
                        .setValue('bjensen@example.com')
                        .setType(Email.Type.WORK)
                        .setDisplay('mymail').build())
                .addPhoto(new Photo.Builder()
                        .setValue('example.png')
                        .setDisplay('myphoto').build())
                .addPhoneNumber(new PhoneNumber.Builder()
                        .setValue('555-555-8377')
                        .setType(PhoneNumber.Type.WORK)
                        .setDisplay('myphonenumber').build())
                .addAddress(new Address.Builder()
                        .setType(Address.Type.WORK)
                        .setStreetAddress('example street 42')
                        .setLocality('Bonn')
                        .setRegion('North Rhine-Westphalia')
                        .setPostalCode('11111')
                        .setCountry('Germany')
                        .setDisplay('myaddress').build())
                .addExtension(new Extension.Builder('urn:scim:schemas:extension:test:1.0:User')
                        .setField('keyString', 'example')
                        .setField('keyBoolean', true)
                        .setField('keyInteger', 123G)
                        .setField('keyDecimal', 123.456G)
                        .setField('keyBinary', ByteBuffer.wrap([101, 120, 97, 109, 112, 108, 101] as byte[]))
                        .setField('keyReference', new URI('https://example.com/Users/28'))
                        .setField('keyDateTime', DateHelper.createDate(2008, 0, 23, 18, 29, 49)).build())
                .build()

        when:
        JsonPath json = from(mapper.writeValueAsString(user))

        then:
        json.getList('schemas').containsAll(['urn:ietf:params:scim:schemas:core:2.0:User',
                                             'urn:scim:schemas:extension:test:1.0:User'])
        json.getString('externalId') == 'bjensen'
        json.getString('userName') == 'bjensen'
        json.getString('name.formatted') == 'Ms. Barbara J Jensen III'
        json.getString('name.familyName') == 'Jensen'
        json.getString('name.givenName') == 'Barbara'
        json.getString('displayName') == 'BarbaraJ.'
        json.getString('nickName') == 'Barbara'
        json.getString('title') == 'Dr.'
        json.getString('locale') == 'de'
        json.getString('emails[0].value') == 'bjensen@example.com'
        json.getString('emails[0].type') == 'work'
        json.getString('emails[0].display') == 'mymail'
        json.getString('photos[0].value') == 'example.png'
        json.getString('photos[0].display') == 'myphoto'
        json.getString('phoneNumbers[0].value') == '555-555-8377'
        json.getString('phoneNumbers[0].type') == 'work'
        json.getString('phoneNumbers[0].display') == 'myphonenumber'
        json.getString('addresses[0].type') == 'work'
        json.getString('addresses[0].streetAddress') == 'example street 42'
        json.getString('addresses[0].locality') == 'Bonn'
        json.getString('addresses[0].region') == 'North Rhine-Westphalia'
        json.getString('addresses[0].postalCode') == '11111'
        json.getString('addresses[0].country') == 'Germany'
        json.getString('addresses[0].display') == 'myaddress'
        json.getString('\'urn:scim:schemas:extension:test:1.0:User\'.keyString') == 'example'
        json.getBoolean('\'urn:scim:schemas:extension:test:1.0:User\'.keyBoolean') == true
        json.getInt('\'urn:scim:schemas:extension:test:1.0:User\'.keyInteger') == 123
        json.getDouble('\'urn:scim:schemas:extension:test:1.0:User\'.keyDecimal') == 123.456D
        json.getString('\'urn:scim:schemas:extension:test:1.0:User\'.keyBinary') == 'ZXhhbXBsZQ=='
        json.getString('\'urn:scim:schemas:extension:test:1.0:User\'.keyReference') == 'https://example.com/Users/28'
        json.getString('\'urn:scim:schemas:extension:test:1.0:User\'.keyDateTime') == '2008-01-23T18:29:49.000Z'
    }

}
