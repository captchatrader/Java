package com.captchatrader;

/**
 * Applications wish to use callback for submitting captchas in asynchronous mode should create a class that implements ResponseEvent.  The method respond() will be called upon server response.
 *
 * <PRE>
 * Example:
 *
 *    import com.captchatrader.*;
 *    public class MyResponseEvent implements ResponseEvent
 *    {
 *        private boolean gotResponse = false;
 *        public boolean gotResponse()
 *        {
 *            return gotResponse;
 *        }
 *        public void respond(String response)
 *        {
 *            System.out.println("Got response: " + response);
 *            gotResponse = true;
 *        }
 *    }
 * </PRE>
 *
 * @since v2012.1
 * @version 2012.1
 */
public interface ResponseEvent
{
	/**
	 * The callback method will be called upon server response to job submitted with a ResponseEvent object
	 */
	public void respond(String response);
}
