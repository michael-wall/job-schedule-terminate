package com.mw.dispatch.test;

import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael Wall
 */
@Component(
	property = {
		"dispatch.task.executor.name=test-executor-name",
		"dispatch.task.executor.type=test"
	},
	service = DispatchTaskExecutor.class)
public class TestDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String KEY = "test-executor-name";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {
		
		UnicodeProperties dispatchTaskSettingsUnicodeProperties = dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();
		
		long runCount = GetterUtil.getLong(dispatchTaskSettingsUnicodeProperties.getProperty("runCount"), 270);
		long delay = GetterUtil.getLong(dispatchTaskSettingsUnicodeProperties.getProperty("delay"), 10000);
		
		String jobName = dispatchTrigger.getName() + " [" + dispatchTrigger.getDispatchTaskExecutorType() + "]";
		
		_log.info(jobName + ", runCount: " + runCount + ", delay: " + delay);

		for (int i = 0; i < runCount; i++) {
			
			Thread.sleep(delay);
	
			_log.info(jobName + ", " + i);
		}
		
	}

	@Override
	public String getName() {
		return KEY;
	}
	

	private static final Log _log = LogFactoryUtil.getLog(TestDispatchTaskExecutor.class);	
}