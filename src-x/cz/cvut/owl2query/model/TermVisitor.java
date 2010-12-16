package cz.cvut.owl2query.model;


public interface TermVisitor {

    public void accept(GroundTerm term);

    public void accept(Variable term);

}
