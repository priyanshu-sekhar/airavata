package org.apache.airavata.gfac.gsissh.handler;

import java.util.List;
import java.util.Properties;

import org.apache.airavata.gfac.core.GFacException;
import org.apache.airavata.gfac.core.context.JobExecutionContext;
import org.apache.airavata.gfac.core.handler.AbstractHandler;
import org.apache.airavata.gfac.core.handler.GFacHandlerException;
import org.apache.airavata.gfac.core.provider.GFacProviderException;
import org.apache.airavata.gfac.core.GFacUtils;
import org.apache.airavata.gfac.gsissh.security.GSISecurityContext;
import org.apache.airavata.gfac.gsissh.util.GFACGSISSHUtils;
import org.apache.airavata.gfac.ssh.util.HandleOutputs;
import org.apache.airavata.gfac.core.cluster.RemoteCluster;
import org.apache.airavata.model.appcatalog.appinterface.OutputDataObjectType;
import org.apache.airavata.model.workspace.experiment.CorrectiveAction;
import org.apache.airavata.model.workspace.experiment.ErrorCategory;
import org.apache.airavata.registry.cpi.ExpCatChildDataType;
import org.apache.airavata.registry.cpi.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewGSISSHOutputHandler extends AbstractHandler{
	 private static final Logger log = LoggerFactory.getLogger(NewGSISSHOutputHandler.class);
	  public void invoke(JobExecutionContext jobExecutionContext) throws GFacHandlerException {
	        super.invoke(jobExecutionContext);
	        String hostAddress = jobExecutionContext.getHostName();
	        try {
	            if (jobExecutionContext.getSecurityContext(hostAddress) == null) {
	                GFACGSISSHUtils.addSecurityContext(jobExecutionContext);
	            }
	        }  catch (Exception e) {
	        	 try {
	  				GFacUtils.saveErrorDetails(jobExecutionContext,  e.getCause().toString(), CorrectiveAction.CONTACT_SUPPORT, ErrorCategory.AIRAVATA_INTERNAL_ERROR);
	  			} catch (GFacException e1) {
	  				 log.error(e1.getLocalizedMessage());
	  			}  
	            log.error(e.getMessage());
	            throw new GFacHandlerException("Error while creating SSHSecurityContext", e, e.getLocalizedMessage());
	        }
	        RemoteCluster remoteCluster = null;
	        
	        try {
	            remoteCluster = ((GSISecurityContext) jobExecutionContext.getSecurityContext(hostAddress)).getRemoteCluster();
	            if (remoteCluster == null) {
	                GFacUtils.saveErrorDetails(jobExecutionContext, "Security context is not set properly", CorrectiveAction.CONTACT_SUPPORT, ErrorCategory.FILE_SYSTEM_FAILURE);
	                
	                throw new GFacProviderException("Security context is not set properly");
	            } else {
	                log.info("Successfully retrieved the Security Context");
	            }
	        } catch (Exception e) {
	            log.error(e.getMessage());
	            try {
	                GFacUtils.saveErrorDetails(jobExecutionContext,  e.getCause().toString(), CorrectiveAction.CONTACT_SUPPORT, ErrorCategory.AIRAVATA_INTERNAL_ERROR);
	            } catch (GFacException e1) {
	                log.error(e1.getLocalizedMessage());
	            }
	            throw new GFacHandlerException("Error while creating SSHSecurityContext", e, e.getLocalizedMessage());
	        }

	        super.invoke(jobExecutionContext);
	        List<OutputDataObjectType> outputArray =  HandleOutputs.handleOutputs(jobExecutionContext, remoteCluster);
            try {
				experimentCatalog.add(ExpCatChildDataType.EXPERIMENT_OUTPUT, outputArray, jobExecutionContext.getExperimentID());
			} catch (RegistryException e) {
				throw new GFacHandlerException(e);
			}
	    }

    @Override
    public void recover(JobExecutionContext jobExecutionContext) throws GFacHandlerException {
        // TODO: Auto generated method body.
    }

    @Override
	public void initProperties(Properties properties) throws GFacHandlerException {
		// TODO Auto-generated method stub
		
	}

}
