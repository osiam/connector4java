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
package org.osiam.client.user;

import java.util.Date;

import org.osiam.resources.helper.JsonDateSerializer;
import org.osiam.resources.scim.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * represents the basic Data of a scim User
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "link", "gender", "timezone", "verified" })
public class BasicUser {

    private String id;
    private String name;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String userName;
    private String email;
    private String locale;
    @JsonProperty("updated_time")
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date updatedTime;

    private BasicUser() {
    }

    /**
     * Gets the id of the User
     *
     * @return the id of the User
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the formatted name property
     *
     * @return the formatted name
     */
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    /**
     * Gets the given name of the {@link User}
     *
     * @return the given name
     */
    public String getFirstName() {
        if (firstName == null) {
            firstName = "";
        }
        return firstName;
    }

    /**
     * Gets the last name of the {@link User}
     *
     * @return the last name
     */
    public String getLastName() {
        if (lastName == null) {
            lastName = "";
        }
        return lastName;
    }

    /**
     * Gets the primary email address of the {@link User}
     *
     * @return the primary email address
     */
    public String getEmail() {
        if (email == null) {
            email = "";
        }
        return email;
    }

    /**
     * Gets the userName of the {@link User}
     *
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the date where the {@link User} was last updated
     *
     * @return the last updated date
     */
    public Date getUpdatedTime() {
        return new Date(updatedTime.getTime());
    }

    /**
     * Used to indicate the User's default location for purposes of localizing items such as currency, date time format,
     * numerical representations, etc. A locale value is a concatenation of the ISO 639-1 two letter language code, an
     * underscore, and the ISO 3166-1 2 letter country code; e.g., 'en_US' specifies the language English and country US
     *
     * @return the local setting of the {@link User}
     */
    public String getLocale() {
        if (locale == null) {
            locale = "";
        }
        return locale;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BasicUser other = (BasicUser) obj;
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (locale == null) {
            if (other.locale != null) {
                return false;
            }
        } else if (!locale.equals(other.locale)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (updatedTime == null) {
            if (other.updatedTime != null) {
                return false;
            }
        } else if (!updatedTime.equals(other.updatedTime)) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((updatedTime == null) ? 0 : updatedTime.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

}
