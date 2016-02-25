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
package org.osiam.resources.helper;

import com.google.common.base.Optional;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.User;

/**
 * This class is a collection of different helper methods around the scim schema context
 * This class has been deprecated. If You want to use the only method it contains please see
 * {@link User#getPrimaryOrFirstEmail()}
 */
@Deprecated
public class SCIMHelper {


    /**
     * try to extract an email from the User.
     * If the User has a primary email address this email will be returned.
     * If not the first email address found will be returned.
     * If no Email has been found email.isPresent() == false
     *
     * @param user a {@link User} with a possible email
     * @return an email if found
     * @deprecated Please use the method {@link User#getPrimaryOrFirstEmail()}
     */
    @Deprecated
    public static Optional<Email> getPrimaryOrFirstEmail(User user) {
        for (Email email : user.getEmails()) {
            if (email.isPrimary()) {
                return Optional.of(email);
            }
        }

        if (user.getEmails().size() > 0) {
            return Optional.of(user.getEmails().get(0));
        }
        return Optional.absent();
    }
}
