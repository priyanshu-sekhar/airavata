/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.apache.airavata.registry.core.experiment.catalog.resources;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.airavata.registry.core.experiment.catalog.ExpCatResourceUtils;
import org.apache.airavata.registry.core.experiment.catalog.ExperimentCatResource;
import org.apache.airavata.registry.core.experiment.catalog.ResourceType;
import org.apache.airavata.registry.core.experiment.catalog.model.Configuration;
import org.apache.airavata.registry.core.experiment.catalog.model.Configuration_PK;
import org.apache.airavata.registry.cpi.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationResource extends AbstractExpCatResource {
    private final static Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);
    private String configKey;
    private String configVal;
    private Timestamp expireDate;
    private String categoryID = ConfigurationConstants.CATEGORY_ID_DEFAULT_VALUE;

    public ConfigurationResource() {
    }

    /**
     * @param configKey configuration key
     * @param configVal configuration value
     */
    public ConfigurationResource(String configKey, String configVal) {
        this.configKey = configKey;
        this.configVal = configVal;
    }

    /**
     * Since Configuration does not depend on any other data structures at the
     * system, this method is not valid
     *
     * @param type child resource types
     * @return UnsupportedOperationException
     */
    public ExperimentCatResource create(ResourceType type) throws RegistryException {
        logger.error("Unsupported operation for configuration resource " +
                "since there are no child resources generated by configuration resource.. ",
                new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     * Since Configuration does not depend on any other data structures at the
     * system, this method is not valid
     *
     * @param type child resource types
     * @param name name of the child resource
     *             throws UnsupportedOperationException
     */
    public void remove(ResourceType type, Object name) throws RegistryException {
        logger.error("Unsupported operation for configuration resource " +
                "since there are no child resources generated by configuration resource.. ",
                new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }


    /**
     * Since Configuration does not depend on any other data structures at the
     * system, this method is not valid
     *
     * @param type child resource types
     * @param name name of the child resource
     * @return UnsupportedOperationException
     */
    public ExperimentCatResource get(ResourceType type, Object name) throws RegistryException {
        logger.error("Unsupported operation for configuration resource " +
                "since there are no child resources generated by configuration resource.. ",
                new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     * Since Configuration does not depend on any other data structures at the
     * system, this method is not valid
     *
     * @param type child resource types
     * @return UnsupportedOperationException
     */
    public List<ExperimentCatResource> get(ResourceType type) throws RegistryException {
        logger.error("Unsupported operation for configuration resource " +
                "since there are no child resources generated by configuration resource.. ",
                new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     * @param expireDate expire date of the configuration
     */
    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * save configuration to database
     */
    public synchronized void save() throws RegistryException {
        EntityManager em = null;
        try {
            em = ExpCatResourceUtils.getEntityManager();
            //whether existing
            Configuration existing = em.find(Configuration.class, new Configuration_PK(configKey, configVal, categoryID));
            em.close();
            em = ExpCatResourceUtils.getEntityManager();
            em.getTransaction().begin();
            Configuration configuration = new Configuration();
            configuration.setConfig_key(configKey);
            configuration.setConfig_val(configVal);
            configuration.setExpire_date(expireDate);
            configuration.setCategory_id(categoryID);
            if (existing != null) {
                existing.setExpire_date(expireDate);
                existing.setCategory_id(categoryID);
                configuration = em.merge(existing);
            } else {
                em.persist(configuration);
            }
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RegistryException(e);
        } finally {
            if (em != null && em.isOpen()) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    /**
     * Since Configuration does not depend on any other data structures at the
     * system, this method is not valid
     *
     * @param type child resource types
     * @param name of the child resource
     * @return UnsupportedOperationException
     */
    public boolean isExists(ResourceType type, Object name) {
        logger.error("Unsupported operation for configuration resource " +
                "since there are no child resources generated by configuration resource.. ",
                new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     * @return configuration value
     */
    public String getConfigVal() {
        return configVal;
    }

    /**
     * @param configKey configuration key
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @param configVal configuration value
     */
    public void setConfigVal(String configVal) {
        this.configVal = configVal;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
