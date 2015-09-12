// Copyright (c) 2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.js.ut;
import goryachev.common.util.Base64;
import goryachev.common.util.HSLColor;
import goryachev.common.util.Hex;
import goryachev.notebook.js.JsUtil;
import goryachev.notebook.util.DigestTools;
import goryachev.notebook.util.InlineHelp;
import java.awt.Color;
import java.io.File;
import java.security.MessageDigest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.NativeArray;


public class UT
{
	public UT()
	{
	}
	
	
	public void sleep(long ms) throws Exception
	{
		Thread.sleep(ms);
	}
	
	
	public Color hslColor(float hue, float sat, float lum)
	{
		return HSLColor.toColor(hue, sat, lum);
	}
	
	
	public Document parseHtml(Object x)
	{
		return Jsoup.parse(x.toString());
	}
	
	
	public String toString()
	{
		return getHelp().toString();
	}
	
	
	public String encodeBase64(byte[] b)
	{
		return Base64.encode(b);
	}
	
	
	public byte[] decodeBase64(String s) throws Exception
	{
		return Base64.decode(s.trim());
	}
	
	
	public String encodeHex(byte[] b)
	{
		return Hex.toHexString(b);
	}
	
	
	public byte[] decodeHex(String s) throws Exception
	{
		return Hex.parseByteArray(s.trim());
	}
	
	
	public BBuffer computeDigest(String name, Object x) throws Exception
	{
		MessageDigest d = MessageDigest.getInstance(name);
		
		if(JsUtil.isConvertableToByteArray(x))
		{
			byte[] b = JsUtil.parseByteArray(x);
			return DigestTools.compute(d, b);
		}
		else
		{
			String filename = x.toString();
			File f = new File(filename);
			return DigestTools.compute(d, f);
		}
	}
	
	
	public BBuffer sha512(Object x) throws Exception
	{
		return computeDigest("sha-512", x);
	}
	
	
	public BBuffer sha256(Object x) throws Exception
	{
		return computeDigest("sha-256", x);
	}
	
	
	public BBuffer newByteBuffer(int size)
	{
		return new BBuffer(size);
	}
	
	
	public InlineHelp getHelp()
	{
		InlineHelp h = new InlineHelp("");
		h.a("UT offers helpful utility functions:");
		//
		h.a("computeDigest(algorithm, x)", "computes digest of a byte array or a file");
		h.a("decodeBase64(string)", "decodes Base64-encoded string");
		h.a("encodeBase64(bytes)", "encodes a byte array using Base64");
		h.a("decodeHex(string)", "decodes a hexadecimal string");
		h.a("encodeHex(bytes)", "encodes a byte array into a hexadecimal string");
		h.a("hslColor(hue,saturation,luminocity)", "creates color from HSL values");
		h.a("newByteBuffer(size)", "returns a new BBuffer instance");
		h.a("parseHtml(html)", "parses HTML document");
		h.a("sha256(x)", "computes SHA-256 digest of a byte array or a file");
		h.a("sha512(x)", "computes SHA-512 digest of a byte array or a file");
		h.a("sleep(ms)", "sleeps for the specified number of milliseconds");
		return h;
	}
}
