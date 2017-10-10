package org.gal.tools.cvs.comparators;

import java.util.Comparator;

import org.eclipse.core.resources.IResource;

public class IResourceCompareByPath implements Comparator<IResource>{

	@Override
	public int compare(IResource o1, IResource o2) {
		return o1.getFullPath().toPortableString().compareTo(o2.getFullPath().toPortableString());
	}

}
