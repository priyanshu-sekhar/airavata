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

import org.apache.openjpa.persistence.DataCache;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@DataCache
@Entity
@Table(name = "JOB_DETAIL")
@IdClass(JobDetails_PK.class)
public class JobDetail implements Serializable {
    @Id
    @Column(name = "JOB_ID")
    private String jobId;
    @Id
    @Column(name = "TASK_ID")
    private String taskId;
    @Column(name = "JOB_DESCRIPTION")
    @Lob
    private char[] jobDescription;
    @Column(name = "CREATION_TIME")
    private Timestamp creationTime;
    @Column(name = "COMPUTE_RESOURCE_CONSUMED")
    private String computeResourceConsumed;
    @Column(name = "JOBNAME")
    private String jobName;
    @Column(name = "WORKING_DIR")
    private String workingDir;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "TASK_ID")
    private TaskDetail task;

    @OneToOne (fetch = FetchType.LAZY, mappedBy = "jobDetail")
    private Status jobStatus;

    @OneToMany (fetch = FetchType.LAZY,  mappedBy = "jobDetail")
    private List<ErrorDetail> errorDetails;

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<ErrorDetail> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public char[] getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(char[] jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public String getComputeResourceConsumed() {
        return computeResourceConsumed;
    }

    public void setComputeResourceConsumed(String computeResourceConsumed) {
        this.computeResourceConsumed = computeResourceConsumed;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public Status getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Status jobStatus) {
        this.jobStatus = jobStatus;
    }
}
