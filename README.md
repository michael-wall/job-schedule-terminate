This is a POC to create a custom Job Scheduler that sets the status of other Job Scheduler Dispatch Logs to Failed if the In Progress job has been running for more than a set length of time e.g. 30 minutes.

Note:
1. This has been tested locally in an unclustered Liferay DXP 2024.q1.5 using JDK 8 for compile and runtime.
2. The POC iterates through the dispatch triggers and for each active dispatch trigger it checks for the latest dispatch log with status of In Progress. If found it determines how long it has been running based on start date, and if longer than allowedRuntimeSeconds then it updates the status to Failed as well as setting the end date, error and output fields on the dispatch log.
3. It isn't clear whether there can be multiple 'In Progress' dispatch logs for a single dispatch trigger in their scenario. If so then the POC would need to be updated to handle that scenario.

Steps:
1. Build and deploy the terminate-task / com.mw.dispatch.terminate-1.0.0.jar OSGi module.
2. Confirm the OSGi module deploys as expected.
3. Create a new Job Scheduler using 'terminate-executor-name' from the dropdown.
4. Populate the Name and add property 'allowedRuntimeSeconds=1800' (without quotes) on the Details Tab and Save. (The default is 30 minutes if the allowedRuntimeSeconds property is not set.)
5. Configure the Job Scheduler Trigger with the appropriate settings e.g. Active and a Cron Expression to run every 30 minutes etc.
6. Review the logs.
