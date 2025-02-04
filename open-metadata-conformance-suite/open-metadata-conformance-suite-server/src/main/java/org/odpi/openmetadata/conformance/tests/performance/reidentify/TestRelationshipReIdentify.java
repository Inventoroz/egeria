/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.conformance.tests.performance.reidentify;

import org.odpi.openmetadata.conformance.tests.performance.OpenMetadataPerformanceTestCase;
import org.odpi.openmetadata.conformance.workbenches.performance.PerformanceProfile;
import org.odpi.openmetadata.conformance.workbenches.performance.PerformanceWorkPad;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.OMRSMetadataCollection;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.MatchCriteria;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.FunctionNotSupportedException;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Test performance of relationship re-identify operations.
 */
public class TestRelationshipReIdentify extends OpenMetadataPerformanceTestCase
{

    private static final String TEST_CASE_ID   = "repository-relationship-re-identify-performance";
    private static final String TEST_CASE_NAME = "Repository relationship re-identify performance test case";

    private static final String A_FIND_RELATIONSHIPS        = TEST_CASE_ID + "-findRelationshipsByProperty";
    private static final String A_FIND_RELATIONSHIPS_MSG    = "Repository performs search for unordered first instancesPerType homed instances of type: ";

    private static final String A_RE_IDENTIFY     = TEST_CASE_ID + "-reIdentifyRelationship";
    private static final String A_RE_IDENTIFY_MSG = "Repository performs re-identification of homed instances of type: ";

    private final RelationshipDef     relationshipDef;
    private final String              testTypeName;


    /**
     * Typical constructor sets up superclass and discovered information needed for tests
     *
     * @param workPad place for parameters and results
     * @param relationshipDef type of valid relationships
     */
    public TestRelationshipReIdentify(PerformanceWorkPad workPad,
                                      RelationshipDef          relationshipDef)
    {
        super(workPad, PerformanceProfile.RELATIONSHIP_RE_IDENTIFY.getProfileId());

        this.relationshipDef = relationshipDef;

        this.testTypeName = this.updateTestIdByType(relationshipDef.getName(),
                TEST_CASE_ID,
                TEST_CASE_NAME);
    }


    /**
     * Method implemented by the actual test case.
     *
     * @throws Exception something went wrong with the test.
     */
    protected void run() throws Exception
    {
        OMRSMetadataCollection metadataCollection = super.getMetadataCollection();
        int numInstances = super.getInstancesPerType();

        Set<String> keys = getRelationshipKeys(metadataCollection, numInstances);
        reIdentifyRelationships(metadataCollection, keys);

        super.setSuccessMessage("Relationship re-identify performance tests complete for: " + testTypeName);
    }

    /**
     * Retrieve a set of relationship GUIDs for this type.
     *
     * @param metadataCollection through which to call findRelationshipsByProperty
     * @param numInstances of relationships to re-identify
     * @return a set of relationship GUIDs to re-identify
     * @throws Exception on any errors
     */
    private Set<String> getRelationshipKeys(OMRSMetadataCollection metadataCollection, int numInstances) throws Exception
    {
        final String methodName = "getRelationshipKeys";
        Set<String> keys = new HashSet<>();
        try {
            OMRSRepositoryHelper repositoryHelper = super.getRepositoryHelper();
            InstanceProperties byMetadataCollectionId = repositoryHelper.addStringPropertyToInstance(testCaseId,
                    null,
                    "metadataCollectionId",
                    repositoryHelper.getExactMatchRegex(performanceWorkPad.getTutMetadataCollectionId()),
                    methodName);
            long start = System.nanoTime();
            List<Relationship> relationships = metadataCollection.findRelationshipsByProperty(workPad.getLocalServerUserId(),
                    relationshipDef.getGUID(),
                    byMetadataCollectionId,
                    MatchCriteria.ALL,
                    0,
                    null,
                    null,
                    null,
                    null,
                    numInstances);
            long elapsedTime = (System.nanoTime() - start) / 1000000;
            assertCondition(relationships != null,
                    A_FIND_RELATIONSHIPS,
                    A_FIND_RELATIONSHIPS_MSG + testTypeName,
                    PerformanceProfile.RELATIONSHIP_SEARCH.getProfileId(),
                    null,
                    "findRelationshipsByProperty",
                    elapsedTime);
            if (relationships != null) {
                keys = relationships.stream().map(Relationship::getGUID).collect(Collectors.toSet());
            }
        } catch (FunctionNotSupportedException exception) {
            super.addNotSupportedAssertion(A_FIND_RELATIONSHIPS,
                    A_FIND_RELATIONSHIPS_MSG + testTypeName,
                    PerformanceProfile.RELATIONSHIP_SEARCH.getProfileId(),
                    null);
        }
        return keys;
    }

    /**
     * Attempt to re-identify the relationships provided to a use a new GUID.
     * @param metadataCollection through which to call reIdentifyRelationship
     * @param keys GUIDs of instances to re-identify
     * @throws Exception on any errors
     */
    private void reIdentifyRelationships(OMRSMetadataCollection metadataCollection,
                                        Set<String> keys) throws Exception
    {

        final String methodName = "reIdentifyRelationship";

        try {
            for (String guid : keys) {
                long start = System.nanoTime();
                Relationship result = metadataCollection.reIdentifyRelationship(workPad.getLocalServerUserId(),
                        relationshipDef.getGUID(),
                        relationshipDef.getName(),
                        guid,
                        UUID.randomUUID().toString());
                long elapsedTime = (System.nanoTime() - start) / 1000000;
                assertCondition(result != null,
                        A_RE_IDENTIFY,
                        A_RE_IDENTIFY_MSG + testTypeName,
                        PerformanceProfile.RELATIONSHIP_RE_IDENTIFY.getProfileId(),
                        null,
                        methodName,
                        elapsedTime);
            }
        } catch (Exception exc) {
            String operationDescription = "re-identify relationship of type " + relationshipDef.getName();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("typeDefGUID", relationshipDef.getGUID());
            parameters.put("typeDefName", relationshipDef.getName());
            String msg = this.buildExceptionMessage(testCaseId, methodName, operationDescription, parameters, exc);
            throw new Exception(msg, exc);
        }

    }

}
