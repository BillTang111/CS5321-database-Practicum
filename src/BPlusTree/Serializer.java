package BPlusTree;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Serializer {
	private FileOutputStream fout;
	private FileChannel channel;
	private ByteBuffer buffer;
	private int pageSize = 4096;
	private int index;
	
	public Serializer(String input) {
		index = 0;
		try {
			fout = new FileOutputStream(input);
			channel = fout.getChannel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**write next leaf node*/
	public void writeNextNode(LeafNode leaf){
		buffer.putInt(0);
		buffer.putInt(leaf.getNum());
		index = 8;
		for (Map.Entry<Integer, List<DataEntry>> entry : leaf.getMap().entrySet()) {
			int key = entry.getKey();
			List<DataEntry> ridList = entry.getValue();
			buffer.putInt(key);
			buffer.putInt(ridList.size());
			index += 8;
			for (DataEntry pairs : ridList) {
				buffer.putInt(pairs.getPageId());
				buffer.putInt(pairs.getTupleId());
				index += 8;
			}
		}
		//fill the rest with 0
		while (index < pageSize) {
			buffer.putInt(0);
			index += 4;
		}
		buffer.flip();
		try {
			channel.write(buffer);
			if(buffer.hasRemaining()){
				buffer.compact();
			}else{
				buffer.clear();
			}
			index = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**write next index node*/
	public void writeNextNode(IndexNode i){
		buffer.putInt(1);
		buffer.putInt(i.getKeys().size());
		index=8;
		for(int key: i.getKeys()){
			buffer.putInt(key);
			index+=4;
		}
		if(i.isUpperLayer() == false){
			ArrayList<LeafNode> leafChildren = i.getLeafChildren();
			for(LeafNode leaf: leafChildren){
				if(leaf != null){
					buffer.putInt(leaf.getAddress());
					index += 4;
				}
			}
		}else{
			ArrayList<IndexNode> indexChildren = i.getIndexChildren();
			for(IndexNode in: indexChildren){
				if(in != null){
					buffer.putInt(in.getAddress());
					index += 4;
				}
			}
		}
		//fill the rest with 0
				while (index < pageSize) {
					buffer.putInt(0);
					index += 4;
				}
				buffer.flip();
				try {
					channel.write(buffer);
					if(buffer.hasRemaining()){
						buffer.compact();
					}else{
						buffer.clear();
					}
					index = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	
	

	public void writeHeadPage(int root, int leafNum, int order) {
		// TODO Auto-generated method stub
		buffer.putInt(root);
		buffer.putInt(leafNum);
		buffer.putInt(order);
		//fill the rest with 0
		for(int i=0; i< pageSize/4-3; i++){
			buffer.putInt(0);
		}
		buffer.flip();
		try {
			channel.write(buffer);
			if(buffer.hasRemaining()){
				buffer.compact();
			}else{
				buffer.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Close the index file.
	 */
	public void close() {
		try {
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
