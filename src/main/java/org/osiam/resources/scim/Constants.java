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
package org.osiam.resources.scim;

/**
 * @deprecated These constants have been moved to their corresponding classes. Will be removed in 1.11 or 2.0.
 */
@Deprecated
public interface Constants {

    /**
     * @deprecated Please use {@link User}.SCHEMA. Will be removed in 1.11 or 2.0.
     */
    String USER_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";
    /**
     * @deprecated Please use {@link Group}.SCHEMA. Will be removed in 1.11 or 2.0.
     */
    String GROUP_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";
    /**
     * @deprecated Please use {@link SCIMSearchResult}.SCHEMA. Will be removed in 1.11 or 2.0.
     */
    String LIST_RESPONSE_CORE_SCHEMA = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    /**
     * @deprecated This constant is going to be removed in 1.11 or 2.0.
     */
    String SERVICE_PROVIDER_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";
    /**
     * @deprecated This constant is going to be removed in 1.11 or 2.0.
     */
    int MAX_RESULT = 100;
}
