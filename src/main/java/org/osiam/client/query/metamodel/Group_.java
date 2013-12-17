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

package org.osiam.client.query.metamodel;

/**
 * all fields from a Group
 */
public abstract class Group_ {

    private Group_(){}

    public static final StringAttribute id = new StringAttribute("id");    // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute displayName = new StringAttribute("displayName");      // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    public static final StringAttribute externalId = new StringAttribute("externalId");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'

    /**
     * all meta attributes from a User
     */
    public abstract static class Meta {
        public static final StringAttribute resourceType = new StringAttribute("meta.resourceType");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute created = new DateAttribute("meta.created");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final DateAttribute lastModified = new DateAttribute("meta.lastModified"); // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute location = new StringAttribute("meta.location");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute version = new StringAttribute("meta.version");   // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }

    /**
     * all Member attributes from a Group
     */
    public abstract static class Members {
        public static final StringAttribute value = new StringAttribute("members.value");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute type = new StringAttribute("members.type");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
        public static final StringAttribute display = new StringAttribute("members.display");  // NOSONAR constance name doesn't has to be like '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    }
}
