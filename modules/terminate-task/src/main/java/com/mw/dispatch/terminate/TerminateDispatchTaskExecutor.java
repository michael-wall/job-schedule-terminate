package com.mw.dispatch.terminate;

import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchLogLocalServiceUtil;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael Wall
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + TerminateDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + TerminateDispatchTaskExecutor.TYPE
	},
	service = DispatchTaskExecutor.class)
public class TerminateDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String KEY = "terminate-executor-name";
	public static final String TYPE = "terminate";

	@Override
	public void doExecute(
			DispatchTrigger currentDispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {
		
		_log.info("starting...");

		UnicodeProperties dispatchTaskSettingsUnicodeProperties = currentDispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		long allowedRuntimeSeconds = GetterUtil.getLong(dispatchTaskSettingsUnicodeProperties.getProperty("allowedRuntimeSeconds"), 1800);
		long allowedRuntimeMilliseconds = allowedRuntimeSeconds * 1000;
		
		_log.info("allowedRuntimeSeconds: " + allowedRuntimeSeconds);
		
		List<DispatchTrigger> dispatchTriggers = _dispatchTriggerLocalService.getDispatchTriggers(-1, -1); // Get all of them
		
		for (DispatchTrigger dispatchTrigger: dispatchTriggers) {
			_log.info("checking: " + dispatchTrigger.getDispatchTaskExecutorType());
			
			if (dispatchTrigger.getDispatchTaskExecutorType().equalsIgnoreCase(TYPE)) {
				_log.info("skipping " + dispatchTrigger.getDispatchTaskExecutorType());
				
				continue;
			}
			
			DispatchLog dispatchLog = _dispatchLogLocalService.fetchLatestDispatchLog(dispatchTrigger.getDispatchTriggerId(), DispatchTaskStatus.IN_PROGRESS);
			
			if (dispatchLog != null) {
				 // Only set as failed if it has been running for longer than allowedRuntimeSeconds
				if (dispatchLog.getStartDate().before(new Date(System.currentTimeMillis() - (allowedRuntimeMilliseconds)))) {
					dispatchLog.setStatus(DispatchTaskStatus.FAILED.getStatus());
				
					DispatchLogLocalServiceUtil.updateDispatchLog(dispatchLog);
					
					_log.info("dispatchLog: " + dispatchLog.getDispatchLogId() + ", updated status to Failed.");
				} else {
					_log.info("dispatchLog: " + dispatchLog.getDispatchLogId() + ", within the allowed runtime.");
				}
			}
		}
		
		_log.info("finishing...");
	}

	@Override
	public String getName() {
		return KEY;
	}
	
	
	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;	
	
	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;	

	private static final Log _log = LogFactoryUtil.getLog(TerminateDispatchTaskExecutor.class);	
}