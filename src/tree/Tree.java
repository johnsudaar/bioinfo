package tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tree<T> {

	private HashMap<String, T> nodes;
	int size = -1;

	public Tree() {
		this.nodes = new HashMap<String, T>();
	}

	public boolean contains(String node) {
		return this.nodes.containsKey(node);
	}

	public boolean add(String node, T obj) {
		if (!this.contains(node)) {
			this.nodes.put(node, obj);
			return true;
		} else {
			return false;
		}
	}

	public T get(String node) {
		return this.nodes.get(node);
	}

	public Object[] nodes() {
		return this.nodes.keySet().toArray();
	}
	
	public Object[] activatedNodes() {
		List<Object> res = new ArrayList<>();
		for(String key : nodes.keySet()) {
			Object curr = nodes.get(key);
			if(curr.getClass().equals(Organism.class) && ((Organism) curr).getActivated()) {
				res.add(key);
			}
			else if(!curr.getClass().equals(Organism.class)) {
				res.add(key);
			}
		}
		return res.toArray();
	}

	public void printTree_aux(int level) {
		if (this != null) {
			Object[] nodess = this.nodes();
			for (Object node : nodess) {
				for (int i = 0; i < level; i++) {
					System.out.print("-");
				}
				System.out.println(node);

				if (this.get((String) node) != null) {
					((Tree<?>) this.get((String) node)).printTree_aux(level + 1);
				}
			}
		}
	}

	public void printTree() {
		printTree_aux(0);
	}

	public int computeSize() {
		int size = 0;
		for (Object a : this.nodes()) {
			String node = (String) a;
			if (this.isLeaf(node)) {
				Organism o = (Organism) this.get(node);
				size += o.size();
			} else {
				Tree t = (Tree) this.get(node);
				size += t.size();
			}
		}
		this.size = size;
		return size;
	}

	public int size() {
		return this.computeSize();
	}

	public boolean isLeaf(String node) {
		return !(this.nodes.get(node) instanceof Tree);
	}

}
