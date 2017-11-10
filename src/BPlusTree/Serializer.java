package BPlusTree;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**This class build a serializer for the B+ tree
 * 
 * author: Lini Tan lt398
 * */
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
			this.writeHeadPage(0, 0, 0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**@param leaf: the leaf node we want to write
	 * 
	 * write next leaf node*/
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
	
	
	
/**write the head page
 * @param root: the location of the root 
 * @param leafNum: the num of leaf
 * @param order: the order size
 * */
	public void writeHeadPage(int root, int leafNum, int order) {
		// TODO Auto-generated method stub
		GoPage(0);
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
	 * @param: Close the index file.
	 */
	public void close() {
		try {
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param rewrite the pages from the given index*/
	public void GoPage(int pageIndex){
		try {
			channel.position((long)pageIndex*pageSize);
			buffer=ByteBuffer.allocate(pageSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
