package net.smartcosmos.android;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMART COSMOS Profiles RestClient for Android
 * ===============================================================================
 * Copyright (C) 2016 Smartrac Technology Fletcher, Inc.
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

import java.util.HashMap;
import java.util.Map;

import net.smartcosmos.android.utility.AsciiHexConverter;

@SuppressWarnings("unchecked")
public class ProfilesBulkImportRequest {

    private static final String PREFIX_BATCH = "urn:uuid:smartrac-group:batch:";
    private static final String TYPE_BATCH = "Batch";
    private static final String PREFIX_TAG = "urn:uuid:smartrac-group:tag:";
    private static final String TYPE_TAG = "Tag";
    private static final String REL_CONTAINS = "contains";

    private Relationship[] relationships;

    private Map<String, Object>[] things;

    public ProfilesBulkImportRequest() {
        things = new HashMap[0];
        relationships = new Relationship[0];
    }

    public void addBatch(String batchId) {

        Map<String, Object>[] batchThing = new HashMap[1];
        batchThing[0].put("active", true);
        batchThing[0].put("name", batchId);
        batchThing[0].put("type", TYPE_BATCH);
        batchThing[0].put("urn", PREFIX_BATCH + batchId);
        addThings(batchThing);
    }

    public void addTag(String batchId, byte[] uid)
        throws IllegalArgumentException {
        addTag(batchId, AsciiHexConverter.bytesToHex(uid));
    }

    public void addTag(String batchId, String tagId) {
        boolean validBatchId = false;

        RelationshipReference source = new RelationshipReference();
        source.type = TYPE_BATCH;
        source.urn = PREFIX_BATCH + batchId;
        RelationshipReference target = new RelationshipReference();
        target.type = TYPE_TAG;
        target.urn = PREFIX_TAG + tagId;

        for (int i = 0; i < things.length; i++) {
            String thingType = things[i].get("type").toString();
            String thingUrn = things[i].get("urn").toString();

            if (source.type.equalsIgnoreCase(thingType) && source.urn.equalsIgnoreCase(thingUrn)) {
                validBatchId = true;
                break;
            }
        }
        if (!validBatchId) {
            throw new IllegalArgumentException("Cannot add tag to absent batch " + batchId + ".");
        }

        Map<String, Object>[] tagThing = new HashMap[1];
        tagThing[0].put("active", true);
        tagThing[0].put("name", tagId);
        tagThing[0].put("type", target.type);
        tagThing[0].put("urn", target.urn);
        addThings(tagThing);

        Relationship[] tagRelationship = new Relationship[1];
        tagRelationship[0].source = source;
        tagRelationship[0].target = target;
        tagRelationship[0].relationshipType = REL_CONTAINS;
        addRelationships(tagRelationship);
    }

    /**
     * Add data to an existing tag of the import request.
     *
     * @param uid Tag ID
     * @param keyValueMap Tag data map <key, value>
     * @throws IllegalArgumentException
     */
    public void addTagData(byte[] uid, Map<String, Object> keyValueMap)
        throws IllegalArgumentException {

        addTagData(AsciiHexConverter.bytesToHex(uid), keyValueMap);
    }

    /**
     * Add data to an existing tag of the import request.
     *
     * @param tagId Tag ID
     * @param keyValueMap Tag data map <key, value>
     * @throws IllegalArgumentException
     */
    public void addTagData(String tagId, Map<String, Object> keyValueMap)
        throws IllegalArgumentException {

        String tagUrn = PREFIX_TAG + tagId;

        for (int i = 0; i < things.length; i++) {
            String thingType = things[i].get("type").toString();
            String thingUrn = things[i].get("urn").toString();

            if (TYPE_TAG.equalsIgnoreCase(thingType) && tagUrn.equalsIgnoreCase(thingUrn)) {
                things[i].putAll(keyValueMap);
                return;
            }
        }
        throw new IllegalArgumentException("No such tagId");
    }

    /**
     * Add custom relationships to import request.
     *
     * @param newRelationships Relationships
     */
    public void addRelationships(Relationship[] newRelationships) {
        Relationship[] tmpRelationships = new Relationship[relationships.length + newRelationships.length];
        System.arraycopy(relationships, 0, tmpRelationships, 0, relationships.length);
        System.arraycopy(newRelationships, 0, tmpRelationships, relationships.length, newRelationships.length);
        relationships = tmpRelationships.clone();
    }

    /**
     * Add custom things to import request.
     *
     * @param newThings Things
     */
    public void addThings(Map<String, Object>[] newThings) {
        Map<String, Object>[] tmpThings = new HashMap[things.length + newThings.length];
        System.arraycopy(things, 0, tmpThings, 0, things.length);
        System.arraycopy(newThings, 0, tmpThings, things.length, newThings.length);
        things = tmpThings.clone();
    }

    /**
     * Get all relationships contained in the import request.
     *
     * @return relationships
     */
    public Relationship[] getRelationships() {
        return relationships.clone();
    }

    /**
     * Get all things contained in the import request.
     *
     * @return things
     */
    public Map<String, Object>[] getThings() {
        return things.clone();
    }

    public static class Relationship {
        RelationshipReference source;
        RelationshipReference target;
        String relationshipType;
    }

    public static class RelationshipReference {
        String type;
        String urn;
    }
}
