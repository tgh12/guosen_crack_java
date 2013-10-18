
/*
 * Copyright 2004 Markus Hahn 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.blowfishj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sourceforge.blowfishj.BinConverter;
import net.sourceforge.blowfishj.BlowfishCBC;
import net.sourceforge.blowfishj.BlowfishECB;
import net.sourceforge.blowfishj.BlowfishEasy;
import net.sourceforge.blowfishj.BlowfishInputStream;
import net.sourceforge.blowfishj.BlowfishOutputStream;

/**
 * Demonstrating the Blowfish encryption algorithm classes.
 */
public class BlowfishDemo
{
	// max. size of message to encrypt

	final static int MAX_MESS_SIZE = 64;

	// benchmark settings

	final static int TESTBUFSIZE = 100000;
	final static int TESTLOOPS = 10000;

	// BlowfishEasy reference

	final static String BFEASY_REF_PASSW = "secret";
	final static String BFEASY_REF_TEXT = "Protect me.";

	// startup CBC IV

	final static long CBCIV_START = 0x0102030405060708L;

	// things necessary for compatibility testing

	final static byte[] XCHG_KEY =
	{
		(byte)0xaa, (byte)0xbb, (byte)0xcc, 0x00, 0x42, 0x33
	};
	final static int XCHG_DATA_SIZE = 111;
	
	public static void zhwTest(){
		byte[] testKey = {88,77,78,113,120,119,43,82,104,101,109,98,102,65,53,75};
		BlowfishCBC bfc;
		bfc = new BlowfishCBC(testKey, 0, testKey.length, 0L);
		
		
		byte[] msgBuf = {18,1,0,0,95,83,-74,-103,115,2,0,0,120,1,99,96,64,5,28,64,110,93,110,126,94,106,101,73,101,65,42,68,14,68,-127,-60,65,64,-67,-105,49,-18,-79,31,99,5,-124,-121,73,-90,-107,-26,-91,36,37,-26,-64,37,56,-127,44,-112,94,16,118,-23,-67,56,49,-46,127,-18,12,-72,36,26,3,-92,55,-79,12,83,-81,0,80,-35,-5,96,-115,-46,-117,19,113,-21,45,46,-55,46,75,-52,41,-123,-70,-104,-127,1,102,-81,4,80,-17,-63,110,-117,32,-90,56,-101,0,52,-21,-32,-36,-36,-60,-94,-20,-44,18,-124,118,-104,94,5,-96,10,-105,-34,-27,126,-42,9,-72,-11,-126,-36,-100,-103,2,55,10,110,-81,6,88,-17,-59,-119,2,113,-26,73,8,89,84,22,90,48,-61,-11,26,0,-107,17,19,-50,-59,-87,-123,112,3,97,110,-74,0,-118,88,-5,-127,66,-102,61,-29,122,60,92,26,-123,-63,8,-27,-63,-30,-123,13,-56,-25,3,98,16,45,2,-60,32,-77,100,-95,-76,26,-112,-26,5,98,99,32,102,2,98,83,40,109,14,-92,119,-7,9,-26,48,-58,1,-59,-116,-12,12,13,-96,-92,-95,-87,-119,-123,-119,-98,-127,1,-125,-95,-87,-87,-79,25,72,-36,-40,-48,0,8,12,13,13,76,13,76,25,12,-128,16,0,11,-116,67,-96,-10,-110,-79,20,-20,52} ;
		
		bfc.setCBCIV(0);
		bfc.decrypt(msgBuf, 0, msgBuf, 0, msgBuf.length);

		System.out.println("CBC decrypted: >>>" + new String(msgBuf) + "<<<");
	}


	/**
	 * the application entry point
	 * @param args (command line) parameters
	 */
	public static void main(
		String args[])
	{
		
		zhwTest();
		
		int nI, nJ;
		int nRest, nMsgSize, nLnBrkLen;
		long lTm, lRate;
		double dAmount, dTime, dRate;
		String sEnc, sDec;
		byte[] testKey, tempBuf, cpyBuf, msgBuf, showIV;
		BlowfishECB bfe;
		BlowfishCBC bfc;
		BlowfishEasy bfes;
		BlowfishInputStream bfis;
		BlowfishOutputStream bfos;
		ByteArrayOutputStream baos;


		// first do the self test

		System.out.print("running self test...");

		if (!BlowfishECB.selfTest())
		{
			System.out.println(", FAILED");
			return;
		}

		System.out.println(", passed.");

		// now the classic examples...

		// create our test key

		testKey = new byte[5];
		for (nI = 0; nI < testKey.length; nI++)
		{
			testKey[nI] = (byte) (nI + 1);
		}

		// do the key setups and check for weaknesses

		System.out.print("setting up Blowfish keys...");

		bfe = new BlowfishECB(testKey, 0, testKey.length);

		bfc = new BlowfishCBC(
			testKey,
			0,
			testKey.length,
			CBCIV_START);

		System.out.println(", done.");

		if (bfe.weakKeyCheck())
		{
			System.out.println("ECB key is weak!");
		}
		else
		{
			System.out.println("ECB key OK");
		}

		if (bfc.weakKeyCheck())
		{
			System.out.println("CBC key is weak!");
		}
		else
		{
			System.out.println("CBC key OK");
		}

		// get a message

		System.out.print("something to encrypt please >");
		System.out.flush();

		tempBuf = new byte[MAX_MESS_SIZE];

		nMsgSize = 0;
		nLnBrkLen = 0;

		try
		{
			nLnBrkLen = System.getProperty("line.separator").length();
		}
		catch (Throwable err)
		{
		};

		try
		{
			// (cut off the line break)
			nMsgSize = System.in.read(tempBuf) - nLnBrkLen;
			cpyBuf = new byte[nMsgSize];
			System.arraycopy(tempBuf, 0, cpyBuf, 0, nMsgSize);
			tempBuf = cpyBuf;
		}
		catch (java.io.IOException ioe)
		{
			return;
		}

		// align to the next 8 byte border

		nRest = nMsgSize & 7;

		if (nRest != 0)
		{
			msgBuf = new byte[(nMsgSize & (~7)) + 8];

			System.arraycopy(tempBuf, 0, msgBuf, 0, nMsgSize);

			for (nI = nMsgSize; nI < msgBuf.length; nI++)
			{
				msgBuf[nI] = 0;
			}

			System.out.println(
				"message with "
					+ nMsgSize
					+ " bytes aligned to "
					+ msgBuf.length
					+ " bytes");
		}
		else
		{
			msgBuf = new byte[nMsgSize];

			System.arraycopy(tempBuf, 0, msgBuf, 0, nMsgSize);
		}

		System.out.println(
			"aligned data : " + BinConverter.bytesToHexStr(msgBuf));

		// ECB encryption/decryption test

		bfe.encrypt(msgBuf, 0, msgBuf, 0, msgBuf.length);

		// show the result

		System.out.println(
			"ECB encrypted: " + BinConverter.bytesToHexStr(msgBuf));

		bfe.decrypt(msgBuf, 0, msgBuf, 0, msgBuf.length);

		System.out.println("ECB decrypted: >>>" + new String(msgBuf) + "<<<");

		// CBC encryption/decryption test

		showIV = new byte[BlowfishCBC.BLOCKSIZE];

		bfc.getCBCIV(showIV, 0);

		System.out.println("CBC IV: " + BinConverter.bytesToHexStr(showIV));

		bfc.encrypt(msgBuf, 0, msgBuf, 0, msgBuf.length);

		// show the result

		System.out.println(
			"CBC encrypted: " + BinConverter.bytesToHexStr(msgBuf));

		bfc.setCBCIV(CBCIV_START);
		bfc.decrypt(msgBuf, 0, msgBuf, 0, msgBuf.length);

		System.out.println("CBC decrypted: >>>" + new String(msgBuf) + "<<<");

		System.out.println("tests done.");

		// demonstrate easy encryption

		bfes = new BlowfishEasy(BFEASY_REF_PASSW.toCharArray());

		System.out.println(sEnc = bfes.encryptString(BFEASY_REF_TEXT));
		System.out.println(bfes.decryptString(sEnc));

		// show stream handling

		try
		{
			bfos = new BlowfishOutputStream(
				XCHG_KEY,
				0,
				XCHG_KEY.length,
				baos = new ByteArrayOutputStream());

			for (nI = 0; nI < XCHG_DATA_SIZE; nI++)
			{
				bfos.write(nI);
			}

			bfos.close();

			tempBuf = baos.toByteArray();

			System.out.println(BinConverter.bytesToHexStr(tempBuf));

			bfis = new BlowfishInputStream(
				XCHG_KEY,
				0,
				XCHG_KEY.length,
				new ByteArrayInputStream(tempBuf));

			for (nI = 0; nI < XCHG_DATA_SIZE; nI++)
			{
				if ((nI & 0x0ff) != bfis.read())
				{
					System.out.println(
						"corrupted data at position " + nI);
				}
			}

			bfis.close();
		}
		catch (IOException ie)
		{
			System.out.println(ie);
		}

		// benchmark

		System.out.println("\nrunning benchmark (CBC)...");

		lTm = System.currentTimeMillis();

		tempBuf = new byte[TESTBUFSIZE];

		for (nI = 0; nI < TESTLOOPS; nI++)
		{
			bfc.encrypt(tempBuf, 0, tempBuf, 0, tempBuf.length);

			if (0 == (nI % (TESTLOOPS / 40)))
			{
				System.out.print("#");
				System.out.flush();
			}
		}

		lTm = System.currentTimeMillis() - lTm;

		System.out.println();

		dAmount = TESTBUFSIZE * TESTLOOPS;
		dTime = lTm;
		dRate = (dAmount * 1000) / dTime;
		lRate = (long) dRate;

		System.out.println(+ lRate + " bytes/sec");

		bfe.cleanUp();
		bfc.cleanUp();
	}

}
