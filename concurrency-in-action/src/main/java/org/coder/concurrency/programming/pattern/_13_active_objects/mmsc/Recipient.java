package org.coder.concurrency.programming.pattern._13_active_objects.mmsc;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Recipient implements Serializable {

    private static final long serialVersionUID = -3972590382077816924L;

    private Set<String> to = new HashSet<String>();

    public void addTo(String msisdn) {
        to.add(msisdn);
    }

    public Set<String> getToList() {
        return (Set<String>) Collections.unmodifiableCollection(to);
    }

}