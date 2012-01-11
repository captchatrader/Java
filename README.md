CaptchaTrader Java API
======================

This library allows interaction with http://captchatrader.com/ through Java.

Usage
-----

Compile the Java code with Java 6.0 or download the provided .jar file.

Public API
----------

### Constructor

Create a new CaptchaTrader instance.

	public CaptchaTrader(String apiKey, String username, String password)
	public CaptchaTrader(String username, String password)
	public CaptchaTrader(String apiKey)
	public CaptchaTrader()

### Submit

Submit a CAPTCHA URL.

	public String submit(URL url)
	public String submit(File file)
	
### Respond

Respond to the last sent job.

	public void respond(boolean isCorrect)

### GetCredits

Get the credits remaining on the current user

	public int getCredits()

### GetWaitTime

Get the wait time on the current user.  If no user specified, return the wait time for the last user in queue.

	public int getWaitTime()

### GetQueueSize

Get the solver queue size.  Two numbers are returned: the number of users ahead of the requesting user and the total queue size.

	public int[] getQueueSize()

### Enqueue

Add a user to the job delegation queue.

	public String enqueue()

### Dequeue

Remove the user from all pending jobs and the job queue.

	public void dequeue()

### Answer

Provide an answer to a job.

	public void answer(String value)

Synchronous Submit Example
--------------------------

	CaptchaTrader ct = new CaptchaTrader(<apiKey>, <username>, <password>);
	URL url = new URL("http://www.google.com/recaptcha/api/image?c=03AHJ_VuuH-DBRSxMQgwIJM4L5B5-CmEDLCigmIPZcc50vRJVSXRIp0dDZKRskWTXgiM7m0T2nus0PH4gFWC74QPWjX9W9dzpN-qpRWQJ3GO7v4nF9oDCvI9TtfISCFeIcwzMJbh4aqfOq1_rhWjJ0Pmpbu-Uy1-Yj7A");
	String solution = ct.submit(url);
	ct.respond(true);