package org.kobjects.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public abstract class AbstractNode implements TreeNode {

    AbstractNode parent;
    Vector children;
    JTree tree;


    protected AbstractNode (AbstractNode parent, boolean mayHaveChildren) {
	this.parent = parent;
	if (mayHaveChildren) 
	    children = new Vector ();
    }


    public abstract Component getComponent ();


    public JTree getTree () {
	if (tree != null) return tree;
	if (parent != null) return parent.getTree ();
	return null;
    }


    public TreePath getPath () {
	return parent == null 
	    ? new TreePath (this)
		: parent.getPath ().pathByAddingChild (this);
    }
	

    public void add (AbstractNode node, boolean select) {
	if (node.parent != this) 
	    throw new RuntimeException ("inconsistent parent");

	children.addElement (node);
	
	JTree t = getTree (); 

	if (t != null) {
	    DefaultTreeModel model = (DefaultTreeModel) t.getModel (); 

	    model.nodeChanged (this);
	    model.nodesWereInserted (this, new int[] {children.size ()-1});
	    
	    TreePath path = node.getPath ();
	    t.makeVisible (path);
	    t.setSelectionPath (path);
	}
    }
	/*
	public RootNode getRoot () {
	    AbstractNode node = this;
	    while (node.parent != null) node = node.parent;
	    return (RootNode) node;
	    }*/

    public Enumeration children () {
	return children == null 
	    ? new Vector ().elements () 
		: children.elements ();
    }
    
    public boolean getAllowsChildren () {
	return children != null;
    }
    
    public TreeNode getChildAt (int index) {
	return (TreeNode) children.elementAt(index);
    }
    

    public int getChildCount () {
	return children == null ? 0 : children.size ();
    }

    public int getIndex (TreeNode n) {
	return children == null ? -1 : children.indexOf (n);
    }
    
    public TreeNode getParent () {
	return parent;
    }

    public boolean isLeaf () {
	return children == null || children.size () == 0;
    }

    public void remove () {
	if (parent != null) {
	    int i = parent.children.indexOf (this);
	    parent.children.removeElementAt (i);
	    
	    JTree t = getTree ();
	    
	    if (t != null) {
		DefaultTreeModel dtm = (DefaultTreeModel) t.getModel ();
		
		dtm.nodesWereRemoved (parent, new int[] {i}, new Object[]{this});
		
		t.setSelectionPath (parent.getPath ());
	    }
	}
	
	if (children != null) 
	    for (int i = 0; i < children.size (); i++) 
		((AbstractNode) children.elementAt (i)).remove ();
    }


    protected void selected () {
    }
}
    




