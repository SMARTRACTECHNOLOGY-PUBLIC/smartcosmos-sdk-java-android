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
        ObjectEntity[] batchObjects = new ObjectEntity[1];
        batchObjects[0] = new ObjectEntity();
        batchObjects[0].objectUrn = PREFIX_BATCH + batchId;
        batchObjects[0].type = TYPE_BATCH;
        batchObjects[0].name = batchId;
        addObjects(batchObjects);
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

        ObjectEntity[] tagObjects = new ObjectEntity[1];
        tagObjects[0] = new ObjectEntity();
        tagObjects[0].objectUrn = PREFIX_TAG + tagId;
        tagObjects[0].type = TYPE_TAG;
        tagObjects[0].name = tagId;
        addObjects(tagObjects);

        RelationshipEntity[] tagRelationships = new RelationshipEntity[1];
        tagRelationships[0] = new RelationshipEntity();
        tagRelationships[0].entityReferenceType = ProfilesEntityReferenceType.Object;
        tagRelationships[0].referenceUrn = TYPE_BATCH;
        tagRelationships[0].type = "contains";
        tagRelationships[0].relatedEntityReferenceType = ProfilesEntityReferenceType.Object;
        tagRelationships[0].relatedReferenceUrn = PREFIX_TAG + tagId;
        addRelationships(tagRelationships);
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
        MetadataEntity[] tagMetadata = new MetadataEntity[metadata.length + keyValueMap.size()];
        int i = 0;
        for (Map.Entry<String, String > entry : keyValueMap.entrySet()) {
            tagMetadata[i] = new MetadataEntity();
            tagMetadata[i].entityReferenceType = ProfilesEntityReferenceType.Object;
            tagMetadata[i].referenceUrn = PREFIX_TAG + tagId;
            tagMetadata[i].dataType = DATATYPE_STRING;
            tagMetadata[i].key = entry.getKey();
            tagMetadata[i].value = entry.getValue();
            i++;
        }
        addMetadata(tagMetadata);
    }

    public void addObjects(ObjectEntity[] newObjects) {
        ObjectEntity[] tmpObjects = new ObjectEntity[objects.length + newObjects.length];
        System.arraycopy(objects, 0, tmpObjects, 0, objects.length);
        System.arraycopy(newObjects, 0, tmpObjects, objects.length, newObjects.length);
        objects = tmpObjects.clone();
    }

    public void addObjectAddresses(AddressEntity[] newAddresses) {
        AddressEntity[] tmpAddresses = new AddressEntity[objectAddresses.length + newAddresses.length];
        System.arraycopy(objectAddresses, 0, tmpAddresses, 0, objectAddresses.length);
        System.arraycopy(newAddresses, 0, tmpAddresses, objectAddresses.length, newAddresses.length);
        objectAddresses = tmpAddresses.clone();
    }

    public void addRelationships(RelationshipEntity[] newRelationships) {
        RelationshipEntity[] tmpRelationships = new RelationshipEntity[relationships.length + newRelationships.length];
        System.arraycopy(relationships, 0, tmpRelationships, 0, relationships.length);
        System.arraycopy(newRelationships, 0, tmpRelationships, relationships.length, newRelationships.length);
        relationships = tmpRelationships.clone();
    }

    public void addMetadata(MetadataEntity[] newMetadata) {
        MetadataEntity[] tmpMetadata = new MetadataEntity[metadata.length + newMetadata.length];
        System.arraycopy(metadata, 0, tmpMetadata, 0, metadata.length);
        System.arraycopy(newMetadata, 0, tmpMetadata, metadata.length, newMetadata.length);
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
        String name;
        String moniker;
    }

    public static class ObjectEntity {
        String objectUrn;
        String type;
        String name;
    }

    public static class AddressEntity {
        String objectUrn;
        String type;
        String line1;
        String line2;
        String stateProvince;
        String countryAbbreviation;
        String city;
        String postalCode;
    }

    public static class RelationshipEntity {
        ProfilesEntityReferenceType entityReferenceType;
        String referenceUrn;
        String type;
        ProfilesEntityReferenceType relatedEntityReferenceType;
        String relatedReferenceUrn;
    }

    public static class MetadataEntity {
        String value;
        ProfilesEntityReferenceType entityReferenceType;
        String referenceUrn;
        String dataType;
        String key;
    }
}
