package org.osiam.client.update;

import org.osiam.client.query.metamodel.Attribute;
import org.osiam.client.query.metamodel.User_;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;

import java.util.*;

public class UpdateUser{

    private User user;
    private UpdateUser(Builder builder){
        user = builder.updateUser.build();
    }

    public User getUserToUpdate(){
        return user;
    }

    public static class Builder{

        User.Builder updateUser = null;
        Set<String> deleteFields = new HashSet<>();
        List<MultiValuedAttribute> emails = new ArrayList<>();

        public Builder(){
            updateUser = new User.Builder();
        }

        public Builder(String userName){
            updateUser = new User.Builder(userName);
        }

        public void deleteNickname(){
            deleteFields.add(User_.nickName.toString());
        }

        public void updateNickname(String nickName){
            updateUser.setNickName(nickName);
        }

        public void deleteEmails(){
            deleteFields.add("emails");
        }

        public void deleteEmail(MultiValuedAttribute email){
            MultiValuedAttribute delteEmail = new MultiValuedAttribute.Builder()
                    .setValue(email.getValue())
                    .setType(email.getType())
                    .setDisplay(email.getDisplay())
                    .setPrimary(email.isPrimary())
                    .setOperation("delete").build();
            emails.add(delteEmail);
        }

        public void updateEmail(MultiValuedAttribute email){
            emails.add(email);
        }

        public void addEmail(MultiValuedAttribute email){
            emails.add(email);
        }

        public UpdateUser build(){
            Meta meta = new Meta.Builder().setAttributes(deleteFields).build();
            updateUser.setMeta(meta);
            updateUser.setEmails(emails);
            return new UpdateUser(this);
        }
    }
}
