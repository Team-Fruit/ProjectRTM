package net.teamfruit.projectrtm.ngtlib.block;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.teamfruit.projectrtm.ngtlib.io.NGTFileLoader;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.ngtlib.io.NGTText;

@Deprecated
public class NGTStructureBuilder {
	public class BlockSetSB {
		public final Block block;
		public final int metadata;

		public BlockSetSB(Block par1, int par2) {
			this.block = par1;
			this.metadata = par2;
		}
	}

	public static final NGTStructureBuilder instance = new NGTStructureBuilder();
	private static final Map<String, BlockSetSB[][][]> structureMap = new HashMap<String, BlockSetSB[][][]>();
	private static final ArrayList<String> structureList = new ArrayList<String>();
	private static final BlockSetSB airBlock = getBlockSetSB(Blocks.air, 0);

	public static BlockSetSB getBlockSetSB(Block par1, int par2) {
		return instance.new BlockSetSB(par1, par2);
	}

	public static void init() {
		NGTLog.debug("start loading NGTStructure");
		List<File> fileList = NGTFileLoader.findFile("", ".ngts", "");
		for (File file : fileList) {
			String[] strings = NGTText.readText(file);
			createBlockSetSB(strings);
		}
	}

	/**
	 * [Y][Z][X]
	 * */
	public static BlockSetSB[][][] getStructure(String name) {
		return structureMap.get(name);
	}

	public static ArrayList<String> getStructureNameList() {
		return structureList;
	}

	public static void createBlockSetSB(String[] stringArray) {
		String mode = "";
		String name = "";
		BlockSetSB[][][] floorList = new BlockSetSB[0][0][0];//y,z,x
		int[] floorArray = new int[0];
		Map<Character, BlockSetSB> blockMap = new HashMap<Character, BlockSetSB>();
		ArrayList<BlockSetSB[]> blockList = new ArrayList<BlockSetSB[]>();

		blockMap.put(' ', airBlock);

		for (String s : stringArray) {
			if (s.startsWith("#")) {
				if (s.startsWith("#Property")) {
					mode = "property";
				} else if (s.startsWith("#Block")) {
					mode = "block";
				} else if (s.startsWith("#Floor")) {
					mode = "floor";

					if (blockList.size()>0) {
						for (int i : floorArray) {
							floorList[i] = blockList.toArray(new BlockSetSB[blockList.size()][]);
						}
						blockList.clear();
					}

					String[] sA1 = s.substring(7).split(",");
					int l = sA1.length;
					floorArray = new int[l];
					for (int i = 0; i<l; ++i) {
						floorArray[i] = Integer.valueOf(sA1[i]);
					}
				} else if (s.startsWith("#Finish")) {
					if (blockList.size()>0) {
						for (int i : floorArray) {
							floorList[i] = blockList.toArray(new BlockSetSB[blockList.size()][]);
						}
						blockList.clear();
					}
					break;
				} else {
					NGTLog.debug("illegal string : "+s);
				}
			} else {
				if (mode.equals("property")) {
					if (s.startsWith("name")) {
						name = s.substring(5);
					} else if (s.startsWith("floor"))//高さ
					{
						int i0 = Integer.valueOf(s.substring(6));
						floorList = new BlockSetSB[i0][0][0];
					}
				} else if (mode.equals("block")) {
					char c = s.charAt(0);
					String[] sA2 = s.split(",");
					String blockName = sA2[0].substring(2);
					int meta = Integer.valueOf(sA2[1]);
					Block block = Block.getBlockFromName(blockName);
					if (block==null) {
						block = Blocks.air;
						NGTLog.debug("illegal block name : "+blockName);
					}
					blockMap.put(c, getBlockSetSB(block, meta));
				} else if (mode.equals("floor")) {
					BlockSetSB[] bs = new BlockSetSB[s.length()];
					for (int i = 0; i<s.length(); ++i) {
						char c = s.charAt(i);
						BlockSetSB block0 = blockMap.get(c);
						if (block0==null) {
							block0 = airBlock;
							NGTLog.debug("not found block : "+c);
						}
						bs[i] = block0;
					}
					blockList.add(bs);
				}
			}
		}

		structureMap.put(name, floorList);
		structureList.add(name);
		blockMap.clear();
		NGTLog.debug("load structure : "+name);
	}
}