/**
 * 
 */
package com.vagm.vagmdroid;

import java.lang.reflect.Method;

import org.robolectric.Robolectric;
import org.robolectric.TestLifecycleApplication;

import roboguice.RoboGuice;

/**
 * The Class TestApplication.
 * @author roman_konovalov
 */
public class TestApplication extends Application implements TestLifecycleApplication {
	 @Override
	    public void onCreate() {
	        super.onCreate();

	       /* RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
	                RoboGuice.newDefaultRoboModule(this), new RobolectricSampleModule());*/
	    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeTest(final Method method) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareTest(final Object test) {
		 TestApplication application = (TestApplication) Robolectric.application;

	        RoboGuice.setBaseApplicationInjector(application, RoboGuice.DEFAULT_STAGE);

	        RoboGuice.getInjector(application).injectMembers(test);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterTest(final Method method) {
		// TODO Auto-generated method stub
	}

}
