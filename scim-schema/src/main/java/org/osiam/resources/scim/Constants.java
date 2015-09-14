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

/**
 * Some needed Constants
 *
 * @deprecated This interface has been deprecated and is going to be removed in a future release.
 *
 */
@Deprecated
public interface Constants {

    /**
     * @deprecated please use {@link User}.SCHEMA
     */
    String USER_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";
    /**
     * @deprecated please use {@link Group}.SCHEMA
     */
    String GROUP_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";
    /**
     * @deprecated please use {@link SCIMSearchResult}.SCHEMA
     */
    String LIST_RESPONSE_CORE_SCHEMA = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    /**
     * @deprecated  This constant is going to be removed soon.
     */
    String SERVICE_PROVIDER_CORE_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig";
    /**
     * @deprecated  This constant is going to be removed soon.
     */
    int MAX_RESULT = 100;
}
