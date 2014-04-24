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

import org.osiam.resources.scim.meta.SetAttribute;
import org.osiam.resources.scim.meta.SingularAttribute;

public class Resource_ {  

    protected Resource_(){
    }

    public static SingularAttribute<Resource, String> id = new SingularAttribute<>("id", Resource.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<Resource, String> externalId = new SingularAttribute<>("externalId", Resource.class, String.class); // NOSONAR : not finished yet
    public static SingularAttribute<Resource, Meta> meta = new SingularAttribute<>("meta", Resource.class, Meta.class); // NOSONAR : not finished yet
    public static SetAttribute<Resource, String> schemas = new SetAttribute<>("schemas", Resource.class, String.class); // NOSONAR : not finished yet

}