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

import net.smartcosmos.android.utility.AsciiHexConverter;

import java.util.Map;

public class ProfilesTransactionRequest {

    static final String DATATYPE_STRING = "StringType";
    static final String PREFIX_BATCH = "urn:uuid:smartrac-group:batch:";
    static final String TYPE_BATCH = "Batch";
    static final String PREFIX_TAG = "urn:uuid:smartrac-group:tag:";
    static final String TYPE_TAG = "Tag";

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

    public void addBatch(String batchId) {
        ObjectEntity[] tmpObjects = new ObjectEntity[objects.length + 1];
        System.arraycopy(objects, 0, tmpObjects, 0, objects.length);
        tmpObjects[objects.length].objectUrn = PREFIX_BATCH + batchId;
        tmpObjects[objects.length].type = TYPE_BATCH;
        tmpObjects[objects.length].name = batchId;
        objects = tmpObjects.clone();
    }

    public void addTag(String batchId, byte[] uid)
            throws IllegalArgumentException {
        addTag(batchId, AsciiHexConverter.bytesToHex(uid));
    }

    public void addTag(String batchId, String tagId)
            throws IllegalArgumentException {
        boolean validBatchId = false;
        for (ObjectEntity o : objects) {
            if (o.objectUrn.equalsIgnoreCase(PREFIX_BATCH + batchId)) {
                validBatchId = true;
                break;
            }
        }
        if (!validBatchId) {
            throw new IllegalArgumentException("Cannot add tag to absent batch " + batchId + ".");
        }

        ObjectEntity[] tmpObjects = new ObjectEntity[objects.length + 1];
        System.arraycopy(objects, 0, tmpObjects, 0, objects.length);
        tmpObjects[objects.length].objectUrn = PREFIX_TAG + tagId;
        tmpObjects[objects.length].type = TYPE_TAG;
        tmpObjects[objects.length].name = tagId;
        objects = tmpObjects.clone();

        RelationshipEntity[] tmpRelationships = new RelationshipEntity[relationships.length + 1];
        System.arraycopy(relationships, 0, tmpRelationships, 0, relationships.length + 1);
        tmpRelationships[tmpRelationships.length].entityReferenceType = ProfilesEntityReferenceType.Object;
        tmpRelationships[tmpRelationships.length].referenceUrn = TYPE_BATCH;
        tmpRelationships[tmpRelationships.length].type = "contains";
        tmpRelationships[tmpRelationships.length].relatedEntityReferenceType = ProfilesEntityReferenceType.Object;
        tmpRelationships[tmpRelationships.length].relatedReferenceUrn = PREFIX_TAG + tagId;
        relationships = tmpRelationships.clone();
    }

    public void addTagData(byte[] uid, Map<String, String> keyValueMap)
            throws IllegalArgumentException {
        addTagData(AsciiHexConverter.bytesToHex(uid), keyValueMap);
    }

    public void addTagData(String tagId, Map<String, String> keyValueMap)
            throws IllegalArgumentException {
        boolean validTagId = false;
        for (ObjectEntity o : objects) {
            if (o.objectUrn.equalsIgnoreCase(PREFIX_TAG + tagId)) {
                validTagId = true;
                break;
            }
        }
        if (!validTagId) {
            throw new IllegalArgumentException("Cannot add data to absent tag " + tagId + ".");
        }
        MetadataEntity[] tmpMetadata = new MetadataEntity[metadata.length + keyValueMap.size()];
        System.arraycopy(metadata, 0, tmpMetadata, 0, metadata.length);
        int i = metadata.length;
        for (Map.Entry<String, String > entry : keyValueMap.entrySet()) {
            tmpMetadata[i].entityReferenceType = ProfilesEntityReferenceType.Object;
            tmpMetadata[i].referenceUrn = PREFIX_TAG + tagId;
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
        public String objectUrn;
        public String type;
        public String line1;
        public String line2;
        public String stateProvince;
        public String countryAbbreviation;
        public String city;
        public String postalCode;
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
