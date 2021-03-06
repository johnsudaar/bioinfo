package manager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tree.Organism;
import tree.Tree;

public class TreeWalker {
	private Queue<Organism> organisms;
	private static Lock mainLock;

	public TreeWalker(Tree t){
		mainLock = new ReentrantLock();
		mainLock.lock();
		this.organisms = new ConcurrentLinkedDeque<Organism>();
		this.toQueue(t);
		mainLock.unlock();
	}
	
	private void toQueue(Tree t){
		for(Object o : t.activatedNodes()){
			if(t.isLeaf((String)o)){
				this.organisms.add((Organism)t.get((String)o));
			} else {
				toQueue((Tree) t.get((String)o));
			}
		}
	}
	
	public boolean hasNext(){
		mainLock.lock();
		boolean res = ! this.organisms.isEmpty();
		mainLock.unlock();
		return res;
	}
	
	public Organism next(){
		mainLock.lock();
		Organism res = this.organisms.poll();
		mainLock.unlock();
		return res;
	}
	
}
