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

package org.apache.airavata.registry.core.experiment.catalog.model;

import org.apache.airavata.registry.core.experiment.catalog.model.Status;
import org.apache.airavata.registry.core.experiment.catalog.model.TaskDetail;
import org.apache.openjpa.persistence.DataCache;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@DataCache
@Entity
@Table(name = "DATA_TRANSFER_DETAIL")
public class DataTransferDetail implements Serializable {
    @Id
    @Column(name = "TRANSFER_ID")
    private String transferId;
    @Column(name = "TASK_ID")
    private String taskId;
    @Column(name = "CREATION_TIME")
    private Timestamp creationTime;
    @Lob
    @Column(name = "TRANSFER_DESC")
    private char[] transferDesc;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "TASK_ID")
    private TaskDetail task;

    @OneToOne (fetch = FetchType.LAZY, mappedBy = "transferDetail")
    private Status dataTransferStatus;

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public char[] getTransferDesc() {
        return transferDesc;
    }

    public void setTransferDesc(char[] transferDesc) {
        this.transferDesc = transferDesc;
    }

    public Status getDataTransferStatus() {
        return dataTransferStatus;
    }

    public void setDataTransferStatus(Status dataTransferStatus) {
        this.dataTransferStatus = dataTransferStatus;
    }
}
