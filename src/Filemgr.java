/**
 * 
 */


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.apache.commons.cli.*;

/**
 * @author physicsboy
 *
 */
public class Filemgr {
	private final String DFUPREFIX_szSignature = "DfuSe";
	private final byte DFUPREFIX_bVersion = 0x01, DFUPREFIX_bTargets = 0x01;
	private int DFUSUFFIX_bcdDevice = 0xffff, DFUSUFFIX_idProduct = 0xdf11, DFUSUFFIX_idVendor = 0x0483;
	public void setDFUSUFFIX_bcdDevice(int dFUSUFFIX_bcdDevice) {
		DFUSUFFIX_bcdDevice = dFUSUFFIX_bcdDevice;
	}
	public void setDFUSUFFIX_idProduct(int dFUSUFFIX_idProduct) {
		DFUSUFFIX_idProduct = dFUSUFFIX_idProduct;
	}
	public void setDFUSUFFIX_idVendor(int dFUSUFFIX_idVendor) {
		DFUSUFFIX_idVendor = dFUSUFFIX_idVendor;
	}
	private final int[] crc32_table = new int[]
			{
					0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f,
					0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
					0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2,
					0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
					0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
					0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
					0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
					0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
					0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423,
					0xcfba9599, 0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
					0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190, 0x01db7106,
					0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
					0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d,
					0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
					0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
					0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
					0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
					0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
					0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa,
					0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
					0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
					0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
					0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84,
					0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
					0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
					0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
					0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
					0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
					0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55,
					0x316e8eef, 0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
					0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28,
					0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
					0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f,
					0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
					0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
					0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
					0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
					0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
					0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc,
					0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
					0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693,
					0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
					0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d,
			};
	private byte[] dfu_prefix = new byte[11];//fixed length
	private byte[] dfu_suffix = new byte[12];//fixed length
	private byte[] data;
	private boolean verbose;
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	void create_dfuprefix(int size)
	{
		int total = size + 293;
		//szSignature
		System.arraycopy(this.DFUPREFIX_szSignature.getBytes(), 0, dfu_prefix, 0, 5);
		//bVersion
		dfu_prefix[5] = DFUPREFIX_bVersion;
		//DFUImageSize
		dfu_prefix[6] = (byte) (total & 0xFF);
		dfu_prefix[7] = (byte) ((total >>> 8) & 0xFF);
		dfu_prefix[8] = (byte) ((total >>> 16) & 0xFF);
		dfu_prefix[9] = (byte) ((total >>> 24) & 0xFF);
		//bTargets
		dfu_prefix[10] = DFUPREFIX_bTargets;
	}
	void create_dfusuffix(int bcdDevice, int idProduct, int idVendor)
	{
		//bcdDevice
		dfu_suffix[0] = (byte) (bcdDevice & 0xFF);
		dfu_suffix[1] = (byte) ((bcdDevice >>> 8) & 0xFF);
		//idProduct
		dfu_suffix[2] = (byte) (idProduct & 0xFF);
		dfu_suffix[3] = (byte) ((idProduct >>> 8) & 0xFF);
		//idVendor
		dfu_suffix[4] = (byte) (idVendor & 0xFF);
		dfu_suffix[5] = (byte) ((idVendor >>> 8) & 0xFF);
		//bcdDFU
		dfu_suffix[6] = (byte) 0x1A;
		dfu_suffix[7] = (byte) 0x01;
		//ucDfuSignature
		dfu_suffix[8] = (byte) 'U';
		dfu_suffix[9] = (byte) 'F';
		dfu_suffix[10] = (byte) 'D';
		//bLength
		dfu_suffix[11] = 0x10;
	}
	int crc32_byte(int accum, byte delta)
	{
		return crc32_table[(accum ^ delta) & 0xff] ^ (accum >>> 8);
	}
	public class DfuImage{
		private byte[] target_prefix = new byte[274], dfuimages;
		private final String TARGETPREFIX_szSignature = "Target";
		private final byte TARGETPREFIX_bAlternateSetting = 0x00, TARGETPREFIX_bTargetNamed = 0x01;
		private final String TARGETPREFIX_szTargetName;
		private int TARGETPREFIX_dwNbElements;
		private int dwTargetSize = 0;
		DfuImage(String targetname, String[] filelist){
			TARGETPREFIX_szTargetName = targetname;
			ImageElement image_element = new ImageElement();
			image_element.load_hexfile(filelist[0]);
			dwTargetSize += image_element.getImage_element().length;
			dfuimages = new byte[dwTargetSize];
			System.arraycopy(image_element.getImage_element(), 0, dfuimages, 0, image_element.getImage_element().length);
			for(int i = 1; i < filelist.length; i++){
				image_element.load_hexfile(filelist[i]);
				byte[] olddfuimage = dfuimages;
				dwTargetSize += image_element.getImage_element().length;
				dfuimages = new byte[dwTargetSize];
				System.arraycopy(olddfuimage, 0, dfuimages, 0, olddfuimage.length);
				System.arraycopy(image_element.getImage_element(), 0, dfuimages, olddfuimage.length, image_element.getImage_element().length);
			}
			TARGETPREFIX_dwNbElements = filelist.length;
			System.out.printf("Parsed %d file(s)\n", TARGETPREFIX_dwNbElements);
			create_tprefix();
		}
		DfuImage(String targetname, String[] filelist, String[] offsetlist){
			TARGETPREFIX_szTargetName = targetname;
			ImageElement image_element = new ImageElement();
			if(filelist.length == offsetlist.length){
				image_element.load_bin(filelist[0], offsetlist[0]);
				dwTargetSize += image_element.getImage_element().length;
				dfuimages = new byte[dwTargetSize];
				System.arraycopy(image_element.getImage_element(), 0, dfuimages, 0, image_element.getImage_element().length);
				for(int i = 1; i < filelist.length; i++){
					image_element.load_bin(filelist[i], offsetlist[i]);
					byte[] olddfuimage = dfuimages;
					dwTargetSize += image_element.getImage_element().length;
					dfuimages = new byte[dwTargetSize];
					System.arraycopy(olddfuimage, 0, dfuimages, 0, olddfuimage.length);
					System.arraycopy(image_element.getImage_element(), 0, dfuimages, olddfuimage.length, image_element.getImage_element().length);
				}
				TARGETPREFIX_dwNbElements = filelist.length;
				System.out.printf("Parsed %d file(s)\n", TARGETPREFIX_dwNbElements);
				create_tprefix();
			}
		}
		private void create_tprefix()
		{
			//szSignature
			System.arraycopy(this.TARGETPREFIX_szSignature.getBytes(), 0, target_prefix, 0, 6);
			//bAlternateSetting
			target_prefix[6] = TARGETPREFIX_bAlternateSetting;
			//bTargetNamed
			target_prefix[7] = TARGETPREFIX_bTargetNamed;
			//szTargetName
			byte[] chartmp = this.TARGETPREFIX_szTargetName.getBytes();
			System.arraycopy(chartmp, 0, target_prefix, 11, chartmp.length);
			for(int i=11+chartmp.length; i<266; i++){
				target_prefix[i] = 0;
			}
			//dwTargetSize
			target_prefix[266] = (byte) (dwTargetSize & 0xFF);
			target_prefix[267] = (byte) ((dwTargetSize >>> 8) & 0xFF);
			target_prefix[268] = (byte) ((dwTargetSize >>> 16) & 0xFF);
			target_prefix[269] = (byte) ((dwTargetSize >>> 24) & 0xFF);
			//dwNbElements
			target_prefix[270] = (byte) (TARGETPREFIX_dwNbElements & 0xFF);
			target_prefix[271] = (byte) ((TARGETPREFIX_dwNbElements >>> 8) & 0xFF);
			target_prefix[272] = (byte) ((TARGETPREFIX_dwNbElements >>> 16) & 0xFF);
			target_prefix[273] = (byte) ((TARGETPREFIX_dwNbElements >>> 24) & 0xFF);
		}
		public byte[] getDfuimage() {
			return dfuimages;
		}
		public byte[] getTarget_prefix() {
			return target_prefix;
		}
		public String getTARGETPREFIX_szTargetName() {
			return TARGETPREFIX_szTargetName;
		}
	}
	public class ImageElement{
		public class Hexline{
			final int type;
			final int adress;
			final int lenght;
			final int data[];
			boolean hexlineverbose;
			public Hexline(byte[] theline)
			{
				hexlineverbose=false;
				int sum = 0, len = 0, cksum = 0, num = 0, code = 0;
				int[] bytes;
				long addr = 0;
				int loc = 0;
				String linnestr = new String(theline);
				if (theline[0] != ':')
					;
				if ((theline.length) < 11)
					;
				loc++;
				Scanner scanner = new Scanner(linnestr.substring(loc, loc + 2));
				len = scanner.nextShort(16);
				scanner.close();
				if ((theline.length) < (11 + (len * 2)))
					;
				loc += 2;
				scanner = new Scanner(linnestr.substring(loc, loc + 4));
				addr = scanner.nextInt(16);
				scanner.close();
				loc += 4;
				scanner = new Scanner(linnestr.substring(loc, loc + 2));
				code = scanner.nextShort(16);
				scanner.close();
				loc += 2;
				sum = (len & 255) + ((int)(addr >>> 8) & 255) + (int)(addr & 255) + (code & 255);
				if(verbose && hexlineverbose) System.out.printf("  \n DFU image content: %04x %04x ", addr, len);
				bytes = new int[len];
				while (num != len)
				{
					scanner = new Scanner(linnestr.substring(loc, loc + 2));
					bytes[num] = scanner.nextShort(16);
					scanner.close();
					if(verbose && hexlineverbose) System.out.printf("%02x", bytes[num]);
					loc += 2;
					sum += bytes[num] & 255;
					num++;
					if (num >= 256){
						;
					}
				}
				scanner = new Scanner(linnestr.substring(loc, loc + 2));
				cksum = scanner.nextShort(16);
				scanner.close();
				if ((((sum & 0xFF) + (cksum & 0xFF)) & 0xFF) == 0)
					; /* checksum error */
				this.type = code;
				this.adress = (int)(addr & 0xFFFF);
				this.lenght = len;
				this.data = bytes;
			}
		}
		private byte[] image_element;
		private byte[] ImageElement_dwElementAddress = new byte[4], ImageElement_dwElementSize = new byte[4];
		void load_hexfile(String filename){
			final int increamentsize = 4096;
			int addr, n;
			int i, j, total = 0;
			int minaddr = 65536, maxaddr = 0;
			if ((filename.length()) == 0){
				if(verbose) System.out.printf("   Can't load a file without the filename.");
				if(verbose) System.out.printf("  '?' for help\n");
				return;
			}
			//extracting the image
			image_element = new byte[increamentsize];
			try {
				BufferedReader fhex = new BufferedReader(new FileReader(filename));
				while(true){
					try {
						Hexline hexline = new Hexline(fhex.readLine().getBytes());
						addr = hexline.adress;
						n = hexline.lenght;
						switch (hexline.type)
						{
						case 0:
							for (i = 0; i <= (n - 1); i++)
							{
								if(addr > image_element.length - 1){//size is not enough
									byte[] img_tmp = new byte[increamentsize + image_element.length];
									System.arraycopy(image_element, 0, img_tmp, 0, image_element.length);
									image_element = img_tmp;
								}
								image_element[addr] = (byte) (hexline.data[i] & 255);
								total++;
								if (addr < minaddr)
									minaddr = addr;
								if (addr > maxaddr)
									maxaddr = addr;
								addr++;
							}
							if(verbose && hexline.hexlineverbose) System.out.printf("saved\n");
							break;

						case 1:
							fhex.close();
							if(verbose) System.out.printf("End of file detected, ");
							System.out.printf("Loaded %d bytes. ", total);
							if(verbose) System.out.printf("Image size of hex: %08x\n", total);
							ImageElement_dwElementSize[3] = (byte) ((total >>> 24) & 0xFF);
							ImageElement_dwElementSize[2] = (byte) ((total >>> 16) & 0xFF);
							ImageElement_dwElementSize[1] = (byte) ((total >>> 8) & 0xFF);
							ImageElement_dwElementSize[0] = (byte) (total & 0xFF);
							byte[] img_tmp = new byte[total + 8];
							System.arraycopy(ImageElement_dwElementAddress, 0, img_tmp, 0, 4);
							System.arraycopy(ImageElement_dwElementSize, 0, img_tmp, 4, 4);
							System.arraycopy(image_element, 0, img_tmp, 8, total);
							image_element = img_tmp;
							return;

						case 4:
							if(verbose) System.out.printf("Start of file detected, code starting at adress ");
							for (j = 0; j < hexline.lenght; j++)
							{
								if(verbose) System.out.printf("%02x", hexline.data[j]);
								ImageElement_dwElementAddress[3 - j] = (byte) hexline.data[j];
							}
							ImageElement_dwElementAddress[0] = 0x00;
							ImageElement_dwElementAddress[1] = 0x00;
							if(verbose) System.out.printf("0000\n");
							break;
						case 5:
							//Start Linear Address:
							if(verbose) System.out.printf("Start Linear Address found.\n");
							if(verbose) System.out.printf("\tFor x86 CPU, run start at address: %02x%02x%02x%02x\n",
									hexline.data[0], hexline.data[1], hexline.data[2], hexline.data[3]);
							if(verbose) System.out.printf("\tFor ARM CPU, means reset handler address at: %02x%02x%02x%02x\n",
									hexline.data[0], hexline.data[1], hexline.data[2], hexline.data[3] & 0xfe);
							break;
						default:
							if(verbose) System.out.printf("error, bad type code : %d\n", hexline.type);
							break;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			} catch (FileNotFoundException e1) {
				if(verbose) System.out.printf("   Can't open file '%s' for reading.\n", filename);
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		public void load_bin(String filename, String offset) {
			try {
				File fbin = new File(filename);
				int total = (int) (fbin.length() & 0x7FFFFFFF);//limit file to 2GB size, that's fine
				image_element = new byte[total];
				DataInputStream dis = new DataInputStream(new FileInputStream(fbin));
				dis.readFully(image_element);
				dis.close();
				System.out.printf("Loaded %d bytes, ", total);
				ImageElement_dwElementSize[3] = (byte) ((total >>> 24) & 0xFF);
				ImageElement_dwElementSize[2] = (byte) ((total >>> 16) & 0xFF);
				ImageElement_dwElementSize[1] = (byte) ((total >>> 8) & 0xFF);
				ImageElement_dwElementSize[0] = (byte) (total & 0xFF);
				int ioffset = Integer.parseInt(offset, 16);
				System.out.printf("and will be placed to offset 0x%08x.\n", ioffset);
				ImageElement_dwElementAddress[3] = (byte) ((ioffset >>> 24) & 0xFF);
				ImageElement_dwElementAddress[2] = (byte) ((ioffset >>> 16) & 0xFF);
				ImageElement_dwElementAddress[1] = (byte) ((ioffset >>> 8) & 0xFF);
				ImageElement_dwElementAddress[0] = (byte) (ioffset & 0xFF);
				byte[] img_tmp = new byte[total + 8];
				System.arraycopy(ImageElement_dwElementAddress, 0, img_tmp, 0, 4);
				System.arraycopy(ImageElement_dwElementSize, 0, img_tmp, 4, 4);
				System.arraycopy(image_element, 0, img_tmp, 8, total);
				image_element = img_tmp;
			} catch (FileNotFoundException e) {
				if(verbose) System.out.printf("   Can't open file '%s' for reading.\n", filename);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public byte[] getImage_element() {
			return image_element;
		}
	}
	public void createdfu(String[] filelist, String[] offsetlist, String dfufile, String ProjectName){
		// TODO Auto-generated method stub
		int crc32 = 0xffffffff;
		int i = 0;
		DfuImage dfuimage;
		int increamentsize = 4096;
		if(filelist != null){
			if(offsetlist == null){
				if(verbose) System.out.printf("Loading hex file(s)\n");
				dfuimage = new DfuImage(ProjectName, filelist);
			} else {
				if(filelist.length == offsetlist.length){
					if(verbose) System.out.printf("Loading bin file(s)\n");
					dfuimage = new DfuImage("BrainCo_Focus1", filelist, offsetlist);
				} else
					return;
			}
		}else{
			return;
		}
		create_dfuprefix(dfuimage.getDfuimage().length + dfuimage.getTarget_prefix().length);
		create_dfusuffix(DFUSUFFIX_bcdDevice, DFUSUFFIX_idProduct, DFUSUFFIX_idVendor);
		data = new byte[increamentsize];
		int offset = 0;
		if(verbose) System.out.printf("DFU prefix:\n");
		for (i = 0; i < 11; i++)
		{
			if(verbose) System.out.printf("%02x ", dfu_prefix[i]);
			data[i + offset] = dfu_prefix[i];
		}
		offset += i;
		if(verbose) System.out.printf("\nDFU target prefix:\n");
		for (i = 0; i < 274; i++)
		{
			if(verbose && i < 11 + dfuimage.getTARGETPREFIX_szTargetName().length()){
				System.out.printf("%02x ", dfuimage.getTarget_prefix()[i]);
				if(i % 30 == 29)
					System.out.printf("\n");
			} else if(verbose && i == 11 + dfuimage.getTARGETPREFIX_szTargetName().length()){
				System.out.printf("\n\t\t--omited zeros--\n", dfuimage.getTarget_prefix()[i]);
			} else if(verbose && i >= 266){
				System.out.printf("%02x ", dfuimage.getTarget_prefix()[i]);
			}
			data[i + offset] = dfuimage.getTarget_prefix()[i];
		}
		offset += i;
		if(verbose) System.out.printf("\nDFU core image size %d\n", dfuimage.getDfuimage().length);
		for (i = 0; i < dfuimage.getDfuimage().length; i++)
		{
//			if(verbose) System.out.printf("%02x ", dfuimage.getDfuimage()[i]);
//			if(i%30 == 0)
//				System.out.printf("\n");
			if(i + offset > data.length - 1){//size is not enough
				byte[] img_tmp = new byte[increamentsize + data.length];
				System.arraycopy(data, 0, img_tmp, 0, data.length);
				data = img_tmp;
			}
			data[i + offset] = dfuimage.getDfuimage()[i];
		}
		offset += i;
		if(verbose) System.out.printf("DFU suffix:\n");
		for (i = 0; i < 12; i++)
		{
			if(verbose) System.out.printf("%02x ", dfu_suffix[i]);
			if(i + offset > data.length - 1){//size is not enough
				byte[] img_tmp = new byte[increamentsize + data.length];
				System.arraycopy(data, 0, img_tmp, 0, data.length);
				data = img_tmp;
			}
			data[i + offset] = dfu_suffix[i];
		}
		offset += i;
		/* compute crc32 */
		for (i = 0; i < offset; i++)
			crc32 = crc32_byte(crc32, data[i]);
		if(verbose) System.out.printf("\nTotal Size of dfu is %d, file CRC32 %08x  \n", offset, crc32);
		byte[] crc_result = new byte[4];
		crc_result[0] = (byte) (crc32 & 0xFF);
		crc_result[1] = (byte) ((crc32 >>> 8) & 0xFF);
		crc_result[2] = (byte) ((crc32 >>> 16) & 0xFF);
		crc_result[3] = (byte) ((crc32 >>> 24) & 0xFF);
		FileOutputStream fos;
		try {
			File outfile = new File(dfufile);
			if(verbose) System.out.printf("Writing file...");
			outfile.getParentFile().mkdirs();
			outfile.createNewFile();
			fos = new FileOutputStream(outfile);
			fos.write(data, 0, offset);
			fos.flush();
			fos.write(crc_result);
			fos.flush();
			fos.close();
			if(verbose) System.out.printf("finished\n");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args){
		System.out.printf("DfuSe file manager Java, Tianhe Wang, version 3.0\n");
		Filemgr flmgr = new Filemgr();
		boolean verbose = false;
		String[] filelist = null, offsets = null;
		String outputfile = null, project_name = new String("Focus1Series");
		
		Options options = new Options();

		Option option_verbose_option = new Option("V", "verbose", false, "Verbosely output prefixs and suffixs.");
		option_verbose_option.setRequired(false);
		options.addOption(option_verbose_option);

		Option option_output = new Option("o", "output", true, "Output file");
		option_output.setRequired(true);
		options.addOption(option_output);

		Option option_bin = new Option("bin", "binary", true, "Binary input files. Parameter should follow <binary file 1>@:<offset1>[,<binary file 2>@:<offset2>,...,<binary file n>@:<offsetn>]");
		option_bin.setRequired(false);
		options.addOption(option_bin);

		Option option_hex = new Option("ihex", "intel-hex", true, "intel hex input file");
		option_hex.setRequired(false);
		options.addOption(option_hex);

		Option option_prjname = new Option("p", "project-name", true, "Project name, as short as possible");
		option_prjname.setRequired(false);
		options.addOption(option_prjname);

		Option option_dfu_id = new Option("d", "dfu-id", true, "dfu ids, use column to seperate");
		option_dfu_id.setRequired(false);
		options.addOption(option_dfu_id);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			verbose = cmd.hasOption("verbose");
			outputfile = cmd.getOptionValue("output");
			if(cmd.hasOption("binary"))
			{
				String binary_options = cmd.getOptionValue("binary");
				String[] file_n_off = binary_options.split(",");
				filelist = new String[file_n_off.length];
				offsets = new String[file_n_off.length];
				for(int j = 0; j < file_n_off.length; j++){
					String[] bin_at_addr = file_n_off[j].split("@:", 2);
					filelist[j] = bin_at_addr[0];
					offsets[j] = bin_at_addr[1];
				}
			}
			else if(cmd.hasOption("intel-hex"))
			{
				filelist = new String[1];
				filelist[0] = cmd.getOptionValue("intel-hex");
			}
			else
			{
				throw new ParseException("Must have at least binary file or intel-hex file");
			}
			//set vendor-id & product-id
			int vid = 0x0483, pid = 0xdf11;
			if(cmd.hasOption("dfu-id"))
			{
				String[] vendor_product = cmd.getOptionValue("dfu-id").split(":", 2);
				if(!vendor_product[0].isEmpty()){
					vid = Integer.parseInt(vendor_product[0], 16);
					flmgr.setDFUSUFFIX_idVendor(vid);
				}
				if(vendor_product.length == 2 && !vendor_product[1].isEmpty()){
					pid = Integer.parseInt(vendor_product[1], 16);
					flmgr.setDFUSUFFIX_idProduct(pid);
				}
				if(verbose) System.out.printf("vid = %04x, pid = %04x\n", vid, pid);
			}
			//project-name field
			if(cmd.hasOption("project-name"))
			{
				project_name = cmd.getOptionValue("project-name");
			}
			if(verbose) System.out.printf("project-name is %s\n", project_name);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("Filemgr", options);
			System.exit(1);
		}

		flmgr.setVerbose(verbose);
		flmgr.createdfu(filelist, offsets, outputfile, project_name);
		System.out.printf("Successfully ended\n");
	}
}
