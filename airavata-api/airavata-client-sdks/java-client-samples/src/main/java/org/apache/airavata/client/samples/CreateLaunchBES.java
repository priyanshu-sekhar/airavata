package org.apache.airavata.client.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.airavata.api.Airavata;
import org.apache.airavata.api.client.AiravataClientFactory;
import org.apache.airavata.client.tools.RegisterSampleApplications;
import org.apache.airavata.client.tools.RegisterSampleApplicationsUtils;
import org.apache.airavata.model.appcatalog.appinterface.InputDataObjectType;
import org.apache.airavata.model.appcatalog.appinterface.OutputDataObjectType;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.appcatalog.computeresource.JobSubmissionInterface;
import org.apache.airavata.model.appcatalog.computeresource.JobSubmissionProtocol;
import org.apache.airavata.model.appcatalog.computeresource.SecurityProtocol;
import org.apache.airavata.model.appcatalog.computeresource.UnicoreJobSubmission;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.error.AiravataErrorType;
import org.apache.airavata.model.error.AiravataSystemException;
import org.apache.airavata.model.error.ExperimentNotFoundException;
import org.apache.airavata.model.error.InvalidRequestException;
import org.apache.airavata.model.util.ExperimentModelUtil;
import org.apache.airavata.model.workspace.Gateway;
import org.apache.airavata.model.workspace.Project;
import org.apache.airavata.model.workspace.experiment.AdvancedOutputDataHandling;
import org.apache.airavata.model.workspace.experiment.ComputationalResourceScheduling;
import org.apache.airavata.model.workspace.experiment.ErrorDetails;
import org.apache.airavata.model.workspace.experiment.Experiment;
import org.apache.airavata.model.workspace.experiment.ExperimentSummary;
import org.apache.airavata.model.workspace.experiment.UserConfigurationData;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateLaunchBES {
 
    public static final String THRIFT_SERVER_HOST = "localhost";
    public static final int THRIFT_SERVER_PORT = 8930;

    private final static Logger logger = LoggerFactory.getLogger(CreateLaunchExperiment.class);
    private static final String DEFAULT_USER = "default.registry.user";
    private static final String DEFAULT_GATEWAY = "php_reference_gateway";
    private static Airavata.Client airavataClient;

    private static String echoAppId = "Echo_5dd52cd4-f9a0-459f-9baf-f8e715e44548";
    private static String mpiAppId = "HelloMPI_f0bb3b56-914e-4752-bb7b-dd18ef9dce00";

    private static String unicoreHostName = "fsd-cloud15.zam.kfa-juelich.de";

    private static String gatewayId;

    // unicore service endpoint url
    private static final String unicoreEndPointURL = "https://deisa-unic.fz-juelich.de:9111/FZJ_JUROPA/services/BESFactory?res=default_bes_factory";
//    private static final String unicoreEndPointURL = "https://fsd-cloud15.zam.kfa-juelich.de:7000/INTEROP1/services/BESFactory?res=default_bes_factory";

    public static void main(String[] args) throws Exception {
        airavataClient = AiravataClientFactory.createAiravataClient(THRIFT_SERVER_HOST, THRIFT_SERVER_PORT);
        System.out.println("API version is " + airavataClient.getAPIVersion(null));
//        createGateway();
//        getGateway("testGatewayId");
//        registerApplications(); // run this only the first time
        createAndLaunchExp();
        }

    private static String fsdResourceId;


    public static void getAvailableAppInterfaceComputeResources(String appInterfaceId) {
        try {
            Map<String, String> availableAppInterfaceComputeResources = airavataClient.getAvailableAppInterfaceComputeResources(appInterfaceId);
            for (String key : availableAppInterfaceComputeResources.keySet()) {
                System.out.println("id : " + key);
                System.out.println("name : " + availableAppInterfaceComputeResources.get(key));
            }
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

    }


    public static void createGateway() {
        try {
            Gateway gateway = new Gateway();
            gateway.setGatewayId("testGatewayId2");
            gateway.setGatewayName("testGateway2");
            gatewayId = airavataClient.addGateway(gateway);
            System.out.println(gatewayId);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

    }

    public static void getGateway(String gatewayId) {
        try {
            Gateway gateway = airavataClient.getGateway(gatewayId);
            gateway.setDomain("testDomain");
            airavataClient.updateGateway(gatewayId, gateway);
            List<Gateway> allGateways = airavataClient.getAllGateways();
            System.out.println(allGateways.size());
            if (airavataClient.isGatewayExist(gatewayId)) {
                Gateway gateway1 = airavataClient.getGateway(gatewayId);
                System.out.println(gateway1.getGatewayName());
            }
            boolean b = airavataClient.deleteGateway("testGatewayId2");
            System.out.println(b);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

    }


    public static void createAndLaunchExp() throws TException {
        List<String> experimentIds = new ArrayList<String>();
        try {
            for (int i = 0; i < 1; i++) {
//                final String expId = createEchoExperimentForFSD(airavataClient);
                final String expId = createMPIExperimentForFSD(airavataClient);
                experimentIds.add(expId);
                System.out.println("Experiment ID : " + expId);
//                updateExperiment(airavata, expId);
                launchExperiment(airavataClient, expId);
            }

            Thread.sleep(10000);
            for (String exId : experimentIds) {
                Experiment experiment = airavataClient.getExperiment(exId);
                System.out.println(experiment.getExperimentID() + " " + experiment.getExperimentStatus().getExperimentState().name());
            }


        } catch (Exception e) {
            logger.error("Error while connecting with server", e.getMessage());
            e.printStackTrace();

        }
    }

    public static void launchExperiment(Airavata.Client client, String expId)
            throws TException {
        try {
            String tokenId = "-0bbb-403b-a88a-42b6dbe198e9";
            client.launchExperiment(expId, tokenId);
        } catch (ExperimentNotFoundException e) {
            logger.error("Error occured while launching the experiment...", e.getMessage());
            throw new ExperimentNotFoundException(e);
        } catch (AiravataSystemException e) {
            logger.error("Error occured while launching the experiment...", e.getMessage());
            throw new AiravataSystemException(e);
        } catch (InvalidRequestException e) {
            logger.error("Error occured while launching the experiment...", e.getMessage());
            throw new InvalidRequestException(e);
        } catch (AiravataClientException e) {
            logger.error("Error occured while launching the experiment...", e.getMessage());
            throw new AiravataClientException(e);
        } catch (TException e) {
            logger.error("Error occured while launching the experiment...", e.getMessage());
            throw new TException(e);
        }
    }

    public static void registerApplications() {
        RegisterSampleApplications registerSampleApplications = new RegisterSampleApplications(airavataClient);

        //Register all compute hosts
        registerSampleApplications.registerXSEDEHosts();


        //Register Gateway Resource Preferences
        registerSampleApplications.registerGatewayResourceProfile();

        //Register all application modules
        registerSampleApplications.registerAppModules();

        //Register all application deployments
        registerSampleApplications.registerAppDeployments();

        //Register all application interfaces
        registerSampleApplications.registerAppInterfaces();
    }

    public static String registerUnicoreEndpoint(String hostName, String hostDesc, JobSubmissionProtocol protocol, SecurityProtocol securityProtocol) throws TException {

        ComputeResourceDescription computeResourceDescription = RegisterSampleApplicationsUtils
                .createComputeResourceDescription(hostName, hostDesc, null, null);

        fsdResourceId = airavataClient.registerComputeResource(computeResourceDescription);

        if (fsdResourceId.isEmpty())
            throw new AiravataClientException();

        System.out.println("FSD Compute ResourceID: " + fsdResourceId);

        JobSubmissionInterface jobSubmission = RegisterSampleApplicationsUtils.createJobSubmissionInterface(fsdResourceId, protocol, 2);
        UnicoreJobSubmission ucrJobSubmission = new UnicoreJobSubmission();
        ucrJobSubmission.setSecurityProtocol(securityProtocol);
        ucrJobSubmission.setUnicoreEndPointURL(unicoreEndPointURL);

        return jobSubmission.getJobSubmissionInterfaceId();
    }

    public static String createEchoExperimentForFSD(Airavata.Client client) throws TException {
        try {
            List<InputDataObjectType> exInputs = client.getApplicationInputs(echoAppId);
            for (InputDataObjectType inputDataObjectType : exInputs) {
                if (inputDataObjectType.getName().equalsIgnoreCase("Input_to_Echo")) {
                    inputDataObjectType.setValue("Hello World");
                } else if (inputDataObjectType.getName().equalsIgnoreCase("Input_to_Echo2")) {
                    inputDataObjectType.setValue("http://www.textfiles.com/100/ad.txt");
                } else if (inputDataObjectType.getName().equalsIgnoreCase("Input_to_Echo3")) {
                    inputDataObjectType.setValue("file:///tmp/test.txt");
                }
            }
            List<OutputDataObjectType> exOut = client.getApplicationOutputs(echoAppId);

            Experiment simpleExperiment =
                    ExperimentModelUtil.createSimpleExperiment("default", "admin", "echoExperiment", "SimpleEcho2", echoAppId, exInputs);
            simpleExperiment.setExperimentOutputs(exOut);


            Map<String, String> computeResources = airavataClient.getAvailableAppInterfaceComputeResources(echoAppId);
            if (computeResources != null && computeResources.size() != 0) {
                for (String id : computeResources.keySet()) {
                    String resourceName = computeResources.get(id);
                    if (resourceName.equals(unicoreHostName)) {
                        ComputationalResourceScheduling scheduling = ExperimentModelUtil.createComputationResourceScheduling(id, 1, 1, 1, "normal", 30, 0, 1048576, "sds128");
                        UserConfigurationData userConfigurationData = new UserConfigurationData();
                        userConfigurationData.setAiravataAutoSchedule(false);
                        userConfigurationData.setOverrideManualScheduledParams(false);
                        userConfigurationData.setComputationalResourceScheduling(scheduling);
                        
                        userConfigurationData.setGenerateCert(false);
                        userConfigurationData.setUserDN("");

                        // set output directory 
                        AdvancedOutputDataHandling dataHandling = new AdvancedOutputDataHandling();
                        dataHandling.setOutputDataDir("/tmp/airavata/output/" + UUID.randomUUID().toString() + "/");
                        userConfigurationData.setAdvanceOutputDataHandling(dataHandling);
                        simpleExperiment.setUserConfigurationData(userConfigurationData);

                        return client.createExperiment(DEFAULT_GATEWAY, simpleExperiment);
                    }
                }
            }
        } catch (AiravataSystemException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new AiravataSystemException(e);
        } catch (InvalidRequestException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new InvalidRequestException(e);
        } catch (AiravataClientException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new AiravataClientException(e);
        } catch (TException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new TException(e);
        }
        return null;
    }


    public static String createMPIExperimentForFSD(Airavata.Client client) throws TException {
        try {
            List<InputDataObjectType> exInputs = client.getApplicationInputs(mpiAppId);
            for (InputDataObjectType inputDataObjectType : exInputs) {
//                if (inputDataObjectType.getName().equalsIgnoreCase("Sample_Input")) {
//                    inputDataObjectType.setValue("");
//                }
                if (inputDataObjectType.getName().equalsIgnoreCase("NumberOfProcesses")) {
                    inputDataObjectType.setValue("32");
                }
                if (inputDataObjectType.getName().equalsIgnoreCase("US3INPUT")) {
                    inputDataObjectType.setValue("file://home/m.memon/us3input/smallerdata/hpcinput-uslims3.uthscsa.edu-uslims3_cauma3-01594.tar");
                }
                if (inputDataObjectType.getName().equalsIgnoreCase("US3INPUTARG")) {
                    inputDataObjectType.setValue("hpcinput-uslims3.uthscsa.edu-uslims3_cauma3-01594.tar");
                }
            }
            
            List<OutputDataObjectType> exOut = client.getApplicationOutputs(mpiAppId);
            
            for (OutputDataObjectType outputDataObjectType : exOut) {
            	if(outputDataObjectType.getName().equals("US3OUT")){
            		outputDataObjectType.setValue("output/analysis-results.tar");
            	}
			}
            
            Experiment simpleExperiment =
                    ExperimentModelUtil.createSimpleExperiment("default", "admin", "mpiExperiment", "HelloMPI", mpiAppId, exInputs);
            simpleExperiment.setExperimentOutputs(exOut);


            Map<String, String> computeResources = airavataClient.getAvailableAppInterfaceComputeResources(mpiAppId);
            if (computeResources != null && computeResources.size() != 0) {
                for (String id : computeResources.keySet()) {
                    String resourceName = computeResources.get(id);
                    if (resourceName.equals(unicoreHostName)) {
                        ComputationalResourceScheduling scheduling = ExperimentModelUtil.createComputationResourceScheduling(id, 0, 4, 0, null, 10, 0, 0, null);
                        UserConfigurationData userConfigurationData = new UserConfigurationData();
                        userConfigurationData.setAiravataAutoSchedule(false);
                        userConfigurationData.setOverrideManualScheduledParams(false);
                        userConfigurationData.setComputationalResourceScheduling(scheduling);
                        
                        userConfigurationData.setGenerateCert(true);
                        userConfigurationData.setUserDN("CN=m.memon, O=Ultrascan Gateway, C=DE");

                        // set output directory 
                        AdvancedOutputDataHandling dataHandling = new AdvancedOutputDataHandling();
                        dataHandling.setOutputDataDir("/tmp/airavata/output/" + UUID.randomUUID().toString() + "/");
                        userConfigurationData.setAdvanceOutputDataHandling(dataHandling);
                        simpleExperiment.setUserConfigurationData(userConfigurationData);

                        return client.createExperiment(DEFAULT_GATEWAY, simpleExperiment);
                    }
                }
            }
        } catch (AiravataSystemException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new AiravataSystemException(e);
        } catch (InvalidRequestException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new InvalidRequestException(e);
        } catch (AiravataClientException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new AiravataClientException(e);
        } catch (TException e) {
            logger.error("Error occured while creating the experiment...", e.getMessage());
            throw new TException(e);
        }
        return null;
    }
    
    
    public static List<Experiment> getExperimentsForUser(Airavata.Client client, String user) {
        try {
            return client.getAllUserExperiments(DEFAULT_GATEWAY, user);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Project> getAllUserProject(Airavata.Client client, String user) {
        try {
            return client.getAllUserProjects(DEFAULT_GATEWAY, user);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Project> searchProjectsByProjectName(Airavata.Client client, String user, String projectName) {
        try {
            return client.searchProjectsByProjectName(DEFAULT_GATEWAY, user, projectName);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Project> searchProjectsByProjectDesc(Airavata.Client client, String user, String desc) {
        try {
            return client.searchProjectsByProjectDesc(DEFAULT_GATEWAY, user, desc);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<ExperimentSummary> searchExperimentsByName(Airavata.Client client, String user, String expName) {
        try {
            return client.searchExperimentsByName(DEFAULT_GATEWAY, user, expName);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ExperimentSummary> searchExperimentsByDesc(Airavata.Client client, String user, String desc) {
        try {
            return client.searchExperimentsByDesc(DEFAULT_GATEWAY, user, desc);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ExperimentSummary> searchExperimentsByApplication(Airavata.Client client, String user, String app) {
        try {
            return client.searchExperimentsByApplication(DEFAULT_GATEWAY, user, app);
        } catch (AiravataSystemException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (AiravataClientException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getExperiment(Airavata.Client client, String expId) throws Exception {
        try {
            Experiment experiment = client.getExperiment(expId);
            List<ErrorDetails> errors = experiment.getErrors();
            if (errors != null && !errors.isEmpty()) {
                for (ErrorDetails error : errors) {
                    System.out.println("ERROR MESSAGE : " + error.getActualErrorMessage());
                }
            }

        } catch (ExperimentNotFoundException e) {
            logger.error("Experiment does not exist", e);
            throw new ExperimentNotFoundException("Experiment does not exist");
        } catch (AiravataSystemException e) {
            logger.error("Error while retrieving experiment", e);
            throw new AiravataSystemException(AiravataErrorType.INTERNAL_ERROR);
        } catch (InvalidRequestException e) {
            logger.error("Error while retrieving experiment", e);
            throw new InvalidRequestException("Error while retrieving experiment");
        } catch (AiravataClientException e) {
            logger.error("Error while retrieving experiment", e);
            throw new AiravataClientException(AiravataErrorType.INTERNAL_ERROR);
        }
    }

}
