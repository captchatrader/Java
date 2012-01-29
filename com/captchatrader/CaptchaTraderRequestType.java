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


/**
 * @version 2012.1
 */
public enum CaptchaTraderRequestType
{
	SUBMIT           ("http://api.captchatrader.com/submit",        true,  2),
	RESPOND          ("http://api.captchatrader.com/respond",       true,  1),
	QUERY_CREDIT     ("http://api.captchatrader.com/get_credits",   false, 2),
	QUERY_WAIT_TIME  ("http://api.captchatrader.com/get_wait_time", false, 3),
	ENQUEUE          ("http://api.captchatrader.com/enqueue",       false, 2),
	ANSWER           ("http://api.captchatrader.com/answer",        true,  1),
	DEQUEUE          ("http://api.captchatrader.com/dequeue",       true,  1);

	private String _url;
	private boolean _doPost;
	private int _respSize;
	CaptchaTraderRequestType(String url, boolean doPost, int respSize)
	{
		_url = url;
		_doPost = doPost;
		_respSize = respSize;
	}

	public String url()
	{
		return _url;
	}

	public boolean doPost()
	{
		return _doPost;
	}

	public int respSize()
	{
		return _respSize;
	}
}
