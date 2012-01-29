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

import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.Random;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @version 2012.1
 */
class CaptchaTraderRequest
{
	private URLConnection _urlConnection;
	private OutputStream _os = null;
	private URL _url = null;

	protected void write(String s)
		throws IOException
	{
		if (_os!=null) {
			_os.write(s.getBytes());
		}
	}

	protected void newline()
		throws IOException
	{
		write("\r\n");
	}

	protected void writeln(String s)
		throws IOException
	{
		write(s);
		newline();
	}

	private static Random random = new Random();

	protected static String randomString()
	{
		return Long.toString(random.nextLong(), 36);
	}

	String boundary = "---------------------------" + randomString();

	private void boundary() throws IOException {
		write("--");
		write(boundary);
	}

	private void connectionInit(CaptchaTraderRequestType type)
		throws IOException
	{
		connectionInit(type, null, null);
	}

	private void connectionInit(CaptchaTraderRequestType type, String username, String password)
		throws IOException
	{
		String param = (type.doPost()||username==null||password==null) ? "" : "/username:" + username + "/password:" + password;
		_url = new URL(type.url() + param + ".xml");
		if (type.doPost()) {
			_urlConnection = _url.openConnection();
			_urlConnection.setDoOutput(type.doPost());
			_urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			_os = _urlConnection.getOutputStream();
		}
	}

	public CaptchaTraderRequest()
		throws IOException
	{
		connectionInit(CaptchaTraderRequestType.SUBMIT);
	}

	public CaptchaTraderRequest(CaptchaTraderRequestType type)
		throws IOException
	{
		connectionInit(type);
	}

	public CaptchaTraderRequest(CaptchaTraderRequestType type, String username, String password)
		throws IOException
	{
		connectionInit(type, username, password);
	}

	private void writeName(String name)
		throws IOException
	{
		newline();
		write("Content-Disposition: form-data; name=\"");
		write(name);
		write("\"");
	}

	public void setParameter(String name, String value)
		throws IOException
	{
		boundary();
		writeName(name);
		newline(); newline();
		writeln(value);
	}

	private static void pipe(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buf = new byte[500000];
		int nread;
		synchronized (in) {
			while((nread = in.read(buf, 0, buf.length)) >= 0) {
				out.write(buf, 0, nread);
			}
		}
		out.flush();
		buf = null;
	}

	public void setParameter(String name, File file)
		throws IOException
	{
		if (_os==null) {
			throw new IOException("Request type initialized does not support file uploading");
		}

		String filename = file.getPath();
		FileInputStream is = new FileInputStream(file);

		boundary();
		writeName(name);
		write("; filename=\"");
		write(filename);
		write("\"");
		newline();
		write("Content-Type: ");
		String type = _urlConnection.guessContentTypeFromName(filename);
		if (type == null) {
			type = "application/octet-stream";
		}
		writeln(type);
		newline();
		pipe(is, _os);
		newline();
	}

	public void setParameter(String name, Object object)
		throws IOException
	{
		if (object instanceof File) {
			setParameter(name, (File) object);
		} else {
			setParameter(name, object.toString());
		}
	}

	public BufferedReader post()
		throws IOException
	{
		if (_os==null) {
			throw new IOException("Request type does not support POST");
		}
		boundary();
		writeln("--");
		_os.close();

		return new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
	}

	public InputStream doPost()
		throws IOException
	{
		if (_os==null) {
			throw new IOException("Request type does not support POST");
		}
		boundary();
		writeln("--");
		_os.close();

		return _urlConnection.getInputStream();
	}

	public BufferedReader get()
		throws IOException
	{
		return new BufferedReader(new InputStreamReader(_url.openConnection().getInputStream()));
	}

	public InputStream doGet()
		throws IOException
	{
		return _url.openConnection().getInputStream();
	}
}
