This is a POC to create a custom Job Scheduler that sets the status of Job Scheduler Dispatch Logs to Failed if the job has been running for more than a set length of time e.g. 30 minutes.

This has been tested locally in an unclustered Liferay DXP 2024.q1.5 using JDK 8 for compile and runtime.

Steps:
1. Build and deploy the 'terminate-task / com.mw.dispatch.terminate-1.0.0.jar module.
2. Create a new Job Scheduler using 'terminate-executor-name' from the dropdown.
3. Populate the Name and add property 'allowedRuntimeSeconds=1800' (without quotes) on the Details Tab and Save.
4. Configure the Job Scheduler Trigger with the appropriate settings e.g. Active and running every 30 minutes etc.
5. Review the logs.
