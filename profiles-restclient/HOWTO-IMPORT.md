# How-to: Import tag data into Profiles

## Prerequisites

 - Profiles account with admin role

**Important notes:**

 - This API is primarily designed to be used on Profiles developer instances.
 - On productive instances, such as SMART COSMOS Profiles, data import is restricted
   and not available for normal users.

## Import procedure

The **ProfilesTransactionRequest** supports easy import of tag data sets, including
production batch relationships, additional tag data and custom defined data, such as
arbitrary Objects, Objects Addresses, Relationships and Metadata.

Importing tags is done in a two-step procedure:

1. Collect all tag data and construct a **ProfilesTransactionRequest** from that data
   (the import account needed to construct the request is the domain after the '@' of
   the user email address in most cases)
2. Add a production batch by calling **addBatch**
3. Add all tags by calling **addTag** (this will automatically create the batch
   relationship)
4. Add related tag metadata by calling **addTagData**
5. Create a **ProfilesRestClient** instance and call **importProfilesData** with the
   previously created **importProfilesData** object

**Notes on tag metadata:**

The tag metadata keys have the format **tag:{appId}:{valueType}**

The **appId** can be freely chosen.

The **valueType** shall have one of the following types:
 - **value** represents normal tag data
 - **locked** defined the access conditions of a value
   (**true** = read only, **false** = read and write)
 - **key** represents a tag key to access data
 - **auth** represents a secret to be used for authentication

## Code Samples

First check if the user has access privileges to import data into Profiles. This can
be done by submitting an empty import request:

    ProfilesRestClient client= new ProfilesRestClient(server, user, password);
    String account = client.getAccount();
    ProfilesTransactionRequest req = new ProfilesTransactionRequest(account);
    ProfilesRestResult result = client.importProfilesData(req);
    switch (result.httpStatus) {
        case 200:
            if (result.iCode == 1)
                sResult = "OK";
            else
                sResult = result.sMessage;
        break;
        case 401:
            sResult = "Login incorrect";
        break;
        case 403:
            sResult = "User has no tag import permission";
        break;
        case 404:
            sResult = "Tag import endpoint unavailable";
        break;
        default:
            sResult = "Unknown error";
    }

Now when import access is possible, it is time to import some data:

    String batchId = "MyProfilesBatch";
    String tagIds[] = {"04555555555555", "04AAAAAAAAAAAA"};
    String tagDataKey = "tag:ndef:value";
    String tagDataValue = "D101135502736D6172747261632D67726F75702E636F6D";
    ProfilesTransactionRequest transaction = new ProfilesTransactionRequest(account);
    transaction.addBatch(batchId);
    transaction.addTag(batchId, tagIds[0]);
    transaction.addTag(batchId, tagIds[1]);
    Map<String, String>tagdata = new HashMap<String, String>();
    tagdata.put(tagDataKey, tagDataValue);
    transaction.addTagData(tagIds[0], tagdata);
    transaction.addTagData(tagIds[1], tagdata);
    result = client.importProfilesData(transaction);
    if ((result.httpStatus != 200) || (result.iCode != 1)) {
        throw new Exception(result.sMessage);
    }
