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
package org.apache.airavata.gfac.impl;

import com.google.common.eventbus.Subscribe;
import org.apache.airavata.common.utils.AiravataUtils;
import org.apache.airavata.common.utils.MonitorPublisher;
import org.apache.airavata.common.utils.listener.AbstractActivityListener;
import org.apache.airavata.messaging.core.MessageContext;
import org.apache.airavata.messaging.core.Publisher;
import org.apache.airavata.model.messaging.event.MessageType;
import org.apache.airavata.model.messaging.event.TaskStatusChangeEvent;
import org.apache.airavata.model.messaging.event.WorkflowIdentifier;
import org.apache.airavata.model.messaging.event.WorkflowNodeStatusChangeEvent;
import org.apache.airavata.model.workspace.experiment.WorkflowNodeDetails;
import org.apache.airavata.model.workspace.experiment.WorkflowNodeState;
import org.apache.airavata.model.workspace.experiment.WorkflowNodeStatus;
import org.apache.airavata.registry.cpi.ExperimentCatalog;
import org.apache.airavata.registry.cpi.ExperimentCatalogModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class AiravataWorkflowNodeStatusUpdator implements AbstractActivityListener {
    private final static Logger logger = LoggerFactory.getLogger(AiravataWorkflowNodeStatusUpdator.class);

    private ExperimentCatalog airavataExperimentCatalog;
    private MonitorPublisher monitorPublisher;
    private Publisher publisher;




    public ExperimentCatalog getAiravataExperimentCatalog() {
        return airavataExperimentCatalog;
    }

    public void setAiravataExperimentCatalog(ExperimentCatalog airavataExperimentCatalog) {
        this.airavataExperimentCatalog = airavataExperimentCatalog;
    }

    @Subscribe
    public void setupWorkflowNodeStatus(TaskStatusChangeEvent taskStatus) throws Exception{
    	WorkflowNodeState state=WorkflowNodeState.UNKNOWN;
    	switch(taskStatus.getState()){
    	case CANCELED:
    		state=WorkflowNodeState.CANCELED; break;
    	case COMPLETED:
    		state=WorkflowNodeState.COMPLETED; break;
    	case CONFIGURING_WORKSPACE:
    		state=WorkflowNodeState.INVOKED; break;
    	case FAILED:
    		state=WorkflowNodeState.FAILED; break;
    	case EXECUTING: case WAITING: case PRE_PROCESSING: case POST_PROCESSING: case OUTPUT_DATA_STAGING: case INPUT_DATA_STAGING:
    		state=WorkflowNodeState.EXECUTING; break;
    	case STARTED:
    		state=WorkflowNodeState.INVOKED; break;
    	case CANCELING:
    		state=WorkflowNodeState.CANCELING; break;
		default:
			return;
    	}
    	try {
            String expId = taskStatus.getTaskIdentity().getExperimentId();
			updateWorkflowNodeStatus(expId, taskStatus.getTaskIdentity().getWorkflowNodeId(), state);
            logger.debug("expId - {}: Publishing workflow node status for " + taskStatus.getTaskIdentity().getWorkflowNodeId()
                    + ":" + state.toString(), taskStatus.getTaskIdentity().getExperimentId());
            WorkflowIdentifier workflowIdentity = new WorkflowIdentifier(taskStatus.getTaskIdentity().getWorkflowNodeId(),
                                                                         taskStatus.getTaskIdentity().getExperimentId(),
                                                                         taskStatus.getTaskIdentity().getGatewayId());
            WorkflowNodeStatusChangeEvent event = new WorkflowNodeStatusChangeEvent(state, workflowIdentity);
            monitorPublisher.publish(event);
            String messageId = AiravataUtils.getId("WFNODE");
            MessageContext msgCntxt = new MessageContext(event, MessageType.WORKFLOWNODE, messageId, taskStatus.getTaskIdentity().getGatewayId());
            msgCntxt.setUpdatedTime(AiravataUtils.getCurrentTimestamp());

            publisher.publish(msgCntxt);
		} catch (Exception e) {
            logger.error("expId - " + taskStatus.getTaskIdentity().getExperimentId() + ": Error persisting data"
                    + e.getLocalizedMessage(), e);
            throw new Exception("Error persisting workflow node status..", e);
        }
    }

    public  void updateWorkflowNodeStatus(String experimentId, String workflowNodeId, WorkflowNodeState state) throws Exception {
		logger.info("expId - {}: Updating workflow node status for "+workflowNodeId+":"+state.toString(), experimentId);
    	WorkflowNodeDetails details = (WorkflowNodeDetails) airavataExperimentCatalog.get(ExperimentCatalogModelType.WORKFLOW_NODE_DETAIL, workflowNodeId);
        if(details == null) {
            details = new WorkflowNodeDetails();
            details.setNodeInstanceId(workflowNodeId);
        }
        WorkflowNodeStatus status = new WorkflowNodeStatus();
        status.setWorkflowNodeState(state);
        status.setTimeOfStateChange(Calendar.getInstance().getTimeInMillis());
        details.setWorkflowNodeStatus(status);
        airavataExperimentCatalog.update(ExperimentCatalogModelType.WORKFLOW_NODE_STATUS, status, workflowNodeId);
    }

	public void setup(Object... configurations) {
		for (Object configuration : configurations) {
			if (configuration instanceof ExperimentCatalog){
				this.airavataExperimentCatalog =(ExperimentCatalog)configuration;
			} else if (configuration instanceof MonitorPublisher){
				this.monitorPublisher=(MonitorPublisher) configuration;
			}  else if (configuration instanceof Publisher){
                this.publisher=(Publisher) configuration;
            }
        }
	}
}
