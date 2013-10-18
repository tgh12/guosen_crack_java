
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

package net.sourceforge.blowfishj;

/**
 * Implementation of the Blowfish encryption algorithm in CBC mode.
 */
public class BlowfishCBC extends BlowfishECB
{

	// the CBC IV

	int m_nIVLo;
	int m_nIVHi;

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the current CBC IV.
	 * @return current CBC IV
	 */
	public long getCBCIV()
	{
		return BinConverter.makeLong(m_nIVLo, m_nIVHi);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Gets a copy of the current CBC IV.
	 * @param dest where to put current CBC IV
	 * @deprecated use getCBCIV(byte[], int) instead
	 */
	public void getCBCIV(
		byte[] dest)
	{
		getCBCIV(dest, 0);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Gets a copy of the current CBC IV.
	 * @param dest buffer
	 * @param nOfs where to start writing
	 */
	public void getCBCIV(
		byte[] dest,
		int nOfs)
	{
		BinConverter.intToByteArray(m_nIVHi, dest, nOfs);
		BinConverter.intToByteArray(m_nIVLo, dest, nOfs + 4);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the current CBC IV (for cipher resets).
	 * @param lNewCBCIV the new CBC IV
	 */
	public void setCBCIV(
		long lNewCBCIV)
	{
		m_nIVHi = BinConverter.longHi32(lNewCBCIV);
		m_nIVLo = BinConverter.longLo32(lNewCBCIV);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the current CBC IV (for cipher resets).
	 * @param newCBCIV the new CBC IV
	 * @deprecated use setCBCIV(byte[], int) instead
	 */
	public void setCBCIV(
		byte[] newCBCIV)
	{
		setCBCIV(newCBCIV, 0);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the current CBC IV (for cipher resets).
	 * @param newCBCIV the new CBC IV
	 * @param nOfs where to start reading the IV
	 */
	public void setCBCIV(
		byte[] newCBCIV,
		int nOfs)
	{
		m_nIVHi = BinConverter.byteArrayToInt(newCBCIV, nOfs);
		m_nIVLo = BinConverter.byteArrayToInt(newCBCIV, nOfs + 4);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor, uses a zero CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @deprecated use BlowfishCBC(byte[], int, int) instead
	 */
	public BlowfishCBC(
		byte[] key)
	{
		super(key);

		m_nIVHi = m_nIVLo = 0;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor, uses a zero CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @param nOfs where to start reading the key
	 * @param nLen size of the key in bytes
	 */
	public BlowfishCBC(
		byte[] key,
		int nOfs,
		int nLen)
	{
		super(key, nOfs, nLen);

		m_nIVHi = m_nIVLo = 0;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor to define the CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @param lInitCBCIV the CBC IV
	 * @deprecated use BlowfishCBC(byte[], int, int, long) instead
	 */
	public BlowfishCBC(
		byte[] key,
		long lInitCBCIV)
	{
		super(key);

		setCBCIV(lInitCBCIV);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor to define the CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @param nOfs where to start reading the key
	 * @param nLen size of the key in bytes
	 * @param lInitCBCIV the CBC IV
	 */
	public BlowfishCBC(
		byte[] key,
		int nOfs,
		int nLen,
		long lInitCBCIV)
	{
		super(key, nOfs, nLen);

		setCBCIV(lInitCBCIV);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor to define the CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @param initCBCIV the CBC IV (array with BLOCKSIZE bytes)
	 * @deprecated use BlowfishCBC(byte[], int, int, byte[], int) instead
	 */
	public BlowfishCBC(
		byte[] key,
		byte[] initCBCIV)
	{
		super(key);

		setCBCIV(initCBCIV);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor to define the CBC IV.
	 * @param key key material, up to MAXKEYLENGTH bytes
	 * @param nOfs where to start reading the key
	 * @param nLen size of the key in bytes
	 * @param initCBCIV the CBC IV
	 * @param nIVOfs where to start reading the IV
	 */
	public BlowfishCBC(
		byte[] key,
		int nOfs,
		int nLen,
		byte[] initCBCIV,
		int nIVOfs)
	{
		super(key, nOfs, nLen);

		setCBCIV(initCBCIV, nIVOfs);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * see net.sourceforge.blowfishj.BlowfishECB#cleanUp()
	 */
	public void cleanUp()
	{
		m_nIVHi = m_nIVLo = 0;
		super.cleanUp();
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(byte[], int, byte[], int, int)
	 */
	public int encrypt(
		byte[] inBuf,
		int nInPos,
		byte[] outBuf,
		int nOutPos,
		int nLen)
	{
		// same speed tricks than in the ECB variant ...

		nLen -= nLen % BLOCKSIZE;

		long lTemp;

		int nC = nInPos + nLen;

		int[] pbox = m_pbox;
		int nPBox00 = pbox[0];
		int nPBox01 = pbox[1];
		int nPBox02 = pbox[2];
		int nPBox03 = pbox[3];
		int nPBox04 = pbox[4];
		int nPBox05 = pbox[5];
		int nPBox06 = pbox[6];
		int nPBox07 = pbox[7];
		int nPBox08 = pbox[8];
		int nPBox09 = pbox[9];
		int nPBox10 = pbox[10];
		int nPBox11 = pbox[11];
		int nPBox12 = pbox[12];
		int nPBox13 = pbox[13];
		int nPBox14 = pbox[14];
		int nPBox15 = pbox[15];
		int nPBox16 = pbox[16];
		int nPBox17 = pbox[17];

		int[] sbox1 = m_sbox1;
		int[] sbox2 = m_sbox2;
		int[] sbox3 = m_sbox3;
		int[] sbox4 = m_sbox4;

		int nIVHi = m_nIVHi;
		int nIVLo = m_nIVLo;

		int nHi, nLo;

		while (nInPos < nC)
		{
			nHi  =  inBuf[nInPos++] << 24;
			nHi |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nHi |= (inBuf[nInPos++] <<  8) & 0x000ff00;
			nHi |=  inBuf[nInPos++]        & 0x00000ff;

			nLo  =  inBuf[nInPos++] << 24;
			nLo |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nLo |= (inBuf[nInPos++] <<  8) & 0x000ff00;
			nLo |=  inBuf[nInPos++]        & 0x00000ff;

			// extra step: chain with IV

			nHi ^= nIVHi;
			nLo ^= nIVLo;

			nHi ^= nPBox00;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox01;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox02;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox03;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox04;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox05;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox06;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox07;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox08;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox09;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox10;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox11;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox12;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox13;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox14;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox15;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox16;

			nLo ^= nPBox17;

			outBuf[nOutPos++] = (byte)(nLo >>> 24);
			outBuf[nOutPos++] = (byte)(nLo >>> 16);
			outBuf[nOutPos++] = (byte)(nLo >>>  8);
			outBuf[nOutPos++] = (byte) nLo;

			outBuf[nOutPos++] = (byte)(nHi >>> 24);
			outBuf[nOutPos++] = (byte)(nHi >>> 16);
			outBuf[nOutPos++] = (byte)(nHi >>>  8);
			outBuf[nOutPos++] = (byte) nHi;

			// (the encrypted block becomes the new IV)

			nIVHi = nLo;
			nIVLo = nHi;
		}

		m_nIVHi = nIVHi;
		m_nIVLo = nIVLo;

		return nLen;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(byte[], byte[])
	 * @deprecated use encrypt(byte[], int, byte[], int, int) instead
	 */
	public void encrypt(
		byte[] inBuf,
		byte[] outBuf)
	{
		encrypt(inBuf, 0, outBuf, 0, outBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(byte[])
	 * @deprecated use encrypt(byte[], int, int) instead
	 */
	public void encrypt(
		byte[] buf)
	{
		encrypt(buf, 0, buf, 0, buf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(int[], int, int[], int, int)
	 */
	public void encrypt(
		int[] inBuf,
		int nInPos,
		int[] outBuf,
		int nOutPos,
		int nLen)
	{
		int nC = nInPos + nLen;

		while (nInPos < nC)
		{
			BinConverter.intToByteArray(inBuf[nInPos++], m_blockBuf, 0);
			BinConverter.intToByteArray(inBuf[nInPos++], m_blockBuf, 4);

			encrypt(m_blockBuf);

			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 0);
			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 4);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(int[], int[])
	 * @deprecated use encrypt(int[], int, int[], int, int) instead
	 */
	public void encrypt(
		int[] inBuf,
		int[] outBuf)
	{
		encrypt(inBuf, 0, outBuf, 0, inBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(int[])
	 * @deprecated use encrypt(int[], int, int[], int, int) instead
	 */
	public void encrypt(
		int[] buf)
	{
		encrypt(buf, 0, buf, 0, buf.length);

	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(long[], int, long[], int, int)
	 */
	public void encrypt(
		long[] inBuf,
		int nInPos,
		long[] outBuf,
		int nOutPos,
		int nLen)
	{
		int nC = nInPos + nLen;

		while (nInPos < nC)
		{
			BinConverter.longToByteArray(inBuf[nInPos++], m_blockBuf, 0);

			encrypt(m_blockBuf);

			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 0);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(long[], long[])
	 * @deprecated use encrypt(long[], int, long[], int, int) instead
	 */
	public void encrypt(
		long[] inBuf,
		long[] outBuf)
	{
		encrypt(inBuf, 0, outBuf, 0, inBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#encrypt(long[])
	 * @deprecated use encrypt(long[], int, long[], int, int) instead
	 */
	public void encrypt(
		long[] buf)
	{
		encrypt(buf, 0, buf, 0, buf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(byte[], int, byte[], int, int)
	 */
	public int decrypt(
		byte[] inBuf,
		int nInPos,
		byte[] outBuf,
		int nOutPos,
		int nLen)
	{
		nLen -= nLen % BLOCKSIZE;

		int nC = nInPos + nLen;

		int[] pbox = m_pbox;
		int nPBox00 = pbox[0];
		int nPBox01 = pbox[1];
		int nPBox02 = pbox[2];
		int nPBox03 = pbox[3];
		int nPBox04 = pbox[4];
		int nPBox05 = pbox[5];
		int nPBox06 = pbox[6];
		int nPBox07 = pbox[7];
		int nPBox08 = pbox[8];
		int nPBox09 = pbox[9];
		int nPBox10 = pbox[10];
		int nPBox11 = pbox[11];
		int nPBox12 = pbox[12];
		int nPBox13 = pbox[13];
		int nPBox14 = pbox[14];
		int nPBox15 = pbox[15];
		int nPBox16 = pbox[16];
		int nPBox17 = pbox[17];

		int[] sbox1 = m_sbox1;
		int[] sbox2 = m_sbox2;
		int[] sbox3 = m_sbox3;
		int[] sbox4 = m_sbox4;

		int nIVHi = m_nIVHi;
		int nIVLo = m_nIVLo;

		int nTmpHi, nTmpLo;

		int nHi, nLo;

		while (nInPos < nC)
		{
			nHi  =  inBuf[nInPos++] << 24;
			nHi |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nHi |= (inBuf[nInPos++] <<  8) & 0x000ff00;
			nHi |=  inBuf[nInPos++]        & 0x00000ff;

			nLo  =  inBuf[nInPos++] << 24;
			nLo |= (inBuf[nInPos++] << 16) & 0x0ff0000;
			nLo |= (inBuf[nInPos++] <<  8) & 0x000ff00;
			nLo |=  inBuf[nInPos++]        & 0x00000ff;

			// (save the current block, it will become the new IV)
			nTmpHi = nHi;
			nTmpLo = nLo;

			nHi ^= nPBox17;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox16;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox15;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox14;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox13;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox12;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox11;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox10;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox09;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox08;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox07;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox06;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox05;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox04;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox03;
			nLo	^= (((sbox1[nHi >>> 24] + sbox2[(nHi >>> 16) & 0x0ff]) ^ sbox3[(nHi >>> 8) & 0x0ff]) + sbox4[nHi & 0x0ff]) ^ nPBox02;
			nHi	^= (((sbox1[nLo >>> 24] + sbox2[(nLo >>> 16) & 0x0ff]) ^ sbox3[(nLo >>> 8) & 0x0ff]) + sbox4[nLo & 0x0ff]) ^ nPBox01;

			nLo ^= nPBox00;

			// extra step: unchain

			nHi ^= nIVLo;
			nLo ^= nIVHi;

			outBuf[nOutPos++] = (byte)(nLo >>> 24);
			outBuf[nOutPos++] = (byte)(nLo >>> 16);
			outBuf[nOutPos++] = (byte)(nLo >>>  8);
			outBuf[nOutPos++] = (byte) nLo;

			outBuf[nOutPos++] = (byte)(nHi >>> 24);
			outBuf[nOutPos++] = (byte)(nHi >>> 16);
			outBuf[nOutPos++] = (byte)(nHi >>>  8);
			outBuf[nOutPos++] = (byte) nHi;

			// (now set the new IV)
			nIVHi = nTmpHi;
			nIVLo = nTmpLo;
		}

		m_nIVHi = nIVHi;
		m_nIVLo = nIVLo;

		return nLen;
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(byte[], byte[])
	 * @deprecated use decrypt(byte[], int, byte[], int, int) instead
	 */
	public void decrypt(
		byte[] inBuf,
		byte[] outBuf)
	{
		decrypt(inBuf, 0, outBuf, 0, outBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(byte[])
	 * @deprecated use encrypt(byte[], int, byte[], int, int) instead
	 */
	public void decrypt(
		byte[] buf)
	{
		decrypt(buf, 0, buf, 0, buf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(int[], int, int[], int, int)
	 */
	public void decrypt(
		int[] inBuf,
		int nInPos,
		int[] outBuf,
		int nOutPos,
		int nLen)
	{
		int nC = nInPos + nLen;

		while (nInPos < nC)
		{
			BinConverter.intToByteArray(inBuf[nInPos++], m_blockBuf, 0);
			BinConverter.intToByteArray(inBuf[nInPos++], m_blockBuf, 4);

			decrypt(m_blockBuf);

			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 0);
			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 4);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(int[], int[])
	 * @deprecated use decrypt(int[], int, int[], int, int) instead
	 */
	public void decrypt(
		int[] inBuf,
		int[] outBuf)
	{
		decrypt(inBuf, 0, outBuf, 0, inBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(int[])
	 * @deprecated use decrypt(int[], int, int[], int, int) instead
	 */
	public void decrypt(
		int[] buf)
	{
		decrypt(buf, 0, buf, 0, buf.length);

	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(long[], int, long[], int, int)
	 */
	public void decrypt(
		long[] inBuf,
		int nInPos,
		long[] outBuf,
		int nOutPos,
		int nLen)
	{
		int nC = nInPos + nLen;

		while (nInPos < nC)
		{
			BinConverter.longToByteArray(inBuf[nInPos++], m_blockBuf, 0);

			decrypt(m_blockBuf);

			outBuf[nOutPos++] = BinConverter.byteArrayToInt(m_blockBuf, 0);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(long[], long[])
	 * @deprecated use decrypt(long[], int, long[], int, int) instead
	 */
	public void decrypt(
		long[] inBuf,
		long[] outBuf)
	{
		decrypt(inBuf, 0, outBuf, 0, inBuf.length);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * @see net.sourceforge.blowfishj.BlowfishECB#decrypt(long[])
	 * @deprecated use decrypt(long[], int, long[], int, int) instead
	 */
	public void decrypt(
		long[] buf)
	{
		decrypt(buf, 0, buf, 0, buf.length);
	}
}
