package aterm;

public interface ATermList extends ATerm {

	boolean isEmpty();

	ATermList getNext();

	ATermAppl getFirst();

	ATermList append(ATermAppl rollEdgeIn);

	int getLength();

	ATermAppl elementAt(int i);

	ATermList concat(ATermList classes);

	ATermList insert(ATermAppl makeValue);

}
