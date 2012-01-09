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
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

enum XmlTag {
	ERROR ("error"),
		STATUS ("status"),
		USER ("user"),
		CREDITS ("credits"),
		QUEUE ("queue"),
		POSITION ("position"),
		LENGTH ("length"),
		ETA ("eta"),
		SUCCESS ("success"),
		JOB ("job"),
		ID ("id"),
		CHALLENGE ("challenge"),
		RESPONSE ("response");
	String _tag;
	XmlTag (String tag) {
		_tag = tag;
	}
	public String tag()
	{
		return _tag;
	}
 };

class CaptchaTraderParser
{
	private String[] _responses = null;

	public CaptchaTraderParser(InputStream in, int respSize)
	{
		_responses = new String[respSize];

		try {
			DocumentBuilder dBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(in);
			Element elem = doc.getDocumentElement();
			elem.normalize();

			String rootTag = elem.getNodeName();

			// ERROR
			if (XmlTag.ERROR.tag().equals(rootTag)) {
				_responses = new String[3];
				_responses[0] = "-1";
				_responses[1] = elem.getTextContent();
				_responses[2] = elem.getAttribute(XmlTag.STATUS.tag());

				// ENQUEUE, SUBMIT
			} else if (XmlTag.JOB.tag().equals(rootTag)) {
				_responses[0] = elem.getAttribute(XmlTag.ID.tag());
				if (elem.getElementsByTagName(XmlTag.CHALLENGE.tag()).item(0)!=null) {
					_responses[1] = elem.getElementsByTagName(XmlTag.CHALLENGE.tag()).item(0).getTextContent();
				}
				if (elem.getElementsByTagName(XmlTag.RESPONSE.tag()).item(0)!=null) {
					_responses[1] = elem.getElementsByTagName(XmlTag.RESPONSE.tag()).item(0).getTextContent();
				}
				// DEQUEUE, RESPOND, ANSWER
			} else if (XmlTag.SUCCESS.tag().equals(rootTag)) {
				_responses[0] = elem.getTextContent();

				// GET CREDITS
			} else if (XmlTag.USER.tag().equals(rootTag)) {
				_responses[0] = "0";
				_responses[1] = elem.getElementsByTagName(XmlTag.CREDITS.tag()).item(0).getTextContent();

				// GET WAIT TIME
			} else if (XmlTag.QUEUE.tag().equals(rootTag)) {
				_responses[0] = elem.getElementsByTagName(XmlTag.POSITION.tag()).item(0).getTextContent();
				_responses[1] = elem.getElementsByTagName(XmlTag.LENGTH.tag()).item(0).getTextContent();
				_responses[2] = elem.getElementsByTagName(XmlTag.ETA.tag()).item(0).getTextContent();

				// SUBMIT
			} else if (XmlTag.QUEUE.tag().equals(rootTag)) {
				_responses[0] = elem.getElementsByTagName(XmlTag.POSITION.tag()).item(0).getTextContent();
				_responses[1] = elem.getElementsByTagName(XmlTag.LENGTH.tag()).item(0).getTextContent();
				_responses[2] = elem.getElementsByTagName(XmlTag.ETA.tag()).item(0).getTextContent();
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}

	public String[] getResponses()
	{
		return _responses;
	}
}
