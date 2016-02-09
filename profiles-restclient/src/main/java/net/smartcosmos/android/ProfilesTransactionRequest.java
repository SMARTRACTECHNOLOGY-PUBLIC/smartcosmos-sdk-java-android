package net.smartcosmos.android;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMART COSMOS Profiles RestClient for Android
 * ===============================================================================
 * Copyright (C) 2015 - 2016 Smartrac Technology Fletcher, Inc.
 * ===============================================================================
 * SMART COSMOS SDK
 * (C) Copyright 2015, Smartrac Technology Fletcher, Inc.
 * 267 Cane Creek Rd, Fletcher, NC, 28732, USA
 * All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
 */

import java.util.Map;

public class ProfilesTransactionRequest {

    static final String DATATYPE_STRING = "StringType";
    static final String TAG_PREFIX = "urn:uuid:smartrac-group:tag:";
    static final String TAG_TYPE = "Tag";

    private AccountEntity account;
    private ObjectEntity[] objects;
    private AddressEntity[] objectAddresses;
    private RelationshipEntity[] relationships;
    private MetadataEntity[] metadata;

    public ProfilesTransactionRequest(String accountDomain) {
        account = new AccountEntity();
        account.name = accountDomain;
        account.moniker = "*";
        objects = new ObjectEntity[0];
        objectAddresses = new AddressEntity[0];
        relationships = new RelationshipEntity[0];
        metadata = new MetadataEntity[0];
    }

    public void addTag(String tagId) {
        ObjectEntity[] tmpObjects = new ObjectEntity[objects.length + 1];
        System.arraycopy(objects, 0, tmpObjects, 0, objects.length);
        tmpObjects[objects.length].objectUrn = TAG_PREFIX + tagId;
        tmpObjects[objects.length].type = TAG_TYPE;
        tmpObjects[objects.length].name = tagId;
        objects = tmpObjects.clone();
    }

    public void addTagData(String tagId, Map<String, String> keyValueMap)
            throws IllegalArgumentException {
        boolean validTagId = false;
        for (ObjectEntity o : objects) {
            if (o.objectUrn.equalsIgnoreCase(TAG_PREFIX + tagId)) {
                validTagId = true;
                break;
            }
        }
        if (!validTagId) {
            throw new IllegalArgumentException("Cannot add data to absent tag "+ tagId + ".");
        }
        MetadataEntity[] tmpMetadata = new MetadataEntity[metadata.length + keyValueMap.size()];
        System.arraycopy(metadata, 0, tmpMetadata, 0, metadata.length);
        int i = metadata.length;
        for (Map.Entry<String, String > entry : keyValueMap.entrySet()) {
            tmpMetadata[i].entityReferenceType = ProfilesEntityReferenceType.Object;
            tmpMetadata[i].referenceUrn = TAG_PREFIX + tagId;
            tmpMetadata[i].dataType = DATATYPE_STRING;
            tmpMetadata[i].key = entry.getKey();
            tmpMetadata[i].value = entry.getValue();
        }
        metadata = tmpMetadata.clone();
    }

    public AccountEntity getAccount() {
        return account;
    }

    public ObjectEntity[] getObjects() {
        return objects.clone();
    }

    public AddressEntity[] getObjectAddresses() {
        return objectAddresses.clone();
    }

    public RelationshipEntity[] getRelationships() {
        return relationships.clone();
    }

    public MetadataEntity[] getMetadata() {
        return metadata.clone();
    }

    public static class AccountEntity {
        public String name;
        public String moniker;
    }

    public static class ObjectEntity {
        public String objectUrn;
        public String type;
        public String name;
    }

    public static class AddressEntity {
    }

    public static class RelationshipEntity {
        public ProfilesEntityReferenceType entityReferenceType;
        public String referenceUrn;
        public String type;
        public ProfilesEntityReferenceType relatedEntityReferenceType;
        public String relatedReferenceUrn;
    }

    public static class MetadataEntity {
        public String value;
        public ProfilesEntityReferenceType entityReferenceType;
        public String referenceUrn;
        public String dataType;
        public String key;
    }
}
