/**
 * Copyright (C) 2011 by CaptchaTrader http://captchatrader.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * @author CaptchaTrader
 */
package com.captchatrader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * <p>CaptchaTrader is the main class for interacting with APIs provided by http://api.captchatrader.com.</p>
 * 
 * <PRE>
 * Examples:
 *
 *    CaptchaTrader ct = new CaptchaTrader("APIKEY", "USERNAME", "PASSWORD/PASSKEY");
 *
 *    // Submit a job in synchronous mode
 *    File captcha = new File("CAPTCHA.IMAGE");
 *    String answer = ct.submit(captcha);
 *
 *    // Submit a job in asynchronous mode with {@link com.captchatrader.CaptchaTraderTask CaptchaTraderTask}
 *    CaptchaTraderTask&lt;String&gt; task = ct.getTask();
 *    ct.submit(captcha, task);
 *    while (true) {
 *        try {
 *            System.out.println("task.getResponse(): " + task.getResponse(500));
 *            break;
 *        } catch (TimeoutException toex) {
 *            System.out.println("waiting for answer to captcha job...");
 *        }
 *        // do something else
 *        // Thread.sleep(1000);
 *    }
 *
 *    
 *    // Submit a job in asynchronous mode with {@link com.captchatrader.ResponseEvent ResponseEvent}
 *    MyResponseEvent mrevent = new MyResponseEvent();
 *    ct.submit(captcha, mrevent);
 *    while (mrevent.gotResponse()==false) {
 *        System.out.println("No response yet");
 *        // do something else
 *        // Thread.sleep(1000);
 *    }
 * </PRE>
 * @version 2012.1
 */
public class CaptchaTrader implements Callable<String>
{
	/**
	 * The API key of the host application.
	 */
	private String _apiKey;
	/**
	 * The username to run under.
	 */
	private String _username;
	/**
	 * The password of the user to run under.
	 */
	private String _password;
	/**
	 * The ticket id of the last submitted CAPTCHA.
	 */
	private String _activeJobId = null;
	/**
	 * The enqueue job id.
	 */
	private String _enqueueJobId = null;
	/**
	 * The file for asynchronous submission
	 */
	private File _file = null;
	/**
	 * The URL for asynchronous submission
	 */
	private URL _url = null;
	/**
	 * The ResponseEvent object for callback upon server response
	 */
	private ResponseEvent _event = null;
	/**
	 * Create a new CaptchaTrader instance.
	 * @param apiKey The API key of the host application.
	 * @param username The username to run under.
	 * @param password The password of the user to run under.
	 */
	public CaptchaTrader(String apiKey, String username, String password) {
		_apiKey = apiKey;
		_username = username;
		_password = password;
	}
	/**
	 * Create a new CaptchaTrader instance.
	 * @param username The username to run under.
	 * @param password The password of the user to run under.
	 */
	public CaptchaTrader(String username, String password) {
		_username = username;
		_password = password;
	}
	/**
	 * Create a new CaptchaTrader instance.
	 * @param apiKey The API key of the host application.
	 */
	public CaptchaTrader(String apiKey) {
		_apiKey = apiKey;
	}
	/**
	 * Set a new username to run under.
	 * @param username The username to run under.
	 * @return This CaptchaTrader instance.
	 */
	public CaptchaTrader setUsername(String username) {
		_username = username;
		return this;
	}
	/**
	 * Set a new password. This does not change the user's password.
	 * @param password The password of the user.
	 * @return This CaptchaTrader instance.
	 */
	public CaptchaTrader setPassword(String password) {
		_password = password;
		return this;
	}
	/**
	 * Set the API key of the host application.
	 * @param apiKey The API key of the host application.
	 * @return This CaptchaTrader instance.
	 */
	public CaptchaTrader setApiKey(String apiKey) {
		_apiKey = apiKey;
		return this;
	}
	/**
	 * Submit a CAPTCHA currently saved as a file.
	 * @param file The file that the CAPTCHA is saved as.
	 * @return The decoded CAPTCHA.
	 * @throws Exception Any exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 */
	public String submit(File file) throws Exception {
		CaptchaTraderRequest request = new CaptchaTraderRequest();
		request.setParameter("value", file);
		return submitJob(request);
	}
	/**
	 * Submit a CAPTCHA already hosted on an existing website.
	 * @param url The URL of the CAPTCHA image.
	 * @return The decoded CAPTCHA.
	 * @throws Exception Any exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 */
	public String submit(URL url) throws Exception {
		CaptchaTraderRequest request = new CaptchaTraderRequest();
		request.setParameter("value", url.toString());
		return submitJob(request);
	}
	/**
	 * Complete a job submission.
	 * @param request The CaptchaTraderRequest of the job.
	 * @return The decoded CAPTCHA.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any exception sent by the server.
	 */
	private String submitJob(CaptchaTraderRequest request)
		throws Exception
	{
		request.setParameter("username", _username);
		request.setParameter("password", _password);
		request.setParameter("api_key", _apiKey);
		
		InputStream in = request.doPost();
		String[] response = parseResponses(in, CaptchaTraderRequestType.SUBMIT.respSize());
		if(Integer.parseInt(response[0]) == -1) {
			throw new CaptchaTraderException(response[1]);
		} else {
			_activeJobId = response[0];
			return response[1];
		}
	}
	/**
	 * Respond to the last sent job.
	 * @param isCorrect Whether the job was correct or not.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/respond target=_blank>http://captchatrader.com/documentation/respond</a>
	 * </br>
	 */
	public void respond(boolean isCorrect) throws Exception {
		int jobId = 0;
		try {
			if (_activeJobId!=null) {
				jobId = Integer.parseInt(_activeJobId);
			}
		} catch (Exception ex) {}
		if(jobId <= 0) {
			throw new CaptchaTraderException("No job to respond to");
		} else {
			CaptchaTraderRequest request = new CaptchaTraderRequest(CaptchaTraderRequestType.RESPOND);
			request.setParameter("username", _username);
			request.setParameter("password", _password);
			request.setParameter("ticket", _activeJobId);
			request.setParameter("is_correct", isCorrect);

			InputStream in = request.doPost();
			String[] response = parseResponses(in, CaptchaTraderRequestType.RESPOND.respSize());
			if("-1".equals(response[0])) {
				throw new CaptchaTraderException(response[1]);
			}
			_activeJobId = null;
		}
	}
	/**
	 * Get the credits remaining on the current user.
	 * @return The number of credits remaining.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/get_credits target=_blank>http://captchatrader.com/documentation/get_credits</a>
	 * </br>
	 */
	public int getCredits() throws Exception
	{
		InputStream in = (new CaptchaTraderRequest(CaptchaTraderRequestType.QUERY_CREDIT,
												   _username, _password)).doGet();
		String[] response = parseResponses(in, CaptchaTraderRequestType.QUERY_CREDIT.respSize());
		if(Integer.parseInt(response[0]) == -1) {
			throw new CaptchaTraderException(response[1]);
		}
		return Integer.parseInt(response[1]);
	}
	/**
	 * Get the wait time on the current user.  If no user is specified, return the wait time for the last user in queue.
	 * @return The estimated remaining wait time in seconds.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/get_wait_time target=_blank>http://captchatrader.com/documentation/get_wait_time</a>
	 * </br>
	 */
	public int getWaitTime()
		throws Exception
	{
		InputStream in = (new CaptchaTraderRequest(CaptchaTraderRequestType.QUERY_WAIT_TIME,
														 _username, _password)).doGet();
		String[] response = parseResponses(in, CaptchaTraderRequestType.QUERY_WAIT_TIME.respSize());
		if(Integer.parseInt(response[0]) == -1) {
			throw new CaptchaTraderException(response[1]);
		}
		return Integer.parseInt(response[2]);
	}
	/**
	 * Get the solver queue size
	 * @return Two numbers are returned: the number of users ahead of the requesting user and the total queue size.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 */
	public int[] getQueueSize()
		throws Exception
	{
	    InputStream in = (new CaptchaTraderRequest(CaptchaTraderRequestType.QUERY_WAIT_TIME,
												   _username, _password)).doGet();
		String[] response = parseResponses(in, CaptchaTraderRequestType.QUERY_WAIT_TIME.respSize());
		if(Integer.parseInt(response[0]) == -1) {
			throw new CaptchaTraderException(response[1]);
		}
		int[] qSize = new int[2];
		qSize[0] = Integer.parseInt(response[0]);
		qSize[1] = Integer.parseInt(response[1]);
		return qSize;
	}
	/**
	 * Add a user to the job delegation queue.
	 * @return A data URI of a base64 encoded image.
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/enqueue target=_blank>http://captchatrader.com/documentation/enqueue</a>
	 * </br>
	 */
	public String enqueue()
		throws Exception
	{
		if (_enqueueJobId!=null) {
			throw new CaptchaTraderException("Max 1 enqueue connection limit reached");
		}

		CaptchaTraderRequest ctRequest = new CaptchaTraderRequest(CaptchaTraderRequestType.ENQUEUE,
																  _username, _password);
		InputStream in = ctRequest.doGet();

		String[] response = parseResponses(in, CaptchaTraderRequestType.ENQUEUE.respSize());
		if(Integer.parseInt(response[0]) == -1) {
			throw new CaptchaTraderException(response[1]);
		}
		_enqueueJobId = response[0];
		return response[1];
	}

	/**
	 * Remove the user from all pending jobs and the job queue.
	 * @return 0 for successful operation
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/dequeue target=_blank>http://captchatrader.com/documentation/dequeue</a>
	 * </br>
	 */
	public int dequeue()
		throws Exception
	{
		CaptchaTraderRequest request = new CaptchaTraderRequest(CaptchaTraderRequestType.DEQUEUE);
		request.setParameter("username", _username);
		request.setParameter("password", _password);

		InputStream in = request.doPost();

		_enqueueJobId = null;

		String[] response = parseResponses(in, CaptchaTraderRequestType.DEQUEUE.respSize());

		if("-1".equals(response[0])) {
			throw new CaptchaTraderException(response[1]);
		}

		return 0;
	}

	/**
	 * Provide an answer to a job
	 * @return 0 for successful operation
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * @throws Exception Any other exception sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/answer target=_blank>http://captchatrader.com/documentation/answer</a>
	 * </br>
	 */
	public int answer(String value)
		throws Exception
	{
		if (_enqueueJobId==null) {
			throw new CaptchaTraderException("No enqueued job found");
		}
		CaptchaTraderRequest request = new CaptchaTraderRequest(CaptchaTraderRequestType.ANSWER);
		request.setParameter("username", _username);
		request.setParameter("password", _password);
		request.setParameter("ticket", _enqueueJobId);
		request.setParameter("value", value);
		_enqueueJobId = null;

		InputStream in = request.doPost();

		String[] response = parseResponses(in, CaptchaTraderRequestType.ANSWER.respSize());
		if("-1".equals(response[0])) {
			throw new CaptchaTraderException(response[1]);
		}
		return 0;
	}

	private String[] parseResponses(InputStream in, int respSize)
		throws Exception
	{
		CaptchaTraderParser ctParser = new CaptchaTraderParser(in, respSize);
		return ctParser.getResponses();
	}

	/**
	 * Returns a {@link com.captchatrader.CaptchaTraderTask CaptchaTraderTask} for use by asynchonous job submission
	 * @return CaptchaTraderTask object
	 * @since v2012.1
	 */
	public CaptchaTraderTask<String> getTask()
	{
		CaptchaTraderTask<String> ctTask = new CaptchaTraderTask<String>(this);
		return ctTask;
	}
	/**
	 * Submit a CAPTCHA currently saved as a file, in asynchronous mode.
	 * @param file The file that the CAPTCHA is saved as.
	 * @param task The CaptchaTraderTask for this submission.
	 * @return The passed-in CaptchaTraderTask object
	 * @throws CaptchaTraderException An error has been returned by this API call
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 * @since v2012.1
	 */
	public CaptchaTraderTask submit(File file, CaptchaTraderTask task)
		throws CaptchaTraderException
	{
		if (_url!=null || _file!=null) {
			throw new CaptchaTraderException("Last submitted job has not completed yet.");
		}
		_file = file;

		// Run the task in a new thread
		Thread t = new Thread(task);
		t.start();

		return task;
	}
	/**
	 * Submit a CAPTCHA already hosted on an existing website, in asynchronous mode.
	 * @param url The URL of the CAPTCHA image.
	 * @param task The CaptchaTraderTask for this submission.
	 * @return The passed-in CaptchaTraderTask object
	 * @throws CaptchaTraderException If last submitted job is not completed yet.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 * @since v2012.1
	 */
	public CaptchaTraderTask submit(URL url, CaptchaTraderTask task)
		throws CaptchaTraderException
	{
		if (_url!=null || _file!=null) {
			throw new CaptchaTraderException("Last submitted job has not completed yet.");
		}
		_url = url;

		// Run the task in a new thread
		Thread t = new Thread(task);
		t.start();

		return task;
	}

	/**
	 * Submit a CAPTCHA currently saved as a file, in asynchronous mode.
	 * @param file The file that the CAPTCHA is saved as.
	 * @param event The ResponseEvent object for callback upon server response
	 * @return The passed-in ResponseEvent object
	 * @throws Exception Any exceptions sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 * @since v2012.1
	 */
	public ResponseEvent submit(File file, ResponseEvent event)
		throws Exception
	{
		_event = event;

		submit(file, getTask());
		return event;
	}
	/**
	 * Submit a CAPTCHA already hosted on an existing website, in asynchronous mode.
	 * @param url The URL of the CAPTCHA image.
	 * @param event The ResponseEvent object for callback upon server response
	 * @return The passed-in ResponseEvent object
	 * @throws Exception Any exceptions sent by the server.
	 * <br>
	 * For a list of error codes, see <a href=http://captchatrader.com/documentation/submit target=_blank>http://captchatrader.com/documentation/submit</a>
	 * </br>
	 * @since v2012.1
	 */
	public ResponseEvent submit(URL url, ResponseEvent event)
		throws Exception
	{
		_event = event;
		submit(url, getTask());
		return event;
	}

	/**
	 * Application should not call this method directly
	 */
	public String call()
		throws Exception
	{
		CaptchaTraderRequest request = new CaptchaTraderRequest();
		if (_file!=null) {
			request.setParameter("value", _file);
		} else if(_url!=null) {
			request.setParameter("value", _url.toString());
		} else {
			throw new CaptchaTraderException("No File or URL specified for this job");
		}
		String result = submitJob(request);

		_url = null;
		_file = null;

		if (_event!=null) {
			_event.respond(result);
			_event = null;
		}

		return result;
	}
}
