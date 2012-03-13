package be.cytomine.processing.job

import be.cytomine.processing.Job
import be.cytomine.security.UserJob
import be.cytomine.processing.SoftwareParameter
import grails.converters.JSON
import be.cytomine.processing.JobParameter

class PyxitSuggestedTermJobService extends AbstractJobService {

    static transactional = false

    def jobParameterService

    def init(Job job, UserJob userJob) {
        Collection<SoftwareParameter> softwareParameters = SoftwareParameter.findAllBySoftware(job.software)
        Map<String, String> parametersValues = new HashMap<String, String>()
        parametersValues.put("public_key", userJob.getPublicKey())
        parametersValues.put("private_key", userJob.getPrivateKey())
        parametersValues.put("clf_n_subwindows", 50.toString())
        parametersValues.put("clf_min_size", 0.5.toString())
        parametersValues.put("clf_max_size", 1.toString())
        parametersValues.put("clf_target_width", 16.toString())
        parametersValues.put("clf_target_height", 16.toString())
        parametersValues.put("clf_n_estimators",10.toString())
        parametersValues.put("id_project",job.getProject().getId().toString())
        softwareParameters.each { softwareParameter ->
            def value = parametersValues.get(softwareParameter.getName()) != null ? parametersValues.get(softwareParameter.getName()) : softwareParameter.getDefaultValue()
            JobParameter jobParameter = new JobParameter(value: value, job: job, softwareParameter: softwareParameter)
            jobParameterService.add(JSON.parse(jobParameter.encodeAsJSON()))
        }
    }

    def execute(Job job) {
        //get job params
        String[] jobParams = getParameters(job)
        String[] args = new String[jobParams.length+2]
        //build software params
        args[0] = "python"
        args[1] =  "algo/pyxit/pyxit.py"
        for(int i=0;i<jobParams.length;i++) {
            args[i+2] = jobParams[i]
        }
        printStartJobInfo(job,args)
        launchAndWaitSoftware(args)
        printStopJobInfo(job,args)
    }
}