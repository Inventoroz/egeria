/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.analyticsmodeling.converter;

import org.odpi.openmetadata.accessservices.analyticsmodeling.metadata.GlossaryTerm;
import org.odpi.openmetadata.commonservices.generichandlers.OpenMetadataAPIGenericConverter;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefCategory;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

/**
 * GlossaryTermConverter transfers the relevant properties from an Open Metadata Repository Services (OMRS)
 * EntityDetail object into a GlossaryTerm bean.
 */
public class GlossaryTermConverter extends OpenMetadataAPIGenericConverter<GlossaryTerm>
{
    /**
     * Constructor
     *
     * @param repositoryHelper helper object to parse entity/relationship objects
     * @param serviceName name of this component
     * @param serverName local server name
     */
    public GlossaryTermConverter(OMRSRepositoryHelper repositoryHelper,
                             String               serviceName,
                             String               serverName)
    {
        super(repositoryHelper, serviceName, serverName);
    }


    /**
     * Using the supplied instances, return a new instance of the bean. This is used for beans that have
     * contain a combination of the properties from an entity.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public GlossaryTerm getNewBean(Class<GlossaryTerm>     beanClass,
                        EntityDetail entity,
                        String       methodName) throws PropertyServerException
    {
        if (entity == null) {
            handleMissingMetadataInstance(beanClass.getName(), TypeDefCategory.ENTITY_DEF, methodName);
        }
        
        GlossaryTerm bean = new GlossaryTerm();
		InstanceProperties instanceProperties = new InstanceProperties(entity.getProperties());
		
		bean.setGuid(entity.getGUID());
		bean.setName(removeDisplayName(instanceProperties));
		bean.setDescription(removeDescription(instanceProperties));
		bean.setSummary(removeSummary(instanceProperties));

		return bean;
    }


    /**
     * Using the supplied instances, return a new instance of the bean. This is used for beans that have
     * contain a combination of the properties from an entity and a that os a connected relationship.
     *
     * @param beanClass name of the class to create
     * @param entity entity containing the properties
     * @param relationship relationship containing the properties
     * @param methodName calling method
     * @return bean populated with properties from the instances supplied
     * @throws PropertyServerException there is a problem instantiating the bean
     */
    @Override
    public GlossaryTerm getNewBean(Class<GlossaryTerm>     beanClass,
                        EntityDetail entity,
                        Relationship relationship,
                        String       methodName) throws PropertyServerException
    {
        return getNewBean(beanClass, entity, methodName);
    }
}

