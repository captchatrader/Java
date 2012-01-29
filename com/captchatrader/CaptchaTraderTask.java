package com.captchatrader;

import java.lang.Exception;
import java.lang.InterruptedException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class is not to be instantiated outside the package.
 * Call {@link com.captchatrader.CaptchaTrader#getTask() CaptchaTrader.getTask()} to get an instance.
 * <PRE>
 * Example:
 *
 *    CaptchaTraderTask&lt;String&gt; task = ct.getTask();
 * </PRE>
 *
 * @since v2012.1
 * @version 2012.1
 */
public class CaptchaTraderTask<V> extends FutureTask<V>
{
	/**
	 * Create a CaptchaTraderTask for submitting a captcha job in asynchronous mode
	 * @param callable The CaptchaTrader object
	 */
	CaptchaTraderTask(Callable<V> callable)
	{
			super(callable);
	}
	/**
	 * Waits if necessary for the task to complete, and then retrieves the response.
	 * @throws InterruptedException - if the current thread was interrupted while waiting 
	 * @throws ExecutionException - if the captcha job threw an exception 
	 * @return The response to the captcha submission
	 */
	public V getResponse()
		throws InterruptedException, ExecutionException
	{
		return get();
	}

	/**
	 * Waits if necessary for at most the given time for the task to complete, and then retrieves the response. 
	 * @param timeout The maximum number of milliseconds to wait
	 * @throws InterruptedException - if the current thread was interrupted while waiting 
	 * @throws ExecutionException - if the captcha job threw an exception 
	 * @throws TimeoutException - if the wait timed out
	 * @return The response to the captcha submission
	 */
	public V getResponse(long timeout)
		throws InterruptedException, ExecutionException, TimeoutException
	{
		return get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * CaptchaTraderTask may not be cancelled once submitted
	 * So this method does not do anything
	 * @return false
	 */ 
	@Override 
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return false;
	}
}
