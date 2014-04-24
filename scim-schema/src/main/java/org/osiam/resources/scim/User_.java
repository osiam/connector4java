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

import org.osiam.resources.scim.meta.MapAttribute;
import org.osiam.resources.scim.meta.SetAttribute;
import org.osiam.resources.scim.meta.SingularAttribute;

public class User_ extends Resource_ {
    
    private User_(){
    }

    public static SingularAttribute<User, String> userName = new SingularAttribute<>("userName", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, Name> name = new SingularAttribute<>("name", User.class, Name.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> displayName = new SingularAttribute<>("displayName", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> nickName = new SingularAttribute<>("nickName", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> profileUrl = new SingularAttribute<>("profileUrl", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> title = new SingularAttribute<>("title", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> userType = new SingularAttribute<>("userType", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> preferredLanguage = new SingularAttribute<>("preferredLanguage", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> locale = new SingularAttribute<>("locale", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> timezone = new SingularAttribute<>("timezone", User.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, Boolean> active = new SingularAttribute<>("active", User.class, Boolean.class); // NOSONAR : not finished yet
    public static SingularAttribute<User, String> password = new SingularAttribute<>("password", User.class, String.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> emails = new SetAttribute<>("emails", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> phoneNumbers = new SetAttribute<>("phoneNumbers", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> ims = new SetAttribute<>("ims", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> photos = new SetAttribute<>("photos", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, Address> addresses = new SetAttribute<>("addresses", User.class, Address.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> groups = new SetAttribute<>("groups", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> entitlements = new SetAttribute<>("entitlements", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> roles = new SetAttribute<>("roles", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static SetAttribute<User, MultiValuedAttribute> x509Certificates = new SetAttribute<>("x509Certificates", User.class, MultiValuedAttribute.class); // NOSONAR : not finished yet
    public static MapAttribute<User, String, Extension> extensions = new MapAttribute<>("extensions", User.class, Extension.class); // NOSONAR : not finished yet
}