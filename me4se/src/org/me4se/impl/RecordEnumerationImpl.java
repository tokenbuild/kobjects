package org.me4se.impl;

import javax.microedition.rms.*;


public class RecordEnumerationImpl implements RecordEnumeration {

    RecordStore store;
    RecordFilter filter;
    RecordComparator comparator; 
    boolean keepUpdated;

    int nextId = -1;
    int prevId = -1;
    int currentId = -1;

    public RecordEnumerationImpl (RecordStore store, 
				  RecordFilter filter, 
				  RecordComparator comparator, 
				  boolean keepUpdated) {

	this.store = store;
	this.filter = filter;
	this.comparator = comparator;
	this.keepUpdated = keepUpdated;

	if (comparator != null || keepUpdated) 
	    throw new RuntimeException 
		("comparator and keepupdated not supported yet!");
    }


    public void destroy () {
    }


    public boolean hasNextElement () {
	try {
	    if (nextId >= store.getNextRecordID ()) return false;
	    if (nextId != -1) return true;
	    
	    if (currentId == -1) currentId = 0;

	    nextId = currentId + 1;
	    
	    while (nextId < store.getNextRecordID ()) {
		byte [] data = store.getRecord (nextId);
		if (data != null 
		    && (filter == null 
			|| filter.matches (data))) return true;
		nextId++;
	    }
	    return false;
	}
	catch (RecordStoreException e) {
	    throw new RuntimeException (e.toString ());
	}
    }


    public boolean hasPreviousElement () {
	try {
	    if (prevId <= 0) return false;
	    if (prevId != -1) return true;
	    
	    if (currentId == -1) 
		currentId = store.getNextRecordID ();

	    prevId = currentId - 1;
	    
	    if (filter == null) return prevId > 0;
	    
	    while (prevId > 0) {
		byte [] data = store.getRecord (prevId);
		if (data != null 
		    && (filter == null 
			|| filter.matches (data))) 
		    return true;
		prevId--;
	    }
	    
	    return false;
	}
	catch (RecordStoreException e) {
	    throw new RuntimeException (e.toString ());
	}
    }


    public boolean isKeptUpdated () {
	return keepUpdated;
    }


    public void keepUpdated (boolean keepUpdated) {
	this.keepUpdated = keepUpdated;
	if (keepUpdated) throw new RuntimeException ("not supported yet!");
    }


    public byte [] nextRecord () {
	try {
	    return store.getRecord (nextRecordId ());
	}
	catch (RecordStoreException e) {
	    throw new RuntimeException (e.toString ());
	}
    }


    public int nextRecordId () {
	if (nextId == -1) {
	    hasNextElement ();
	}

	currentId = nextId;
	nextId = -1;
	prevId = -1;
	return currentId;
    }


    public int numRecords () {
	throw new RuntimeException ("not yet supported!");
    }

    public byte [] previousRecord () {
	try {
	    return store.getRecord (previousRecordId ());
	}
	catch (RecordStoreException e) {
	    throw new RuntimeException (e.toString ());
	}
    }
    
    
    public int previousRecordId () {
	if (prevId == -1) {
	    hasPreviousElement ();
	}

	currentId = prevId;
	nextId = -1;
	prevId = -1;
	return currentId;
    }

    public void rebuild () {
    }

    public void reset () {
	currentId = 0;
	nextId = -1;
	prevId = -1;
    }
}